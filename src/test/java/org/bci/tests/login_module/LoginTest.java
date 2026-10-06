package org.bci.tests.login_module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import org.bci.base.BaseClass;
import org.bci.dataproviders.DataProviders;
import org.bci.pageobjects.loginmodule.ForgotPasswordPage;
import org.bci.pageobjects.loginmodule.LoginPage;
import org.bci.pojoclasses.LoginPojo;
import org.bci.utilities.TestDetails;
import com.microsoft.playwright.Page;

import io.qameta.allure.Allure;
import io.qameta.allure.Flaky;
import io.qameta.allure.Issue;
import io.qameta.allure.testng.AllureTestNg;

@Listeners(AllureTestNg.class)
public class LoginTest extends BaseClass {

	private static final Logger logger = LogManager.getLogger(LoginTest.class);
	
	@Test(
		description = "Verify Launch Url Test"
		)
	@TestDetails(
		id = "MES-TC-001",
		author = "Amit",
		priority = TestDetails.Priority.CRITICAL,
		severity = TestDetails.Severity.BLOCKER,
		browsers = {"chrome"},
		device = "Desktop"
	)
	public void verifyUrlLaunchTest() {
		logger.info("Starting URL Launch Validation Test");

		// Get the current active Playwright page instance from BaseClass
		Page page = getPage(); 
		String currentUrl = page.url();
		logger.info("Current navigated URL: {}", currentUrl);

		// Validate that the URL is loaded and contains the expected login path/endpoint
		Assert.assertNotNull(currentUrl, "URL Launch Failed: Page URL is null!");
		Assert.assertFalse(currentUrl.isEmpty(), "URL Launch Failed: Page URL is empty!");
		
		// Checking if it contains the login path (Adjust "/auth/login" if your base URL route differs)
		boolean isUrlCorrect = currentUrl.contains("auth/login");
		Assert.assertTrue(isUrlCorrect, "URL Validation Failed! Expected URL to contain 'auth/login', but found: " + currentUrl);

		logger.info("URL Launch Validation completed successfully");
	}
	
	
	// Test Case 002 Need to discuss
	
	@Test(

			dataProvider = "LoginData",
			dataProviderClass = DataProviders.class,
			description = "Verify Plant Auto-Selection Based on Username"	
		)
	@TestDetails(
	    id = "MES-TC-003",
	    author = "Amit",
	    priority = TestDetails.Priority.CRITICAL,
	    severity = TestDetails.Severity.BLOCKER,
	    browsers = {"chrome"},
	    device = "Desktop"
	)
	public void testPlantAutoSelection(LoginPojo data) {
	    LoginPage loginPage = new LoginPage();
	    
	    // Enter username "e.g.B1083" and verify that plant value "32" autopopulates
	    loginPage.verifyPlantAutoSelection(
	    		data.getUsername(),
	    		data.getPassword(),
	    		data.getPlantName()
	    		);
	}

	@Test(
			dataProvider = "LoginData",
			dataProviderClass = DataProviders.class,
			description = "Verify Login Test"	
		)
	@TestDetails(
		id = "MES-TC-004",
		author = "Amit",
		priority = TestDetails.Priority.CRITICAL,
		severity = TestDetails.Severity.BLOCKER,
		browsers = {"chrome"},
		device = "Desktop"
	)
	public void loginTest(LoginPojo data) {
		logger.info("Starting Login Test execution");

		// Initialize the LoginPage page object
		LoginPage loginPage = new LoginPage();

		/*
		String username = getSecureConfigValue("demo.username", "DEMO_USERNAME", "username");
		String password = getSecureConfigValue("demo.password", "DEMO_PASSWORD", "password");
		*/
		
		String username = data.getUsername();
		String password = data.getPassword();
		
		// Perform login action and capture the validation status
		boolean isLoginSuccessful = loginPage.login(username, password);
		
		// Assert that the login was successful and the dashboard is visible
		Assert.assertTrue(isLoginSuccessful, "Login Test Failed: Dashboard element is not visible after sign in!");

		logger.info("Login Test execution completed successfully");
	}
	
	@Test(
			dataProvider = "LoginData",
			dataProviderClass = DataProviders.class,
			description = "Verify login with valid User Name and invalid Password"	
		)
	@TestDetails(
		id = "MES-TC-005",
		author = "Amit",
		priority = TestDetails.Priority.HIGH,
		severity = TestDetails.Severity.NORMAL,
		browsers = {"chrome"},
		device = "Desktop"
	)
	public void loginWithInvalidPasswordTest(LoginPojo data) {
		logger.info("Starting Login Test with Invalid Password execution");

		LoginPage loginPage = new LoginPage();

		logger.info("Attempting login with username: {} and an invalid password", data.getUsername());
		
		// Perform login actions
		loginPage.enterUsername(data.getUsername());
		loginPage.enterPassword(data.getPassword());
		loginPage.clickSignIn();

		// Validate that the SweetAlert2 error popup appears with both expected messages
		boolean isErrorMsgVerified = loginPage.verifyLoginErrorMessage("Login failed", "Invalid User ID or Password");
		
		// Assert the result
		Assert.assertTrue(isErrorMsgVerified, "Negative Login Test Failed: Expected error popup 'Login failed' / 'Invalid User ID or Password' did not appear!");

		// Also ensure the dashboard is NOT visible
		Assert.assertFalse(loginPage.isDashboardVisible(), "Security Risk: User accessed dashboard with an invalid password!");

		logger.info("Login Test with Invalid Password completed successfully.");
	}
	
		@Test(
			dataProvider = "LoginData",
			dataProviderClass = DataProviders.class,
			description = "Verify Plant does not load and Sign In button does not grant access for invalid credentials"
		)
		@TestDetails(
			id = "MES-TC-006",
			author = "Amit",
			priority = TestDetails.Priority.HIGH,
			severity = TestDetails.Severity.NORMAL,
			browsers = {"chrome"},
			device = "Desktop"
		)
		public void verifyPlantAndSignInBlockedTest(LoginPojo data) {
			logger.info("Starting Negative Test: Invalid Credentials - Username: {}", data.getUsername());

			LoginPage loginPage = new LoginPage();

			// 1. Enter invalid username and trigger blur/tab event
			loginPage.enterUsername(data.getUsername());
			
			// 2. Validate that the plant field remains empty or unselected (value should be "0")
			boolean isPlantEmpty = loginPage.isPlantFieldEmpty();
			Assert.assertTrue(isPlantEmpty, "Security Issue: Plant field loaded data or auto-selected for invalid username: " + data.getUsername());

			// 3. Enter password
			loginPage.enterPassword(data.getPassword());

			// 4. Click the Sign-In button (which should fail to log in)
			loginPage.clickSignIn();

			// 5. Ensure dashboard is NOT visible (proving the Sign In action did not work/grant access)
			boolean isDashboardVisible = loginPage.isDashboardVisible();
			Assert.assertFalse(isDashboardVisible, "Security Risk: User successfully accessed the dashboard with invalid credentials!");

			logger.info("Negative Test completed successfully - Plant did not load and Sign In action was successfully blocked.");
		}
		
		@Test(
				dataProvider = "LoginData",
				dataProviderClass = DataProviders.class,
				description = "Verify login with username having leading/trailing spaces (Plant should not load)"
			)
			@TestDetails(
				id = "MES-TC-007",
				author = "Amit",
				priority = TestDetails.Priority.HIGH,
				severity = TestDetails.Severity.NORMAL,
				browsers = {"chrome"},
				device = "Desktop"
			)
			public void loginWithSpacesInUsernameFromExcelTest(LoginPojo data) {
				// Explicitly add spaces to the username pulled from Excel to ensure it has leading/trailing whitespace
				String usernameWithSpaces = "   " + data.getUsername().trim() + "   ";
				
				logger.info("Starting Negative Test: Username with forced spaces - Username: [{}]", usernameWithSpaces);

				LoginPage loginPage = new LoginPage();

				// 1. Enter the username with spaces and trigger blur/tab event
				loginPage.enterUsername(usernameWithSpaces);
				
				// 2. Validate that the plant field remains empty or unselected (value should be "0")
				boolean isPlantEmpty = loginPage.isPlantFieldEmpty();
				Assert.assertTrue(isPlantEmpty, "Security/UX Issue: Plant field loaded data for a username with spaces: " + usernameWithSpaces);

				// 3. Enter password from Excel
				loginPage.enterPassword(data.getPassword());

				// 4. Click Sign In
				loginPage.clickSignIn();

				// 5. Ensure dashboard is NOT visible
				boolean isDashboardVisible = loginPage.isDashboardVisible();
				Assert.assertFalse(isDashboardVisible, "Security Risk: User accessed dashboard using a username with spaces!");

				logger.info("Negative Test completed successfully - Plant did not load and login was blocked for username with spaces.");
			}
		
		@Test(
				dataProvider = "LoginData",
				dataProviderClass = DataProviders.class,
				description = "Verify Sign In button is disabled when username and/or password fields are blank"
			)
			@TestDetails(
				id = "MES-TC-008",
				author = "Amit",
				priority = TestDetails.Priority.HIGH,
				severity = TestDetails.Severity.NORMAL,
				browsers = {"chrome"},
				device = "Desktop"
			)
			public void verifyLoginWithBlankFieldsTest(LoginPojo data) {
				logger.info("Starting Negative Test: Blank Fields Validation - Username: [{}], Password provided: [{}]", 
						data.getUsername(), (data.getPassword() != null && !data.getPassword().isEmpty()));

				LoginPage loginPage = new LoginPage();

				// 1. Enter username (could be empty/blank from Excel)
				if (data.getUsername() != null && !data.getUsername().isEmpty()) {
					loginPage.enterUsername(data.getUsername());
				}

				// 2. If username is blank, verify plant field remains empty
				if (data.getUsername() == null || data.getUsername().trim().isEmpty()) {
					boolean isPlantEmpty = loginPage.isPlantFieldEmpty();
					Assert.assertTrue(isPlantEmpty, "Security/UX Issue: Plant field loaded data even though username was blank!");
				}

				// 3. Enter password (could be empty/blank from Excel)
				if (data.getPassword() != null && !data.getPassword().isEmpty()) {
					loginPage.enterPassword(data.getPassword());
				}

				// 4. Validate that the Sign In button is disabled
				boolean isSignInDisabled = loginPage.isSignInButtonDisabled();
				Assert.assertTrue(isSignInDisabled, "Security Risk: Sign In button is enabled/active when required fields are blank!");

				// 5. Ensure dashboard is NOT visible
				Assert.assertFalse(loginPage.isDashboardVisible(), "Security Risk: User accessed dashboard with blank credentials!");

				logger.info("Blank fields negative test completed successfully - Sign In button is correctly disabled.");
			}
		
		// Test Case 009 Need to discuss with the expected result
		
		@Test(
				dataProvider = "LoginData",
				dataProviderClass = DataProviders.class,
				description = "Verify navigation to Forgot Password screen and validate reset request message"
			)
			@TestDetails(
				id = "MES-TC-010",
				author = "Amit",
				priority = TestDetails.Priority.MEDIUM,
				severity = TestDetails.Severity.NORMAL,
				browsers = {"chrome"},
				device = "Desktop"
			)
			public void verifyForgotPasswordNavigationAndRequestTest(LoginPojo data) {
				logger.info("Starting Test: Verify Forgot Password Message for Username: {}", data.getUsername());

				LoginPage loginPage = new LoginPage();
				loginPage.enterUsername(data.getUsername());

				// 1. Click on 'Forgot password?' button from the login screen
				loginPage.clickForgotPassword();

				// 2. Enter username/email on the reset screen
				loginPage.enterResetPasswordUsername(data.getUsername());

				// 3. Click the 'Send Reset' button to raise the request
				loginPage.clickSendResetButton();

				// 4. Verify the expected success message text directly 
				String expectedSuccessMessage = data.getExpectedSuccessMessage(); 
				boolean isMessageValid = loginPage.verifyResetSuccessMessage(expectedSuccessMessage);
				
				Assert.assertTrue(isMessageValid, "Validation Failure: The reset success status message text did not match expectations!");

				logger.info("Forgot Password message verification test completed successfully.");
			}
		
		@Test(
			    dataProvider = "LoginData",
			    dataProviderClass = DataProviders.class,
			    description = "Verify system alert when clicking password reset without entering username"
			)
			@TestDetails(
			    id = "MES-TC-011",
			    author = "Amit",
			    priority = TestDetails.Priority.MEDIUM,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			public void verifyForgotPasswordBlankUsernameAlertTest(LoginPojo data) {
			    logger.info("Starting Negative Test: Verify Forgot Password alert for blank username");

			    ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

			    // 1. Click on 'Forgot password?' button from the login screen
			    forgotPasswordPage.clickForgotPassword();

			    // 2. Verify that the mandatory validation alert message is displayed correctly
			    String expectedAlertMessage = data.getExpectedAlertMessage();
			    
			    boolean isAlertMessageValid = forgotPasswordPage.verifyResetErrorMessage(expectedAlertMessage); 

			    Assert.assertTrue(isAlertMessageValid, "Validation Failure: Expected alert message '" + expectedAlertMessage + "' was not displayed!");

			    logger.info("Forgot Password blank username validation test completed successfully.");
			}
		
		@Test(
				dataProvider = "LoginData",
				dataProviderClass = DataProviders.class,
				description = "Verify login is blocked for an inactive user and correct deactivation message is displayed")
		@TestDetails(
				id = "MES-TC-012",
				author = "Amit",
				priority = TestDetails.Priority.HIGH,
				severity = TestDetails.Severity.NORMAL,
				browsers = {"chrome"},
				device = "Desktop"
			)
	    public void inactiveUserLoginBlocked(LoginPojo data) {
	        logger.info("Starting Test: - Inactive User Login Block");

	        LoginPage loginPage = new LoginPage();

	        String inactiveUsername = data.getUsername();
	        String inactivePassword = data.getPassword();
	        
	        String expectedErrorTitle = data.getExpectedErrorTitle(); 
	        String expectedErrorMessage = data.getExpectedErrorMessage();

	        loginPage.enterUsername(inactiveUsername);
	        loginPage.enterPassword(inactivePassword);
	        loginPage.clickSignIn();

	        boolean isDashboardVisible = loginPage.isDashboardVisible();
	        Assert.assertFalse(isDashboardVisible, "Failure: Dashboard is visible for an inactive user!");

	        boolean isErrorVerified = loginPage.verifyLoginErrorMessage(expectedErrorTitle, expectedErrorMessage);
	        Assert.assertTrue(isErrorVerified, "Failure: The deactivation error popup message did not match expected values.");

	        logger.info("Test completed successfully For Inactive User");
	    }
		
		@Test(
			    dataProvider = "LoginData",
			    dataProviderClass = DataProviders.class,
			    description = "Verify login is blocked for a deleted user and correct error message is displayed"
			)
			@TestDetails(
			    id = "MES-TC-013",
			    author = "Amit",
			    priority = TestDetails.Priority.HIGH,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			public void deletedUserLoginBlocked(LoginPojo data) {
			    logger.info("Starting Test: - Deleted User Login Block");

			    LoginPage loginPage = new LoginPage();

			    String deletedUsername = data.getUsername();
			    String deletedPassword = data.getPassword();
			    
				String expectedErrorTitle = data.getExpectedErrorTitle();
				String expectedErrorMessage = data.getExpectedErrorMessage();

			    // Perform login actions
			    loginPage.enterUsername(deletedUsername);
			    loginPage.enterPassword(deletedPassword);
			    loginPage.clickSignIn();

			    // 1. Verify that dashboard is NOT visible (login is blocked)
			    boolean isDashboardVisible = loginPage.isDashboardVisible();
			    Assert.assertFalse(isDashboardVisible, "Failure: Dashboard is visible for a deleted user!");

			    // 2. Verify the appropriate error popup message is displayed
			    boolean isErrorVerified = loginPage.verifyLoginErrorMessage(expectedErrorTitle, expectedErrorMessage);
			    Assert.assertTrue(isErrorVerified, "Failure: The deleted account error popup message did not match expected values.");

			    logger.info("Blocked for a deleted user test completed successfully.");
			}
		
		@Test(
			    dataProvider = "LoginData",
			    dataProviderClass = DataProviders.class,
			    description = "Verify Show/Hide password button is working as expected"
			)
			@TestDetails(
			    id = "MES-TC-014",
			    author = "Amit",
			    priority = TestDetails.Priority.MEDIUM,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			public void verifyShowHidePasswordToggle(LoginPojo data) {
			    logger.info("Starting Test: - Verify Show/Hide Password Button Functionality");

			    LoginPage loginPage = new LoginPage();

			    String validUsername = data.getUsername();
			    String validPassword = data.getPassword();

			    // 1 & 2. Enter username
			    loginPage.enterUsername(validUsername);

			    // 3. Enter password and verify it is masked/encrypted by default (type="password")
			    loginPage.enterPassword(validPassword);
			    Assert.assertEquals(loginPage.getPasswordFieldType(), "password", "Password should be masked/encrypted initially!");

			    // 4 & 5. Click show/hide button and verify password becomes visible (type="text")
			    loginPage.clickShowHidePasswordButton();
			    Assert.assertEquals(loginPage.getPasswordFieldType(), "text", "Password should be visible after clicking the show button!");

			    // 6 & 7. Click again on show/hide button and verify password is hidden again (type="password")
			    loginPage.clickShowHidePasswordButton();
			    Assert.assertEquals(loginPage.getPasswordFieldType(), "password", "Password should be hidden/masked again after second click!");

			    logger.info("Show/Hide Functionality test completed successfully.");
			}
		
		@Test(
			    description = "Verify Quick Access Auto-Fill for Admin, Operator, and Viewer roles"
			)
			@TestDetails(
			    id = "MES-TC-015",
			    author = "Amit",
			    priority = TestDetails.Priority.MEDIUM,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			public void verifyQuickAccessAutoFillRoles() {
			    logger.info("Starting Test:- Quick Access Auto-Fill for All Roles");

			    LoginPage loginPage = new LoginPage();

			    // Array of roles to test autofill functionality
			    String[] roles = {"Admin", "Operator", "Viewer"};

			    for (String role : roles) {
			        logger.info("Testing auto-fill for role: {}", role);

			        // 1. Click the quick-fill role card
			        loginPage.clickRoleCard(role);

			        // 2. Verify that both username and password fields are successfully autofilled (not empty)
			        String filledUsername = loginPage.getUsernameFieldValue();
			        String filledPassword = loginPage.getPasswordFieldValue();

			        logger.info("Auto-filled Username for {}: [{}]", role, filledUsername);
			        
			        Assert.assertNotNull(filledUsername, "Username field should not be null for " + role);
			        Assert.assertFalse(filledUsername.trim().isEmpty(), "Username field was not auto-filled for role: " + role);
			        
			        Assert.assertNotNull(filledPassword, "Password field should not be null for " + role);
			        Assert.assertFalse(filledPassword.trim().isEmpty(), "Password field was not auto-filled for role: " + role);

			    }

			    logger.info("Test completed successfully: MES-TC-015 - All role auto-fills verified.");
			}
		
		@Test(
			    description = "Verify successful login using auto-filled role credentials and redirection to dashboard"
			)
			@TestDetails(
			    id = "MES-TC-016",
			    author = "Amit",
			    priority = TestDetails.Priority.HIGH,
			    severity = TestDetails.Severity.CRITICAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			@Flaky
			@Issue("Functionality Not Working")
			public void verifySuccessfulLoginUsingAutoFill() {
			    logger.info("Starting Test: - Successful Login Using Auto-Filled Credentials");

			    LoginPage loginPage = new LoginPage();

			    // Choose the role you want to test with (e.g., "Admin", "Operator", or "Viewer")
			    String targetRole = "Admin";
			    logger.info("Testing auto-fill login for role: {}", targetRole);

			    // 1. Click the quick-fill role card to autopopulate username and password
			    loginPage.clickRoleCard(targetRole);

			    // 2. Click the Sign In / Submit button
			    loginPage.clickSignIn();

			    // 3. Verify that the system authenticates and successfully navigates to the dashboard
			    boolean isDashboardVisible = loginPage.isDashboardVisible();
			    logger.info("Dashboard visibility status after auto-fill login: {}", isDashboardVisible);

			    Assert.assertTrue(isDashboardVisible, "Failure: Dashboard is not visible after logging in with auto-filled " + targetRole + " credentials!");

			    logger.info("Test completed successfully:- Auto-fill login and dashboard redirection verified.");
			}
		
		@Test(
				dataProvider = "LoginData",
			    dataProviderClass = DataProviders.class,
			    description = "Verify error handling when attempting login with role credentials resulting in an error"
			)
			@TestDetails(
			    id = "MES-TC-017",
			    author = "Amit",
			    priority = TestDetails.Priority.MEDIUM,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			@Flaky
			@Issue("Functionality Not Working")
			public void verifyErrorHandlingForRoleLogin(LoginPojo data) {
			    logger.info("Starting Test: - Verify Error Handling for Role Login");

			    LoginPage loginPage = new LoginPage();

			    // Select the role card (e.g., Admin, Operator, or Viewer)
			    String targetRole = "Admin";
			    logger.info("Clicking quick-fill role card for: {}", targetRole);
			    loginPage.clickRoleCard(targetRole);

			    // Click the Sign-In button
			    loginPage.clickSignIn();

			    // Define expected error title and message 
				String expectedErrorTitle = data.getExpectedErrorTitle();
				String expectedErrorMessage = data.getExpectedErrorMessage();

			    // Verify that the dashboard is NOT visible (login is blocked)
			    boolean isDashboardVisible = loginPage.isDashboardVisible();
			    Assert.assertFalse(isDashboardVisible, "Failure: Dashboard should not be visible for this login attempt!");

			    // Verify the error popup message
			    boolean isErrorVerified = loginPage.verifyLoginErrorMessage(expectedErrorTitle, expectedErrorMessage);
			    Assert.assertTrue(isErrorVerified, "Failure: The expected error popup message was not displayed correctly.");

			    logger.info("Role Base Login test completed successfully.");
			}
		
		@Test(
			    description = "Verify Admin Login toggle can be turned ON and OFF successfully"
			)
			@TestDetails(
			    id = "MES-TC-018",
			    author = "Amit",
			    priority = TestDetails.Priority.MEDIUM,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			public void verifyAdminLoginToggleOnOffFunctionality() {
			    logger.info("Starting Test: - Verify Admin Login Toggle ON/OFF Functionality");

			    LoginPage loginPage = new LoginPage();

			    // 1. Verify default state is OFF
			    Assert.assertTrue(loginPage.isAdminLoginToggleOff(), "Failure: Admin Login toggle should be OFF by default!");
			    logger.info("Verified: Toggle is OFF by default.");

			    // 2. Click toggle to turn it ON
			    loginPage.clickAdminLoginToggle();

			    // 3. Verify state is now ON
			    Assert.assertTrue(loginPage.isAdminLoginToggleOn(), "Failure: Admin Login toggle failed to turn ON after clicking!");
			    logger.info("Verified: Toggle successfully turned ON.");

			    // 4. Click toggle again to turn it OFF
			    loginPage.clickAdminLoginToggle();

			    // 5. Verify state is back to OFF
			    Assert.assertTrue(loginPage.isAdminLoginToggleOff(), "Failure: Admin Login toggle failed to turn OFF after second click!");
			    logger.info("Verified: Toggle successfully turned OFF again.");

			    logger.info("Test completed successfully.");
			}
		
		// Test Case 19 functionality not working
		
		@Test(
			    description = "Verify Sign In button label changes to 'Admin Sign In' when Admin Login toggle is turned ON"
			)
			@TestDetails(
			    id = "MES-TC-020",
			    author = "Amit",
			    priority = TestDetails.Priority.MEDIUM,
			    severity = TestDetails.Severity.NORMAL,
			    browsers = {"chrome"},
			    device = "Desktop"
			)
			public void verifySignInButtonLabelChangeOnToggle() {
			    logger.info("Starting Test: MES-TC-020 - Verify Sign In Button Label Change on Toggle");

			    LoginPage loginPage = new LoginPage();

			    // 1. Verify default Sign In button text (e.g., "Sign In")
			    String defaultButtonText = loginPage.getSignInButtonText();
			    logger.info("Default Sign In button text: {}", defaultButtonText);
			    Assert.assertTrue(defaultButtonText.contains("Sign In"), "Failure: Default button text is incorrect!");

			    // 2. Turn ON the Admin Login toggle
			    loginPage.clickAdminLoginToggle();
			    Assert.assertTrue(loginPage.isAdminLoginToggleOn(), "Failure: Admin Login toggle failed to turn ON!");

			    // 3. Verify the button text changes to "Admin Sign In"
			    String updatedButtonText = loginPage.getSignInButtonText();
			    logger.info("Updated Sign In button text after toggle ON: {}", updatedButtonText);
			    
			    Assert.assertEquals(updatedButtonText.trim(), "Admin Sign In", "Failure: Sign In button label did not change to 'Admin Sign In'!");

			    // Turn toggle back OFF and verify it reverts
			    loginPage.clickAdminLoginToggle();
			    String revertedButtonText = loginPage.getSignInButtonText();
			    Assert.assertTrue(revertedButtonText.contains("Sign In"), "Failure: Button label did not revert after turning toggle OFF!");

			    logger.info("Test completed successfully");
			}
		
		// Test Case 21 functionality not working
		
		
		
		
}