package org.bci.tests.login_module;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.bci.base.BaseClass;
import org.bci.dataproviders.DataProviders;
import org.bci.pageobjects.loginmodule.LoginPage;
import org.bci.pageobjects.loginmodule.LogoutPage;
import org.bci.pojoclasses.LoginPojo;
import org.bci.utilities.TestDetails;

public class LogoutTest extends BaseClass {
	
	
	
		@Test(
	        dataProvider = "LoginData",
	        dataProviderClass = DataProviders.class,
	        description = "Verify Logout Functionality and Goodbye Message"
	    )
	    @TestDetails(
	        id = "MES-TC-022",
	        author = "Amit",
	        priority = TestDetails.Priority.HIGH,
	        severity = TestDetails.Severity.CRITICAL,
	        browsers = {"chrome"},
	        device = "Desktop"
	    )
	    public void testLogoutFunctionality(LoginPojo data) {
	        logger.info("Starting Logout Functionality Test execution for user: {}", data.getUsername());

	        // 1. Initialize LoginPage and perform login using DataProvider POJO
	        LoginPage loginPage = new LoginPage();
	        
	        boolean isLoginSuccessful = loginPage.login(data.getUsername(), data.getPassword());
	        Assert.assertTrue(isLoginSuccessful, "Logout Test Setup Failed: Unable to log in to the dashboard!");
	        logger.info("Successfully logged in as user: {}. Proceeding to log out.", data.getUsername());

	        // 2. Initialize LogoutPage and perform sign out
	        LogoutPage logoutPage = new LogoutPage();
	        logoutPage.clickSignOut();

	        // 3. Verify successful logout, goodbye message, and return to login screen
	        logoutPage.verifySuccessfulLogout(data.getLogoutExpectedMsg());

	        logger.info("Logout Functionality Test execution completed successfully for user: {}", data.getLogoutExpectedMsg());
	    }

		@Test(
		        dataProvider = "LoginData",
		        dataProviderClass = DataProviders.class,
		        description = "Verify Back Button Access After Logout"
		    )
		    @TestDetails(
		        id = "MES-TC-023",
		        author = "Amit",
		        priority = TestDetails.Priority.HIGH,
		        severity = TestDetails.Severity.CRITICAL,
		        browsers = {"chrome"},
		        device = "Desktop"
		    )
		    public void testBackButtonAccessAfterLogout(LoginPojo data) {
		        logger.info("Starting Back Button Security Test execution for user: {}", data.getUsername());

		        // 1. Perform login
		        LoginPage loginPage = new LoginPage();
		        boolean isLoginSuccessful = loginPage.login(data.getUsername(), data.getPassword());
		        Assert.assertTrue(isLoginSuccessful, "Test Setup Failed: Unable to log in to the dashboard!");
		        logger.info("Logged in successfully. User is on the dashboard.");

		        // 2. Perform logout and verify initial logout screen
		        LogoutPage logoutPage = new LogoutPage();
		        logoutPage.clickSignOut();
		        logoutPage.getGoodbyeMessageLocatorWithUsername(data.getLogoutExpectedMsg());

		        // 3. Execute back button action and verify security via Page Object
		        logoutPage.verifyBackButtonSecurity();

		        logger.info("Back Button Security Test completed successfully for user: {}", data.getUsername());
		    }
		
			@Test(
		        dataProvider = "LoginData",
		        dataProviderClass = DataProviders.class,
		        description = "Verify User Can Re-Login Successfully After Logout"
		    )
		    @TestDetails(
		        id = "MES-TC-024",
		        author = "Amit",
		        priority = TestDetails.Priority.HIGH,
		        severity = TestDetails.Severity.CRITICAL,
		        browsers = {"chrome"},
		        device = "Desktop"
		    )
		    public void testReLoginAfterLogout(LoginPojo data) {
		        logger.info("Starting Re-Login After Logout Test execution for user: {}", data.getUsername());

		        // Step 1 & 2: Initial Login to the dashboard
		        LoginPage loginPage = new LoginPage();
		        boolean isFirstLoginSuccessful = loginPage.login(data.getUsername(), data.getPassword());
		        Assert.assertTrue(isFirstLoginSuccessful, "Initial Login Setup Failed: Unable to log in!");
		        logger.info("Initial login successful. User is on the dashboard.");

		        // Step 3 & 4: Perform Logout and verify goodbye message
		        LogoutPage logoutPage = new LogoutPage();
		        logoutPage.clickSignOut();
		        logoutPage.verifySuccessfulLogout(data.getLogoutExpectedMsg());
		        logger.info("Successfully logged out. User is back on the login screen.");

		        // Step 5 & 6: Re-enter credentials and log back in
		        boolean isReLoginSuccessful = loginPage.reLogin(data.getUsername(), data.getPassword());
		        
		        // Final Assertion: Verify dashboard is visible again
		        Assert.assertTrue(isReLoginSuccessful, "Re-Login Failed: Dashboard is not visible after second sign-in!");
		        
		        logger.info("Re-Login After Logout Test completed successfully for user: {}", data.getUsername());
		    }
		
			@Test(
		        dataProvider = "LoginData",
		        dataProviderClass = DataProviders.class,
		        description = "Verify Logout Session Validation After Browser Refresh"
		    )
		    @TestDetails(
		        id = "MES-TC-025",
		        author = "Amit",
		        priority = TestDetails.Priority.MEDIUM,
		        severity = TestDetails.Severity.CRITICAL,
		        browsers = {"chrome"},
		        device = "Desktop"
		    )
		    public void testLogoutSessionValidationAfterRefresh(LoginPojo data) {
		        logger.info("Starting Logout Refresh Validation Test execution for user: {}", data.getUsername());

		        // Step 1-3: Perform login
		        LoginPage loginPage = new LoginPage();
		        boolean isLoginSuccessful = loginPage.login(data.getUsername(), data.getPassword());
		        Assert.assertTrue(isLoginSuccessful, "Test Setup Failed: Unable to log in to the dashboard!");
		        logger.info("Logged in successfully user is on the dashboard.");

		        // Step 4 & 5: Perform logout and verify landing on logout/login screen
		        LogoutPage logoutPage = new LogoutPage();
		        logoutPage.clickSignOut();
		        logoutPage.verifySuccessfulLogout(data.getLogoutExpectedMsg());
		        logger.info("Successfully logged out. User is on the login screen.");

		        // Step 6: Refresh the browser
		        logoutPage.refreshBrowser();

		        // Expected Result Verification: 
		        // Ensure that after refresh, the dashboard remains hidden and the login screen is still active
		        logoutPage.verifySessionIsTerminatedAfterBack(); // Reuses your existing session security check

		        logger.info("Logout Refresh Validation Test completed successfully for user: {}", data.getUsername());
		    }
			
			//	MES-TC-026 Pending (Time)
		
		
}
