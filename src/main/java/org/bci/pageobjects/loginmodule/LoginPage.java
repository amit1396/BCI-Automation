package org.bci.pageobjects.loginmodule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bci.base.BaseClass;
import org.bci.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.bci.base.BaseClass;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

public class LoginPage extends BasePage {

	private static final Logger logger = LogManager.getLogger(LoginPage.class);

	// Locators
	private final Locator usernameField;
	private final Locator passwordField;
	private final Locator signInButton;
	private final Locator dashboardField;
	private final Locator plantFieldLocator;
	private final Locator swalTitle;
	private final Locator swalMessage;
	private final Locator forgotPasswordField;
	private final Locator rstPassScrnUsernameField;
	private final Locator sendResetButtonField;
	private final Locator requestRaiseSucessMsgLocator;
	private final Locator usernameRequiredFieldLocator;
	private final Locator showHidePasswordButton;
	private final Locator adminLoginToggle;
	
	// Constructor
	public LoginPage() {
		super(BaseClass.getPage());
		Page page = BaseClass.getPage();

		// Initialize locators
		this.usernameField = page.locator("input[name='username']");
		this.passwordField = page.locator("input[name='password']");
		this.signInButton = page.locator("button[type='submit']");
		this.dashboardField = page.locator("//span[@aria-current='page']");
		this.plantFieldLocator = page.locator("//select[@id='plant']");
		this.swalTitle = page.locator("#swal2-title");
		this.swalMessage = page.locator("#swal2-html-container");
		this.forgotPasswordField = page.locator("//button[contains(normalize-space(),'Forgot password?')]");
		this.rstPassScrnUsernameField = page.locator("//input[@id='email']");
		this.sendResetButtonField = page.locator("//span[contains(text(),'Send Reset')]");
		this.requestRaiseSucessMsgLocator = page.locator("//li[@role='status']");
		this.usernameRequiredFieldLocator = page.locator("//div[@data-title]");
		this.showHidePasswordButton = page.locator("input#password + button");
		this.adminLoginToggle = page.locator(
				"//span[normalize-space()='Admin Login']/ancestor::div[contains(@class,'flex items-center justify-between')]//button");
 
	}

	// Actions with Log4j Logging
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

	// Validation method to check if login was successful
	public boolean isDashboardVisible() {
		return isElementVisibleAndVerified(dashboardField, "Dashboard Visible");
	}

	// Convenient combined login method with built-in validation
	public boolean login(String username, String password) {
		logger.info("Starting login process with user: {}", username);
		enterUsername(username);
		enterPassword(password);
		clickSignIn();

		logger.info("Login actions completed. Checking visibility of dashboard.");
		return isDashboardVisible();
	}

	public void verifyPlantAutoSelection(String username, String password, String expectedPlantValue) {
		logger.info("Entering username to trigger auto-selection: {}", username);

		// Enter username
		usernameField.fill(username);

		usernameField.press("Tab");

		passwordField.fill(password);

		passwordField.press("Tab");

		logger.info("Verifying if plant field auto-selected the expected value: {}", expectedPlantValue);

		// Playwright's assertThat automatically waits and retries until the dropdown
		// value updates here
		assertThat(plantFieldLocator).hasValue(expectedPlantValue);

		logger.info("Plant auto-selection verified successfully.");
	}

	// Add this validation method to LoginPage
	public boolean verifyLoginErrorMessage(String expectedTitle, String expectedMessage) {
		logger.info("Verifying login error popup messages...");
		try {
			// Using Playwright's built-in assertions with auto-waiting
			assertThat(swalTitle).hasText(expectedTitle);
			assertThat(swalMessage).hasText(expectedMessage);
			
			logger.info("Login error popup verified successfully: {} - {}", expectedTitle, expectedMessage);
			return true;
		} catch (AssertionError e) {
			logger.error("Popup message validation failed: {}", e.getMessage());
			return false;
		}
	}
	
	// Helper method to check if the plant dropdown is reset/empty (value = "0")
		public boolean isPlantFieldEmpty() {
			logger.info("Checking if plant field is empty/unselected...");
			try {
				// Using Playwright assertion to verify the value is "0" (Select)
				assertThat(plantFieldLocator).hasValue("0");
				logger.info("Plant field is successfully unselected/empty.");
				return true;
			} catch (AssertionError e) {
				logger.error("Plant field validation failed, it contains a value: {}", e.getMessage());
				return false;
			}
		}
		
		// Helper method to check if the Sign In button is disabled
		public boolean isSignInButtonDisabled() {
			logger.info("Checking if Sign In button is disabled...");
			boolean isDisabled = signInButton.isDisabled();
			logger.info("Sign In button disabled status: {}", isDisabled);
			return isDisabled;
		}
		
		public void clickForgotPassword() {
			logger.info("Clicking on 'Forgot password?' button");
			safeClick(forgotPasswordField);
			logger.info("'Forgot password?' button clicked successfully");
		}

		public void enterResetPasswordUsername(String username) {
			logger.info("Entering username/email for password reset: {}", username);
			safeClick(rstPassScrnUsernameField);
			commonSendKeys(rstPassScrnUsernameField, username);
		}

		public void clickSendResetButton() {
			logger.info("Clicking 'Send Reset' button");
			safeClick(sendResetButtonField);
			logger.info("'Send Reset' button clicked successfully");
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
		
		public void clickShowHidePasswordButton() {
		    logger.info("Clicking Show/Hide password toggle button");
		    safeClick(showHidePasswordButton);
		    logger.info("Show/Hide password button clicked successfully");
		}

		// Validation method to check input field type attribute:
		public String getPasswordFieldType() {
		    String typeAttr = passwordField.getAttribute("type");
		    logger.info("Current password field type attribute is: {}", typeAttr);
		    return typeAttr;
		}
		
		
		// Role Base Login Access like Admin, Operator, Viewer
		public void clickRoleCard(String roleName) {
		    logger.info("Clicking role card for: {}", roleName);
		    
		    Locator roleButton;
		    switch (roleName.toLowerCase()) {
		        case "admin":
		            roleButton = page.locator("//button[.//div[normalize-space()='Admin']]");
		            break;
		        case "operator":
		            roleButton = page.locator("//button[.//div[normalize-space()='Operator']]");
		            break;
		        case "viewer":
		            roleButton = page.locator("//button[.//div[normalize-space()='Viewer']]");
		            break;
		        default:
		            throw new IllegalArgumentException("Invalid role name provided: " + roleName);
		    }
		    
		    safeClick(roleButton);
		    logger.info("Role card '{}' clicked successfully.", roleName);
		}
		
		public String getUsernameFieldValue() {
		    return usernameField.inputValue();
		}

		public String getPasswordFieldValue() {
		    return passwordField.inputValue();
		}
		
		public void clickAdminLoginToggle() {
		    logger.info("Clicking Admin Login toggle button");
		    safeClick(adminLoginToggle);
		    logger.info("Admin Login toggle clicked successfully");
		}

		public boolean isAdminLoginToggleOn() {
		    String buttonClass = adminLoginToggle.getAttribute("class");
		    Locator toggleSpan = adminLoginToggle.locator("span");
		    String spanClass = toggleSpan.getAttribute("class");
		    
		    boolean isOn = buttonClass.contains("bg-emerald-600") && spanClass.contains("translate-x-5");
		    logger.info("Is Admin Login toggle ON? {}", isOn);
		    return isOn;
		}

		public boolean isAdminLoginToggleOff() {
		    String buttonClass = adminLoginToggle.getAttribute("class");
		    Locator toggleSpan = adminLoginToggle.locator("span");
		    String spanClass = toggleSpan.getAttribute("class");
		    
		    boolean isOff = buttonClass.contains("bg-slate-200") && spanClass.contains("translate-x-0");
		    logger.info("Is Admin Login toggle OFF? {}", isOff);
		    return isOff;
		}

		public String getSignInButtonText() {
		    String text = signInButton.textContent();
		    logger.info("Current Sign In button text retrieved: {}", text);
		    return text;
		}
		
		// Re-login action explicitly logging the re-authentication step test
	    public boolean reLogin(String username, String password) {
	        logger.info("Attempting re-login for user: {} after previous logout session", username);
	        return login(username, password); // Reuses your robust login method here
	    }

}