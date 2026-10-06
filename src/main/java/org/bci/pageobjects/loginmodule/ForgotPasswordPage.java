package org.bci.pageobjects.loginmodule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bci.base.BaseClass;
import org.bci.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ForgotPasswordPage extends BasePage {

	private static final Logger logger = LogManager.getLogger(ForgotPasswordPage.class);

	// Locators
	private final Locator usernameField;
	private final Locator forgotPasswordField;
	private final Locator rstPassScrnUsernameField;
	private final Locator sendResetButtonField;
	private final Locator requestRaiseSucessMsgLocator;
	private final Locator usernameRequiredFieldLocator;
	private final Locator checkEmailFieldMsg;
	private final Locator loginScreenBackMsg;

	// Constructor
	public ForgotPasswordPage() {
		super(BaseClass.getPage());
		Page page = BaseClass.getPage();

		// Initialize locators
		this.usernameField = page.locator("input[name='username']");
		this.forgotPasswordField = page.locator("//button[contains(normalize-space(),'Forgot password?')]");
		this.rstPassScrnUsernameField = page.locator("//input[@id='email']");
		this.sendResetButtonField = page.locator("//span[contains(text(),'Send Reset')]");
		this.requestRaiseSucessMsgLocator = page.locator("//li[@role='status']");
		this.usernameRequiredFieldLocator = page.locator("//div[@data-title]");
		this.checkEmailFieldMsg = page.locator("//h2[contains(text(),'Check Your Email')]");
		this.loginScreenBackMsg = page.locator("//h2[contains(text(),'Welcome back')]");
	}

	// Action Methods

	public void clickForgotPassword() {
		logger.info("Clicking on 'Forgot password?' button");
		safeClick(forgotPasswordField);
		logger.info("'Forgot password?' button clicked successfully");
	}

	// Verifications / Assertions

	public boolean verifyResetErrorMessage(String expectedErrorMessage) {
		logger.info("Verifying password reset error/alert message matches: [{}]", expectedErrorMessage);
		try {

			assertThat(usernameRequiredFieldLocator).hasText(expectedErrorMessage);
			logger.info("Reset error message verified successfully.");
			return true;
		} catch (AssertionError e) {
			logger.error("Reset error message validation failed: {}", e.getMessage());
			return false;
		}
	}

	// Actions for Reset Password Screen
	public void enterUsernameForReset(String username) {
		logger.info("Entering username: {}", username);
		safeClick(usernameField);
		commonSendKeys(usernameField, username);
	}
	
	public void clearUsernameField() {
		logger.info("Clearing the username field on the reset password screen...");
		rstPassScrnUsernameField.clear();
		logger.info("Username field cleared successfully.");
	}
	
	public void resetPassScrnUsernameField(String username) {
		logger.info("Entering reset password screen username: {}", username);
		commonSendKeys(rstPassScrnUsernameField, username);
	}

	public void clickSendResetButton() {
		logger.info("Clicking on 'Send Reset' button");
		safeClick(sendResetButtonField);
		logger.info("'Send Reset' button clicked successfully");
	}
	
	public void verifyCheckEmailScreenVisible() {
		logger.info("Verifying 'Check Your Email' success screen is visible...");
		assertThat(checkEmailFieldMsg).isVisible();
		assertThat(checkEmailFieldMsg).hasText("Check Your Email");
		logger.info("'Check Your Email' screen verified successfully.");
	}
	
	public boolean isResetSuccessMessageVisible() {
		logger.info("Verifying password reset request success status message...");
		return isElementVisibleAndVerified(requestRaiseSucessMsgLocator, "Reset Request Success Status Message");
	}
	
	public boolean verifyResetSuccessMessage(String expectedMessage) {
		logger.info("Verifying password reset success message matches: [{}]", expectedMessage);
		try {
			assertThat(requestRaiseSucessMsgLocator).hasText(expectedMessage);
			logger.info("Reset success message verified successfully.");
			return true;
		} catch (AssertionError e) {
			logger.error("Reset success message validation failed: {}", e.getMessage());
			return false;
		}
	}
	
	public void verifyLoginScreenVisible() {
		logger.info("Verifying that the user is navigated back to the login screen...");
		assertThat(loginScreenBackMsg).isVisible();
		assertThat(loginScreenBackMsg).hasText("Welcome back");
		logger.info("Successfully verified the user is back on the login screen.");
	}
}