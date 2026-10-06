package org.bci.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.bci.base.BaseClass;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;

public class ExtentListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(ExtentListener.class);
    
    private static final ThreadLocal<ExtentReports> extentReportThread = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> testReport = new ThreadLocal<>();

    private static String getReportName(ITestContext context) {
        String dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
        String browser = "Browser";
        
        try {
            if (context != null && context.getCurrentXmlTest() != null) {
                String xmlBrowser = context.getCurrentXmlTest().getParameter("browser");
                if (xmlBrowser != null && !xmlBrowser.isEmpty()) {
                    browser = xmlBrowser.toUpperCase();
                } else {
                    browser = context.getName().replaceAll("[^a-zA-Z0-9]", "_");
                }
            }
        } catch (Exception ignored) {}

        return "TestExecutionReport_" + browser + "_" + dateFormat + ".html";
    }

    private static ExtentReports createInstance(ITestContext context) {
        String reportPath = System.getProperty("user.dir") + "/reports/" + getReportName(context);
        
        File dir = new File(System.getProperty("user.dir") + "/reports/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Automation Execution Report - Playwright");
        sparkReporter.config().setReportName("Functional Test Results");
        sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("Automation Tool", "Playwright Java");

        String browser = "Chrome";
        try {
            if (context != null && context.getCurrentXmlTest() != null) {
                String xmlBrowser = context.getCurrentXmlTest().getParameter("browser");
                if (xmlBrowser != null && !xmlBrowser.isEmpty()) {
                    browser = xmlBrowser;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract browser parameter: {}", e.getMessage());
        }
        extentReports.setSystemInfo("Browser", browser.toUpperCase());

        return extentReports;
    }

    @Override
    public synchronized void onStart(ITestContext context) {
        logger.info("Test Suite/Block Started: {}", context.getName());
        ExtentReports extent = createInstance(context);
        extentReportThread.set(extent);
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
   
        String description = result.getMethod().getDescription();
        String testId = "";
        
        try {
            TestDetails details = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestDetails.class);
            if (details != null) {
                if (!details.id().trim().isEmpty()) {
                    testId = "[" + details.id() + "] ";
                }
                
                final String fId = details.id();
                final String fAuthor = details.author();
                final String fPriority = details.priority().name();
                final String fSeverity = details.severity().name().toLowerCase();
                final String fDevice = details.device();

                Allure.label("severity", fSeverity);
                Allure.parameter("Test ID", fId);
                Allure.parameter("Author", fAuthor);
                Allure.parameter("Priority", fPriority);
                Allure.parameter("Device", fDevice);
            }
        } catch (Exception e) {
            logger.debug("Could not extract TestDetails annotation for Allure: {}", e.getMessage());
        }

        String testDisplayName = (description != null && !description.isEmpty()) 
                               ? testId + description 
                               : className + " :: " + methodName;

        ExtentReports extent = extentReportThread.get();
        if (extent != null) {
            ExtentTest test = extent.createTest(testDisplayName, "Class: " + className + " | Method: " + methodName);
            testReport.set(test);
        }
        
        try {
            TestDetails details = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestDetails.class);
            if (details != null && testReport.get() != null) {
                ExtentTest test = testReport.get();
                test.assignCategory("ID: " + details.id());
                test.assignCategory("Priority: " + details.priority());
                test.assignCategory("Severity: " + details.severity());
                test.assignAuthor(details.author());
                test.assignDevice(details.device());
            }
        } catch (Exception e) {
            logger.debug("Could not extract TestDetails annotation: {}", e.getMessage());
        }
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        ExtentTest test = testReport.get();
        if (test != null) {
            test.log(Status.PASS, "Test Passed");
        }
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        ExtentTest test = testReport.get();
        if (test != null) {
            test.log(Status.FAIL, result.getThrowable());

            // 1. Capture and Attach Failure Screenshot
            byte[] screenshotBytes = captureScreenshotAsBytes();
            if (screenshotBytes != null) {
                String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
                test.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
                
                // Safe Allure attachment
                Allure.attachment("Failure Screenshot", new ByteArrayInputStream(screenshotBytes));
            } else {
                test.warning("Failed to capture screenshot for the failed test.");
            }
        }
        
        // 2. Capture and Attach Recorded Video on Failure
        attachVideoSafe(result, "failed_videos");
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        ExtentTest test = testReport.get();
        if (test != null) {
            test.log(Status.SKIP, "Test Skipped: " + result.getThrowable());
        }
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        logger.info("Test Suite/Block Finished: {}", context.getName());
        ExtentReports extent = extentReportThread.get();
        if (extent != null) {
            extent.flush();
        }
        extentReportThread.remove();
        testReport.remove();
    }

   

    private void attachVideoSafe(ITestResult result, String statusFolder) {
        try {
            String methodName = result.getMethod().getMethodName();
            String targetDir = System.getProperty("user.dir") + "/target/" + statusFolder + "/";
            
            File folder = new File(targetDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Polling loop: Wait up to 8 seconds for Playwright to finish writing the file
            File[] matchingFiles = null;
            int retries = 0;
            while (retries < 16) {
                matchingFiles = folder.listFiles((dir, name) -> 
                    name.contains(methodName) && name.endsWith(".webm")
                );
                
                if (matchingFiles != null && matchingFiles.length > 0) {
                    Arrays.sort(matchingFiles, Comparator.comparingLong(File::lastModified));
                    File latestVideo = matchingFiles[matchingFiles.length - 1];
                    if (latestVideo.length() > 0) {
                        break;
                    }
                }
                Thread.sleep(500);
                retries++;
            }
            
            if (matchingFiles != null && matchingFiles.length > 0) {
                Arrays.sort(matchingFiles, Comparator.comparingLong(File::lastModified));
                File latestVideo = matchingFiles[matchingFiles.length - 1];
                
                byte[] videoBytes = Files.readAllBytes(latestVideo.toPath());
                
                if (videoBytes.length == 0) {
                    logger.warn("Found video file, but it is empty (0 bytes): {}", latestVideo.getName());
                    return;
                }

                // 1. Attach Video to Extent Report (HTML5 Video Player)
                String base64Video = Base64.getEncoder().encodeToString(videoBytes);
                String videoHtml = "<video width='320' height='240' controls>" +
                                   "<source src='data:video/webm;base64," + base64Video + "' type='video/webm'>" +
                                   "Your browser does not support the video tag." +
                                   "</video>";

                ExtentTest currentTest = testReport.get();
                if (currentTest != null) {
                    currentTest.info("Recorded Test Video: <br>" + videoHtml);
                    logger.info("Successfully attached video to ExtentReports for method: {}", methodName);
                }

                // 2. Attach Video to Allure Report using the universally compatible 2-arg method
                Allure.attachment("Recorded Video", new ByteArrayInputStream(videoBytes));
                logger.info("Successfully attached video to Allure for method: {}", methodName);

            } else {
                logger.warn("No matching video files found for method: '{}' in path: {}", methodName, targetDir);
            }
        } catch (Exception e) {
            logger.error("Failed to attach video to Report for method: {}", result.getMethod().getMethodName(), e);
        }
    }

    private byte[] captureScreenshotAsBytes() {
        try {
            Page page = BaseClass.getPage(); 
            if (page == null) {
                logger.warn("Playwright Page instance is null. Cannot capture screenshot.");
                return null;
            }

            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

        } catch (Exception e) {
            logger.error("Exception occurred while taking Playwright screenshot: {}", e.getMessage());
            return null;
        }
    }

    public static ExtentTest extentTest() {
        return testReport.get();
    }

    @Override
    public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not implemented yet
    }
}