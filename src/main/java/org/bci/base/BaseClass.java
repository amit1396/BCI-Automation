package org.bci.base;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;

public class BaseClass {

    // ================= THREAD LOCAL MANAGEMENT (ISOLATED PER THREAD) =================
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserInstance = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();
    private static final ThreadLocal<String> currentBrowser = new ThreadLocal<>();

    protected static ExtentReports extent;
    protected static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static Logger logger;
    private static Properties prop;

    // ================= PAGE & CONTEXT GETTERS =================
    public static Page getPage() {
        return page.get();
    }

    public static BrowserContext getContext() {
        return context.get();
    }

    protected void saveStorageStateToFile() {
        try {
            if (getContext() != null) {
                String statePath = "auth.json";
                getContext().storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(statePath)));
                logger.info("Playwright storage state (cookies/local storage) saved to file");
            }
        } catch (Exception e) {
            logger.warn("Failed to save storage state: " + e.getMessage());
        }
    }

    protected boolean loadStorageStateFromFile() {
        File stateFile = new File("auth.json");
        return stateFile.exists();
    }

    // ================= LOAD CONFIG =================
    @BeforeSuite(alwaysRun = true)
    public void initConfig() {
        try {
            logger = LogManager.getLogger(BaseClass.class);

            setProp(new Properties());
            String path = System.getProperty("user.dir") + "/src/test/resources/config.properties";

            try (FileInputStream fis = new FileInputStream(path)) {
                getProp().load(fis);
            }

            // For the Debug Purpose
//			getProp().forEach((k, v) -> System.out.println("DEBUG CONFIG -> [" + k + "] = [" + v + "]"));

            validateConfig();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private void validateConfig() {
        String applicationUrl = getProp().getProperty("url", "").trim();
        if (applicationUrl.isEmpty()) {
            throw new RuntimeException("'url' is missing in config.properties");
        }

        String username = getSecureConfigValue("demo.username", "DEMO_USERNAME", "username");
        String password = getSecureConfigValue("demo.password", "DEMO_PASSWORD", "password");

        validateSecureValue(username, "Main application username");
        validateSecureValue(password, "Main application password");
    }

    protected void checkServerAvailability() {
        try {
            String baseUrl = getProp().getProperty("url");
            if (baseUrl.contains("#")) {
                baseUrl = baseUrl.split("#")[0];
            }

            URL url = URI.create(baseUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.connect();

            int code = connection.getResponseCode();

            // Actually drain the stream so we know the server finished sending something
            try (var in = connection.getInputStream()) {
                in.readAllBytes();
            } catch (Exception readEx) {
                logger.warn("Server responded but body read failed: " + readEx.getMessage());
            }

            if (code != 200) {
                logger.warn("Server not ready. Status code: " + code);
                throw new SkipException("Server health check failed. Status code: " + code);
            } else {
                logger.info("Server is reachable and responded fully. Status code: " + code);
            }
        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            logger.warn("Server is not reachable: " + e.getMessage());
            throw new SkipException("Server is not reachable: " + e.getMessage());
        }
    }

    // ================= BEFORE EACH TEST =================
    @Parameters({ "browser", "headless" })
    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest(
            @Optional("chrome") String browserName,
            @Optional("") String xmlHeadless) {

        currentBrowser.set(browserName);
        checkServerAvailability();

        try {
            boolean headless;
            if (xmlHeadless != null && !xmlHeadless.trim().isEmpty()) {
                headless = Boolean.parseBoolean(xmlHeadless);
                logger.info("Headless mode loaded from TestNG XML: " + headless);
            } else {
                headless = Boolean.parseBoolean(getProp().getProperty("headless", "false"));
                logger.info("Headless mode loaded from config.properties: " + headless);
            }
            // ============================================================

            Playwright pw = Playwright.create();
            playwright.set(pw);

            Browser browser = createBrowser(pw, browserName, headless);
            browserInstance.set(browser);

            boolean hasAuthState = loadStorageStateFromFile();

            // Configure context options with dynamic screen/viewport settings and ignore HTTPS errors
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setIgnoreHTTPSErrors(true) // HTTP / SSL sites
                    .setRecordVideoDir(Paths.get("videos/"))
                    .setRecordVideoSize(1280, 720);

            if (!headless) {
                contextOptions.setViewportSize(null);
            } else {
                contextOptions.setViewportSize(1920, 1080);
            }

            BrowserContext ctx;
            if (hasAuthState) {
                contextOptions.setStorageStatePath(Paths.get("auth.json"));
                ctx = browser.newContext(contextOptions);
                context.set(ctx);
                logger.info("Initialized context with saved auth state and video recording.");
            } else {
                ctx = browser.newContext(contextOptions);
                context.set(ctx);
                logger.info("Initialized fresh browser context with video recording.");
            }

            // ================= START PLAYWRIGHT TRACING =================
            ctx.tracing().start(new com.microsoft.playwright.Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            logger.info("Playwright tracing started.");
            // ============================================================

            Page testPage = getContext().newPage();
            testPage.setDefaultTimeout(60000);
            testPage.setDefaultNavigationTimeout(60000);

            // === ADD THESE LISTENERS TO CATCH BLANK SCREEN / JS ERRORS ===
            testPage.onConsoleMessage(msg -> {
                if ("error".equals(msg.type())) {
                    logger.error("BROWSER CONSOLE ERROR: " + msg.text());
                }
            });

            testPage.onPageError(error -> {
                logger.error("UNCAUGHT PAGE ERROR: " + error);
            });

            testPage.onResponse(response -> {
                if (!response.ok() && response.status() >= 400 && response.url().contains("98.70.13.78")) {
                    logger.warn("FAILED ASSET/API REQUEST: " + response.url() + " [Status: " + response.status() + "]");
                }
            });
            // ============================================================

            page.set(testPage);

            String targetUrl = getProp().getProperty("url").trim();

            logger.info("Launching browser: " + browserName);
            logger.info("Navigating to: " + targetUrl);

            try {
                testPage.navigate(
                        targetUrl,
                        new Page.NavigateOptions()
                                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                                .setTimeout(60000)
                );

                try {
                    testPage.waitForLoadState(LoadState.NETWORKIDLE,
                            new Page.WaitForLoadStateOptions().setTimeout(15000));
                } catch (Exception idleEx) {
                    logger.warn("Page did not reach NETWORKIDLE within 15s, continuing anyway: " + idleEx.getMessage());
                }

                logger.info("Navigation committed and DOM loaded.");
                logger.info("Current URL: " + testPage.url());

            } catch (Exception e) {
                logger.error("Failed to navigate to: " + targetUrl, e);
                try { captureScreenshot("NAVIGATION_FAILED"); } catch (Exception ignored) {}
                throw new RuntimeException("Unable to open application URL: " + targetUrl, e);
            }

            try {
                Locator usernameInput = testPage.locator("input[name='username']");

                usernameInput.waitFor(
                        new Locator.WaitForOptions()
                                .setTimeout(45000)
                                .setState(WaitForSelectorState.VISIBLE)
                );

                logger.info("Login page is ready.");

            } catch (Exception e) {
                logger.error("Login page element not found. Current URL: " + testPage.url(), e);

                // Extra diagnostics before failing, so the log tells you WHY it's blank.
                try {
                    String bodySnippet = testPage.evaluate("() => document.body ? document.body.innerText.substring(0, 500) : 'NO BODY'").toString();
                    logger.error("Page body text snapshot (first 500 chars): " + bodySnippet);
                } catch (Exception evalEx) {
                    logger.warn("Could not read page body for diagnostics: " + evalEx.getMessage());
                }

                try {
                    int scriptCount = ((Number) testPage.evaluate("() => document.scripts.length")).intValue();
                    logger.error("Number of <script> tags on page: " + scriptCount);
                } catch (Exception evalEx) {
                    logger.warn("Could not count scripts: " + evalEx.getMessage());
                }

                try {
                    captureScreenshot("LOGIN_PAGE_NOT_READY");
                } catch (Exception ignored) {}

                throw new RuntimeException("Application opened but login page elements failed to render. Current URL: " + testPage.url(), e);
            }

            boolean loggedIn = false;
            if (hasAuthState) {
                try {
                    testPage.waitForSelector("a:has-text('Sign In')", new Page.WaitForSelectorOptions().setTimeout(5000));
                    loggedIn = true;
                    logger.info("Session reused successfully via storage state.");
                } catch (Exception e) {
                    logger.info("Session expired or invalid. Fresh login required.");
                }
            }

            if (!loggedIn) {
                logger.info("Performing fresh login setup...");
                String username = getSecureConfigValue("demo.username", "DEMO_USERNAME", "username");
                String password = getSecureConfigValue("demo.password", "DEMO_PASSWORD", "password");
                saveStorageStateToFile();
            }

        } catch (Exception e) {
            logger.error("Setup failed. Aborting test.", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    // ================= BROWSER INITIALIZATION =================
    private Browser createBrowser(Playwright pw, String browserName, boolean headless) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless) .setSlowMo(1000)  ;

        // We can use the .setSlowMo(1000);

        List<String> args = new ArrayList<>(Arrays.asList(
                "--start-maximized",
                "--no-first-run",
                "--no-default-browser-check",
                "--disable-infobars",
                "--disable-session-crashed-bubble",
                "--ignore-certificate-errors",
                "--allow-running-insecure-content",
                "--disable-web-security",
                "--unsafely-treat-insecure-origin-as-secure=http://98.70.13.78:1145",
                "--disable-features=IsolateOrigins,site-per-process",
                "--no-sandbox",
                "--disable-setuid-sandbox",
                "--disable-blink-features=AutomationControlled"
        ));

        options.setArgs(args);

        switch (browserName.toLowerCase()) {
            case "chrome":
            case "edge":
                options.setChannel(browserName.equalsIgnoreCase("edge") ? "msedge" : "chrome");
                return pw.chromium().launch(options);
            case "firefox":
                return pw.firefox().launch(options);
            case "safari":
            case "webkit":
                return pw.webkit().launch(options);
            default:
                throw new RuntimeException("Unsupported browser: " + browserName);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        unloadThreads();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        unloadThreads();
    }

    public static void unloadThreads() {
        try {
            if (getPage() != null && !getPage().isClosed()) {
                getPage().close();
            }
        } catch (Exception ignored) {}

        try {
            if (getContext() != null) {
                getContext().close();
            }
        } catch (Exception ignored) {}

        try {
            if (browserInstance.get() != null) {
                browserInstance.get().close();
            }
        } catch (Exception ignored) {}

        try {
            if (playwright.get() != null) {
                playwright.get().close();
            }
        } catch (Exception ignored) {}

        page.remove();
        context.remove();
        browserInstance.remove();
        playwright.remove();
        currentBrowser.remove();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpAfterEachTest(ITestResult result) {
        try {
            if (getContext() != null) {
                try {
                    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String traceDir = System.getProperty("user.dir") + "/target/traces/";
                    File dir = new File(traceDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String tracePath = traceDir + result.getName() + "_" + time + ".zip";

                    getContext().tracing().stop(new com.microsoft.playwright.Tracing.StopOptions()
                            .setPath(Paths.get(tracePath)));
                    logger.info("Playwright trace saved to: " + tracePath);

                    if (result.getStatus() == ITestResult.FAILURE && extentTest.get() != null) {
                        extentTest.get().info("Playwright Trace File: " + tracePath);
                    }
                } catch (Exception traceEx) {
                    logger.warn("Failed to stop/save Playwright trace: " + traceEx.getMessage());
                }
            }

            if (result.getStatus() == ITestResult.FAILURE) {
                try {
                    if (getPage() != null && !getPage().isClosed()) {
                        String screenshotPath = captureScreenshot("FAILED_" + result.getName());
                        logger.info("Failure screenshot saved: " + screenshotPath);
                    }
                } catch (Exception e) {
                    logger.warn("Failure screenshot failed: " + e.getMessage());
                }
            }

            handleTestVideo(result);

        } catch (Exception e) {
            logger.error("AfterMethod cleanup failed for test: " + result.getName(), e);
        } finally {
            closeAndResetBrowser("Test method completed: " + result.getName());
        }
    }

    private void handleTestVideo(ITestResult result) {
        try {
            Page currentPage = getPage();
            if (currentPage != null && !currentPage.isClosed() && currentPage.video() != null) {
                Path sourceVideoPath = currentPage.video().path();

                if (getContext() != null) {
                    getContext().close();
                }

                if (sourceVideoPath != null && sourceVideoPath.toFile().exists()) {
                    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String statusFolder = (result.getStatus() == ITestResult.FAILURE) ? "failed_videos" : "passed_videos";

                    String destDir = System.getProperty("user.dir") + "/target/" + statusFolder + "/";
                    File dir = new File(destDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String destPath = destDir + result.getName() + "_" + time + ".webm";
                    File destFile = new File(destPath);

                    sourceVideoPath.toFile().renameTo(destFile);
                    logger.info("Test video saved to: " + destPath);

                    if (extentTest.get() != null) {
                        if (result.getStatus() == ITestResult.FAILURE) {
                            extentTest.get().fail("Test Failed. Recorded Video:",
                                    com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath(destPath).build());
                        } else {
                            extentTest.get().pass("Test Passed. Recorded Video:",
                                    com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath(destPath).build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to process test video or attach to report: " + e.getMessage());
        }
    }

    private void closeAndResetBrowser(String reason) {
        try {
            if (browserInstance.get() != null) {
                browserInstance.get().close();
                logger.info("Browser closed. Reason: " + reason);
            }
        } catch (Exception e) {
            logger.warn("Browser close failed. Reason: " + reason + ". Error: " + e.getMessage());
        } finally {
            unloadThreads();
        }
    }

    // ================= SCREENSHOT =================
    public static String captureScreenshot(String testName) {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = System.getProperty("user.dir") + "/screenshots/" + testName + "_" + time + ".png";

        try {
            if (getPage() != null && !getPage().isClosed()) {
                File file = new File(path);
                file.getParentFile().mkdirs();
                getPage().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true));
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: " + e.getMessage());
        }
        return path;
    }

    protected String getSecureConfigValue(String systemPropertyName, String environmentVariableName, String configPropertyName) {
        String value = System.getProperty(systemPropertyName, "").trim();
        if (!value.isEmpty()) {
            return value;
        }

        value = System.getenv(environmentVariableName);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        return getProp().getProperty(configPropertyName, "").trim();
    }

    protected void validateSecureValue(String value, String valueName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(valueName + " is not configured.");
        }
    }

    public static String getBrowserName() {
        return currentBrowser.get() != null ? currentBrowser.get() : "Unknown";
    }

    public static Properties getProp() {
        return prop;
    }

    public static void setProp(Properties prop) {
        BaseClass.prop = prop;
    }
}