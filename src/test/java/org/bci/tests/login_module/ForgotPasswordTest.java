package org.bci.tests.login_module;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.bci.base.BaseClass;
import org.bci.dataproviders.DataProviders;
import org.bci.pageobjects.loginmodule.ForgotPasswordPage;
import org.bci.pageobjects.loginmodule.LoginPage;
import org.bci.pojoclasses.LoginPojo;
import org.bci.utilities.TestDetails;

public class ForgotPasswordTest extends BaseClass {
	
	
	// MES-TC-027 - Pending

		@Test(
				dataProvider = "LoginData",
			    dataProviderClass = DataProviders.class,
			    description = "Verify validation error message 'Username is required' appears when username field is blank"
	    )
	    @TestDetails(
	        id = "MES-TC-028",
	        author = "Amit",
	        priority = TestDetails.Priority.MEDIUM,
	        severity = TestDetails.Severity.NORMAL,
	        browsers = {"chrome"},
	        device = "Desktop"
	    )
	    public void testUsernameRequiredValidationError(LoginPojo data) {
	        logger.info("Starting validation test for 'Username is required' message...");

	        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

	        // 1. Leave USERNAME field blank and trigger the action
	        forgotPasswordPage.clickForgotPassword();

	        // 2. Validate that the exact error message appears
	        String errorMsg = data.getExpectedAlertMessage();
	        forgotPasswordPage.verifyResetErrorMessage(errorMsg);

	        logger.info("Test completed successfully: 'Username is required' validation message verified.");
	    }
	
		@Test(
	        dataProvider = "LoginData",
	        dataProviderClass = DataProviders.class,
	        description = "Verify validation error 'Username is required' when clicking Send Reset with a blank username field"
	    )
	    @TestDetails(
	        id = "MES-TC-029",
	        author = "Amit",
	        priority = TestDetails.Priority.MEDIUM,
	        severity = TestDetails.Severity.NORMAL,
	        browsers = {"chrome"},
	        device = "Desktop"
	    )
	    public void testBlankUsernameOnResetPasswordScreenValidation(LoginPojo data) {
	        logger.info("Starting validation test for blank username on Reset Password screen for user: {}", data.getUsername());

	        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();
	        
	        // 1 & 2. Enter username and navigate to the reset password screen
	        forgotPasswordPage.enterUsernameForReset(data.getUsername());
	        forgotPasswordPage.clickForgotPassword();

	        // 3 & 4. Ensure the Username field on the reset screen is cleared/left blank
	        forgotPasswordPage.clearUsernameField();

	        // 5. Click on the send reset button
	        forgotPasswordPage.clickSendResetButton();

	        // Validate that the 'Username is required' error message appears
	        String expectedErrorMsg = data.getExpectedAlertMessage();
	        forgotPasswordPage.verifyResetErrorMessage(expectedErrorMsg);

	        logger.info("Test completed successfully: 'Username is required' validation error verified on Reset Password screen.");
	    }
	
	
		// MES-TC-030 - Pending (MFA) 
	
		// MES-TC-031 - 
		// MES-TC-032 - 
		// MES-TC-033 - 
	
		@Test(
		        dataProvider = "LoginData",
		        dataProviderClass = DataProviders.class,
		        description = "Verify application behavior when the same user sends a duplicate password reset request"
		    )
		    @TestDetails(
		        id = "MES-TC-034",
		        author = "Amit",
		        priority = TestDetails.Priority.MEDIUM,
		        severity = TestDetails.Severity.NORMAL,
		        browsers = {"chrome"},
		        device = "Desktop"
		    )
		    public void testDuplicatePasswordResetRequest(LoginPojo data) {
		        logger.info("Starting duplicate password reset request test for user: {}", data.getUsername());

		        LoginPage loginPage = new LoginPage();
		        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

		        // Steps 1 - 4: Enter username and navigate to Forgot Password screen
		        loginPage.enterUsername(data.getUsername());
		        forgotPasswordPage.clickForgotPassword();

		        // Step 5 & 6: First Reset Request Submission
		        logger.info("Executing 1st password reset request...");
		        forgotPasswordPage.resetPassScrnUsernameField(data.getUsername());
		        forgotPasswordPage.clickSendResetButton();
		        
		        // Verify 1st Reset Success Message using data provider
		        String expectedSuccessMessage = data.getExpectedSuccessMessage(); 
		        boolean isMessageValid = forgotPasswordPage.verifyResetSuccessMessage(expectedSuccessMessage);
		        Assert.assertTrue(isMessageValid, "Validation Failure: The reset success status message text did not match expectations!");
		        
		        // Step 7: Click Forgot button again and verify "Check Your Email" screen
		        logger.info("Clicking forgot password button again to check email screen view...");
		        forgotPasswordPage.clickForgotPassword();
		        forgotPasswordPage.verifyCheckEmailScreenVisible();

		        logger.info("Duplicate password reset test completed successfully for user: {}", data.getUsername());
		    }
		
		// MES-TC-035 -
		@Test(
				dataProvider = "LoginData",
				dataProviderClass = DataProviders.class,
				description = "Verify Forgot password functionality on page refresh"
		)
		@TestDetails(
				id = "MES-TC-035",
				author = "Amit",
				priority = TestDetails.Priority.MEDIUM,
				severity = TestDetails.Severity.NORMAL,
				browsers = {"chrome"},
				device = "Desktop"
		)
		public void testForgotPasswordFunctionalityOnPageRefresh(LoginPojo data) {
			logger.info("Starting test to verify forgot password functionality on page refresh for user: {}", data.getUsername());

			LoginPage loginPage = new LoginPage();
			ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();
			
			// 1. Enter Username
			loginPage.enterUsername(data.getUsername());

			// 2. Click on forgot password link
			forgotPasswordPage.clickForgotPassword();

			// 3. Enter the Valid user Name in the username field
			forgotPasswordPage.resetPassScrnUsernameField(data.getUsername());

			// 4. Refresh the page
			forgotPasswordPage.refreshBrowser();

			// Verification: System should navigate the user to login page next
			forgotPasswordPage.verifyLoginScreenVisible();
			
			logger.info("Test completed successfully: Page refresh successfully redirected user back to the login screen.");
		}
		
		// MES-TC-036 - 
		 
	
	
	
	
}
