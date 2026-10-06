package org.bci.pageobjects.optivusloginmodule;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bci.base.BaseClass;
import org.bci.base.BasePage;

public class LoginPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    // Locators
    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator signInButton;
    private final Locator dashboardField;

    // Login page context verification
    private final Locator appLogo;
    private final Locator loginHeading;
    private final Locator usernameLabel;
    private final Locator passwordLabel;
    private final Locator forgotPasswordLink;
    private final Locator footerText;

    private final Locator errorBanner;
    private final Locator forgotPasswordScreen;
    private final Locator passwordToggleBtn;

    // Constructor
    public LoginPage() {
        super(BaseClass.getPage());
        Page page = BaseClass.getPage();

        // Initialize locators
        this.usernameField = page.locator("input[name='username']");
        this.passwordField = page.locator("input[name='password']");
        this.signInButton = page.locator("button.login-button[type='submit']");
        this.dashboardField = page.locator(".brand-text");

        this.appLogo = page.locator(".brand-logo, img[alt*='BCI logo']");
        this.loginHeading = page.locator("h1, h1.login-title");
        this.usernameLabel = page.locator("label[for='login-username']");
        this.passwordLabel = page.locator("label[for='login-password']");
        this.forgotPasswordLink = page.locator("a.forgot-link[routerlink='/forgot-password']");
        this.footerText = page.locator("p.login-footer");

        this.errorBanner = page.locator("div.error-banner span");
        this.forgotPasswordScreen = page.locator("p.form-sub");
        this.passwordToggleBtn = page.locator("button.password-toggle, .eye-icon, [aria-label*='password']");
    }

    public boolean isAppLogoVisible() {
        return isElementVisibleAndVerified(appLogo, "App/Client Name Logo");
    }

    public boolean isLoginHeadingVisible() {
        return isElementVisibleAndVerified(loginHeading, "Login Page Heading");
    }

    public boolean isUsernameLabelVisible() {
        return isElementVisibleAndVerified(usernameLabel, "Username Field Label");
    }

    public boolean isPasswordLabelVisible() {
        return isElementVisibleAndVerified(passwordLabel, "Password Field Label");
    }

    public boolean isSignInButtonVisible() {
        return isElementVisibleAndVerified(signInButton, "Sign In Button");
    }

    public boolean isForgotPasswordVisible() {
        return isElementVisibleAndVerified(forgotPasswordLink, "Forgot Password Link");
    }

    public boolean isFooterTextVisible() {
        return isElementVisibleAndVerified(footerText, "Footer Text / Copyright");
    }

    public String getUsernamePlaceholder() {
        return usernameField.getAttribute("placeholder");
    }

    public String getPasswordPlaceholder() {
        return passwordField.getAttribute("placeholder");
    }

    public String getSignInButtonText() {
        return signInButton.textContent().trim();
    }

    public String getForgotPasswordText() {
        return forgotPasswordLink.textContent().trim();
    }

    public String getFooterText() {
        return footerText.textContent().trim();
    }

    public boolean isSignInButtonHoverable() {
        return verifyButtonHoverEffect(signInButton);
    }

    public void enterUsername(String username) {
        logger.info("Entering username: {}", username);
        safeClick(usernameField);
        commonSendKeys(usernameField, username);
    }

    public void enterPassword(String password) {
        logger.info("Entering password......");
        safeClick(passwordField);
        commonSendKeys(passwordField, password);
    }

    public void clickSignIn() {
        logger.info("Clicking Sign In button");
        safeClick(signInButton);
        logger.info("Sign In button clicked successfully");
    }

    public boolean isDashboardVisible() {
        return isElementVisibleAndVerified(dashboardField, "Dashboard Title Visible After Login");
    }

    public boolean login(String username, String password) {
        logger.info("Starting login process with user: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickSignIn();

        logger.info("Login actions completed. Checking visibility of dashboard.");
        return isDashboardVisible();
    }

    public boolean isLoginUrlCorrect(String expectedUrlSubstring) {
        logger.info("Waiting for URL to contain: {}", expectedUrlSubstring);
        try {
            // Wait up to 5 seconds for the URL to update
            page.waitForURL(url -> url.contains(expectedUrlSubstring),
                    new Page.WaitForURLOptions().setTimeout(5000));
            logger.info("Current URL verified successfully: {}", page.url());
            return true;
        } catch (Exception e) {
            logger.error("URL did not update to contain '{}'. Current URL: {}", expectedUrlSubstring, page.url());
            return false;
        }
    }

    public boolean isErrorBannerVisible() {
        return isElementVisibleAndVerified(errorBanner, "Error Banner Message");
    }

    public String getErrorMessage() {
        return errorBanner.textContent().trim();
    }

    public boolean isSignInButtonDisabled() {
        boolean isDisabled = signInButton.isDisabled();
        logger.info("Checking if Sign In button is disabled: {}", isDisabled);
        return isDisabled;
    }

    public boolean isSignInButtonEnabled() {
        boolean isEnabled = signInButton.isEnabled();
        logger.info("Checking if Sign In button is enabled: {}", isEnabled);
        return isEnabled;
    }

    public void clickSignInWhenDisabled() {
        logger.info("Attempting to click disabled Sign In button");
        try {
            signInButton.click(new Locator.ClickOptions().setTimeout(2000));
        } catch (Exception e) {
            logger.info("Playwright correctly prevented clicking the disabled button: {}", e.getMessage());
        }
    }

    public void clearUsername() {
        logger.info("Clearing username field");
        usernameField.clear();
    }

    // Method to verify dashboard visibility in a newly opened tab/page context
    public boolean isDashboardVisibleInTab(Page targetTab) {
        logger.info("Waiting for dashboard visibility in the new tab...");
        try {
            // Create locator bound to the specific target tab
            Locator tabDashboardField = targetTab.locator(".brand-text");

            // Explicitly wait for it to load with a timeout
            tabDashboardField.waitFor(new Locator.WaitForOptions().setTimeout(10000));

            boolean isVisible = tabDashboardField.isVisible();
            logger.info("Dashboard visibility status in new tab: {}", isVisible);
            return isVisible;
        } catch (Exception e) {
            logger.error("Dashboard element did not appear in the new tab: {}", e.getMessage());
            return false;
        }
    }

    public void clickForgotPassword() {
        logger.info("Clicking on Forgot Password link");
        safeClick(forgotPasswordLink);
        logger.info("Forgot Password link clicked successfully");
    }

    public boolean isForgotPasswordScreenVisible() {
        logger.info("Verifying if Forgot Password screen is visible...");
        try {
            forgotPasswordScreen.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            boolean isVisible = forgotPasswordScreen.isVisible();
            logger.info("Forgot Password screen visibility status: {}", isVisible);
            return isVisible;
        } catch (Exception e) {
            logger.error("Forgot Password screen element not visible: {}", e.getMessage());
            return false;
        }
    }

    public String getPasswordInputType() {
        String type = passwordField.getAttribute("type");
        logger.info("Password field input type attribute: [{}]", type);
        return type;
    }

    public void clickPasswordToggle() {
        logger.info("Clicking Show/Hide password toggle button");
        safeClick(passwordToggleBtn);
    }

}