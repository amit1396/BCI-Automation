package org.bci.pageobjects.optivusloginmodule;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bci.base.BaseClass;
import org.bci.base.BasePage;

public class ForgotPasswordPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(ForgotPasswordPage.class);

    // Locators for Forgot Password screen elements
    private final Locator forgotPasswordLink;
    private final Locator appLogo;
    private final Locator pageHeading;
    private final Locator usernameLabel;
    private final Locator usernameInput;
    private final Locator submitButton;
    private final Locator closeButton;
    private final Locator footerText;
    private final Locator errorMessage;
    private final Locator responseMessageText;
    private final Locator enterResetCodeButton;
    private final Locator resetPasswordScreen;
    private final Locator backToSignInLink;

    public ForgotPasswordPage() {
        super(BaseClass.getPage());
        Page page = BaseClass.getPage();
        this.forgotPasswordLink = page.locator("a.forgot-link[routerlink='/forgot-password']");
        this.appLogo = page.locator(".brand-logo, img[alt*='BCI logo']");
        this.pageHeading = page.locator("h1, h1.login-title");
        this.usernameLabel = page.locator("label[for='forgot-username']");
        this.usernameInput = page.locator("input[name='username']");
        this.submitButton = page.locator("button.login-button[type='submit']");
        this.closeButton = page.locator("button.login-button[type='close']");
        this.footerText = page.locator("p.login-footer");
        this.errorMessage = page.locator("div.error-banner span, .error-message, .invalid-feedback");
        this.responseMessageText = page.locator("p.form-sub");
        this.enterResetCodeButton = page.locator("");
        this.resetPasswordScreen = page.locator("");
        this.backToSignInLink = page.locator("a.forgot-link[routerlink='/login']");
    }

    // Visibility Getter & Setter

    public boolean isAppLogoVisible() {
        return isElementVisibleAndVerified(appLogo, "App/Client Name Logo");
    }

    public boolean isPageHeadingVisible() {
        return isElementVisibleAndVerified(pageHeading, "Forgot Password Page Heading");
    }

    public boolean isUsernameLabelVisible() {
        return isElementVisibleAndVerified(usernameLabel, "Username Field Label");
    }

    public boolean isUsernameInputVisible() {
        return isElementVisibleAndVerified(usernameInput, "Username Input Field");
    }

    public boolean isSubmitButtonVisible() {
        return isElementVisibleAndVerified(submitButton, "Submit Button");
    }

    public boolean isCloseButtonVisible() {
        return isElementVisibleAndVerified(closeButton, "Close Button");
    }

    public boolean isFooterTextVisible() {
        return isElementVisibleAndVerified(footerText, "Footer Text / Copyright");
    }

    public String getPageHeadingText() {
        logger.info("Fetching forgot password page heading text");
        return pageHeading.textContent().trim();
    }

    public String getUsernamePlaceholder() {
        logger.info("Fetching username input placeholder text");
        return usernameInput.getAttribute("placeholder");
    }

    public String getSubmitButtonText() {
        logger.info("Fetching submit button text");
        return submitButton.textContent().trim();
    }

    public String getFooterText() {
        logger.info("Fetching footer text");
        return footerText.textContent().trim();
    }

    // Hover Action Methods

    public boolean isSubmitButtonHoverable() {
        return verifyButtonHoverEffect(submitButton);
    }

    public boolean isCloseButtonHoverable() {
        return verifyButtonHoverEffect(closeButton);
    }

    // Actions Method

    public void enterUsername(String username) {
        logger.info("Entering recovery username: {}", username);
        safeClick(usernameInput);
        commonSendKeys(usernameInput, username);
    }

    public void clearUsername() {
        logger.info("Clearing username input field");
        usernameInput.clear();
    }

    public void clickSubmit() {
        logger.info("Clicking Submit button");
        safeClick(submitButton);
    }

    public void clickClose() {
        logger.info("Clicking Close button");
        safeClick(closeButton);
    }

    /**
     * Complete helper action to submit a password reset request.
     *
     * @param username The username to recover
     */
    public void requestPasswordReset(String username) {
        logger.info("Initiating password reset request for user: {}", username);
        enterUsername(username);
        clickSubmit();
        logger.info("Password reset request submitted successfully");
    }

    public boolean isErrorMessageVisible() {
        return isElementVisibleAndVerified(errorMessage, "Forgot Password Error Message");
    }

    public String getErrorMessage() {
        logger.info("Fetching forgot password error message text");
        return errorMessage.textContent().trim();
    }

    public boolean isResponseMessageVisible() {
        return isElementVisibleAndVerified(responseMessageText, "Forgot Password Response Message");
    }

    public String getResponseMessage() {
        logger.info("Fetching forgot password response message text");
        return responseMessageText.textContent().trim();
    }
    public void clickEnterResetCodeButton() {
        logger.info("Clicking on 'Enter Reset Code' button/link");
        safeClick(enterResetCodeButton);
    }

    public boolean isResetPasswordScreenVisible() {
        logger.info("Verifying if Reset Password screen is visible...");
        try {
            resetPasswordScreen.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            boolean isVisible = resetPasswordScreen.isVisible();
            logger.info("Reset Password screen visibility status: {}", isVisible);
            return isVisible;
        } catch (Exception e) {
            logger.error("Reset Password screen element not visible: {}", e.getMessage());
            return false;
        }
    }

    public void clickBackToSignIn() {
        logger.info("Clicking on 'Back to sign in' link");
        safeClick(backToSignInLink);
        logger.info("'Back to sign in' link clicked successfully");
    }

    public String getUsernameInputValue() {
        logger.info("Fetching value from username input field");
        return usernameInput.inputValue();
    }
}