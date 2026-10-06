package org.bci.tests.optivus_loginmodule;

import com.microsoft.playwright.*;
import io.qameta.allure.testng.AllureTestNg;
import org.bci.base.BaseClass;
import org.bci.dataproviders.DataProviders;
import org.bci.pageobjects.optivusloginmodule.ForgotPasswordPage;
import org.bci.pageobjects.optivusloginmodule.LoginPage;
import org.bci.pageobjects.optivusloginmodule.LogoutPage;
import org.bci.pojoclasses.LoginPojo;
import org.bci.utilities.TestDetails;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(AllureTestNg.class)
public class LoginModuleTest extends BaseClass {


    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify application login URL landing using Test ID and Expected URL from Excel"
    )
    @TestDetails(
            id = "OPTVS-TC-001",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyLoginUrlWithExcelData(LoginPojo data) {
        // 1. Fetch Test ID and Expected value dynamically from Excel row data
        String testId = data.getTcId();
        String expectedUrlFromExcel = data.getExpectedUrlMsgName();

        logger.info("Running Test ID: [{}]", testId);
        logger.info("Expected URL/Message fetched from Excel: [{}]", expectedUrlFromExcel);

        // 2. Capture current active URL from the browser
        String currentUrl = BaseClass.getPage().url();
        logger.info("Captured actual browser URL: [{}]", currentUrl);

        // 3. Validations
        Assert.assertNotNull(currentUrl, "Current URL is null! Browser navigation failed.");
        Assert.assertNotNull(expectedUrlFromExcel, "Expected URL from Excel is null or column mapping failed!");

        // 4. Assert that the current URL contains or matches the expected value from Excel
        boolean isMatch = currentUrl.contains(expectedUrlFromExcel);

        Assert.assertTrue(isMatch,
                "URL Landing Verification Failed for Test ID: " + testId +
                        " | Expected to contain: '" + expectedUrlFromExcel +
                        "' | But actual URL was: '" + currentUrl + "'");

        logger.info("Test ID [{}] passed successfully! URL verified.", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify all UI content, text correctness, labels, placeholders, footer, and hover consistency on the Login screen"
    )
    @TestDetails(
            id = "OPTVS-TC-002",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyLoginScreenUIContent(LoginPojo data) {
        logger.info("Starting Comprehensive Login Screen UI Content & Validation Test");

        String testId = data.getTcId();

        logger.info("Checking RunMode for Test ID [{}]: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        Assert.assertTrue(loginPage.isAppLogoVisible(), "UI Validation Failed: App/Client Name Logo is missing!");
        Assert.assertTrue(loginPage.isLoginHeadingVisible(), "UI Validation Failed: Login page heading is missing!");
        Assert.assertTrue(loginPage.isUsernameLabelVisible(), "UI Validation Failed: Username field label is missing!");
        Assert.assertTrue(loginPage.isPasswordLabelVisible(), "UI Validation Failed: Password field label is missing!");
        Assert.assertTrue(loginPage.isSignInButtonVisible(), "UI Validation Failed: Login/Sign-In button is missing!");
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "UI Validation Failed: 'Forgot Password' link is missing!");
        Assert.assertTrue(loginPage.isFooterTextVisible(), "UI Validation Failed: Footer text / copyright info is missing!");

        String userPlaceholder = loginPage.getUsernamePlaceholder();
        String passPlaceholder = loginPage.getPasswordPlaceholder();
        String buttonText = loginPage.getSignInButtonText();
        String forgotText = loginPage.getForgotPasswordText();
        String footerText = loginPage.getFooterText();

        logger.info("Captured Username Placeholder: [{}]", userPlaceholder);
        logger.info("Captured Password Placeholder: [{}]", passPlaceholder);
        logger.info("Captured Sign In Button Text: [{}]", buttonText);
        logger.info("Captured Forgot Password Text: [{}]", forgotText);
        logger.info("Captured Footer Text: [{}]", footerText);

//        Assert.assertNotNull(userPlaceholder, "Username placeholder text is empty or missing!");
//        Assert.assertNotNull(passPlaceholder, "Password placeholder text is empty or missing!");

        Assert.assertEquals(buttonText, "Sign In", "Sign In button text mismatch!");
        Assert.assertEquals(forgotText, "Forgot password?", "Forgot password link text mismatch!");

        String expectedFooter = "©2025-2026 Bar Code India Limited. All rights reserved.";
        Assert.assertEquals(footerText, expectedFooter, "Footer copyright text content mismatch!");

        boolean isHoverWorking = loginPage.isSignInButtonHoverable();
        Assert.assertTrue(isHoverWorking, "Hover functionality failed or inconsistent on the Sign In button!");

        logger.info("Login Screen UI Content and Hover validation test passed successfully!");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify Login Test"
    )
    @TestDetails(
            id = "OPTVS-TC-003",
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

        String username = data.getUsername();
        String password = data.getPassword();

        // Perform login action and capture the validation status
        boolean isLoginSuccessful = loginPage.login(username, password);

        // Assert that the login was successful and the dashboard is visible
        Assert.assertTrue(isLoginSuccessful, "Login Test Failed: Dashboard element is not visible after sign in!");

        logger.info("Login Test execution completed successfully");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify error message when logging in with invalid credentials"
    )
    @TestDetails(
            id = "OPTVS-TC-004",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyInvalidLoginErrorMessage(LoginPojo data) {

        String testId = data.getTcId();
        String runMode = data.getRunMode();

        logger.info("Checking Test ID & Run Mode [{}]: [{}]", testId, runMode);

        logger.info("Starting Invalid Login Error Message Validation for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1. Enter credentials (using data from Excel)
        String username = data.getUsername();
        String password = data.getPassword(); // This should be the invalid password for this row in Excel

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // 2. Validate that the error banner is displayed
        boolean isErrorVisible = loginPage.isErrorBannerVisible();
        Assert.assertTrue(isErrorVisible, "Validation Failed: Error banner alert is not visible on the screen!");

        // 3. Extract the actual message shown on the UI
        String actualErrorMessage = loginPage.getErrorMessage();
        logger.info("Captured UI Error Message: [{}]", actualErrorMessage);

        // 4. Fetch the expected message from Excel (or hardcode/compare against your requirement)
        String expectedErrorMessage = data.getExpectedErrorMessage();

        // Assert the exact message correctness
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error message text validation failed! Expected: '" + expectedErrorMessage + "' but found: '" + actualErrorMessage + "'");

        logger.info("Error message validation passed successfully.");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify error message when logging in with invalid Username and invalid Password"
    )
    @TestDetails(
            id = "OPTVS-TC-005",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyInvalidUsernameAndPasswordErrorMessage(LoginPojo data) {

        String testId = data.getTcId();
        String runMode = data.getRunMode();

        logger.info("Checking RunMode for Test ID & Run Mode [{}]: [{}]", testId, runMode);

        logger.info("Starting Invalid Username & Password Error Message Validation for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1. Enter invalid credentials from Excel (or test data row)
        String username = data.getUsername();
        String password = data.getPassword();

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // 2. Validate error banner visibility
        boolean isErrorVisible = loginPage.isErrorBannerVisible();
        Assert.assertTrue(isErrorVisible, "Validation Failed: Error banner alert is not visible for invalid credentials!");

        // 3. Capture the error message shown on UI
        String actualErrorMessage = loginPage.getErrorMessage();
        logger.info("Captured UI Error Message : [{}]", actualErrorMessage);

        // 4. Validate exact message correctness
        String expectedErrorMessage = data.getExpectedErrorMessage();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error message text validation failed! Expected: '" + expectedErrorMessage + "' but found: '" + actualErrorMessage + "'");

        logger.info("Invalid credentials error message validation passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify error message when logging in with leading or trailing spaces in the username"
    )
    @TestDetails(
            id = "OPTVS-TC-006",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyUsernameWithSpacesErrorMessage(LoginPojo data) {

        String testId = data.getTcId();
        String runMode = data.getRunMode();

        logger.info("Checking Test Id & Run Mode [{}]: [{}]", testId, runMode);

        logger.info("Starting Username with Spaces Validation for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1. Fetch valid username and password, then introduce leading/trailing spaces
        String rawUsername = data.getUsername();
        String usernameWithSpaces = "   " + rawUsername.trim() + "   ";
        String password = data.getPassword();

        logger.info("Injecting username with spaces: [{}]", usernameWithSpaces);

        loginPage.enterUsername(usernameWithSpaces);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // 2. Validate error banner visibility
        boolean isErrorVisible = loginPage.isErrorBannerVisible();
        Assert.assertTrue(isErrorVisible, "Validation Failed: Error banner alert is not visible for username with spaces!");

        // 3. Capture the error message shown on UI
        String actualErrorMessage = loginPage.getErrorMessage();
        logger.info("Captured UI Error Msg: [{}]", actualErrorMessage);

        // 4. Validate exact message correctness
        String expectedErrorMessage = data.getExpectedErrorMessage();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error message text validation failed! Expected: '" + expectedErrorMessage + "' but found: '" + actualErrorMessage + "'");

        logger.info("Username with spaces validation passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify login attempt with blank fields triggers validation or error handling"
    )
    @TestDetails(
            id = "OPTVS-TC-007",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyLoginButtonBlankFieldsBehavior(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Blank Fields Validation Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // Scenario A: Both fields completely blank, then click Sign In
        logger.info("Scenario 1: Leaving fields blank and clicking Sign In.");
        loginPage.clickSignIn();

        // Verify that the dashboard is NOT visible (user remains on login page)
        boolean isDashboardShown = loginPage.isDashboardVisible();
        Assert.assertFalse(isDashboardShown, "Validation Failed: User should not be logged in with blank fields!");

        // Scenario B: Enter value in only Username field, leave Password blank, click Sign In
        logger.info("Scenario 2: Entering only Username, leaving Password blank, and clicking Sign In.");
        loginPage.enterUsername(data.getUsername());
        loginPage.clickSignIn();

        Assert.assertFalse(loginPage.isDashboardVisible(), "Validation Failed: User should not log in with a blank password!");

        // Clear username field using POM method
        loginPage.clearUsername();

        // Scenario C: Enter value in only Password field, leave Username blank, click Sign In
        logger.info("Scenario 3: Entering only Password, leaving Username blank, and clicking Sign In.");
        loginPage.enterPassword(data.getPassword());
        loginPage.clickSignIn();

        Assert.assertFalse(loginPage.isDashboardVisible(), "Validation Failed: User should not log in with a blank username!");

        logger.info("Blank Fields validation test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify application behavior when launching URL in another tab with an active session"
    )
    @TestDetails(
            id = "OPTVS-TC-008",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyMultiTabSessionPersistence(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Multi-Tab Session Persistence Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1. Enter valid credentials and login as User A
        String username = data.getUsername();
        String password = data.getPassword();

        logger.info("Logging in with User A: [{}]", username);
        boolean isLoginSuccessful = loginPage.login(username, password);
        Assert.assertTrue(isLoginSuccessful, "Setup Failed: User A login was unsuccessful!");

        // Capture the application URL from the current session
        String appUrl = BaseClass.getPage().url();
        logger.info("Current active URL after login: [{}]", appUrl);

        // 2. Launch the application URL in another tab using try-with-resources
        logger.info("Opening a new browser tab in the current session.");
        try (Page newTab = BaseClass.getPage().context().newPage()) {

            logger.info("Navigating to application URL in the new tab: [{}]", appUrl);
            newTab.navigate(appUrl);

            // 3. Verify dashboard visibility in the new tab via Page Object encapsulation
            boolean isDashboardVisibleInNewTab = loginPage.isDashboardVisibleInTab(newTab);

            Assert.assertTrue(isDashboardVisibleInNewTab,
                    "Multi-Tab Session Persistence Failed: New tab did not open the dashboard for the logged-in user!");

            logger.info("Multi-Tab Session Persistence validation passed successfully for Test ID: [{}]", testId);
        }
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify navigation from Login screen to Forgot Password screen"
    )
    @TestDetails(
            id = "OPTVS-TC-009",
            author = "Amit",
            priority = TestDetails.Priority.MEDIUM,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyForgotPasswordNavigation(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Verify Forgot Password Navigation Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1. Launch / Verify application is on the login screen
        logger.info("Verifying Forgot Password link visibility on the login screen");
        Assert.assertTrue(loginPage.isForgotPasswordVisible(),
                "Setup Failed: Forgot Password link is not visible on the login screen!");

        // 2. Click on the Forgot Password option
        loginPage.clickForgotPassword();

        // 3. Observe navigation and UI displayed for password reset request
        boolean isScreenVisible = loginPage.isForgotPasswordScreenVisible();
        Assert.assertTrue(isScreenVisible,
                "Navigation Failed: System did not navigate to the Forgot Password screen!");

        logger.info("Forgot Password navigation test passed successfully.");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify login is blocked for Inactive user and appropriate error message is shown"
    )
    @TestDetails(
            id = "OPTVS-TC-010",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyInactiveUserLoginBlocked(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Inactive User Login Block Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1 & 2 & 3. Enter Inactive User Credentials
        String username = data.getUsername();
        String password = data.getPassword();

        logger.info("Attempting login with inactive user: [{}]", username);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);

        // 4. Click Login button
        loginPage.clickSignIn();

        // 5. Verify system blocks login and displays the deactivation error message
        boolean isErrorVisible = loginPage.isErrorBannerVisible();
        Assert.assertTrue(isErrorVisible,
                "Validation Failed: Error banner is not visible for inactive user login!");

        String actualErrorMessage = loginPage.getErrorMessage();
        String expectedErrorMessage = data.getExpectedErrorMessage();

        logger.info("Captured error message: [{}]", actualErrorMessage);

        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error Message Mismatch!");

        logger.info("Inactive User Login Block test passed successfully.");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify login is blocked for Deleted user and appropriate error message is shown"
    )
    @TestDetails(
            id = "OPTVS-TC-011",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyDeletedUserLoginBlocked(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Deleted User Login Block Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1, 2 & 3. Enter Deleted User Credentials
        String username = data.getUsername();
        String password = data.getPassword();

        logger.info("Attempting login with deleted user: [{}]", username);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);

        // 4. Click Login button
        loginPage.clickSignIn();

        // 5. Verify system blocks login and displays the deletion error message
        boolean isErrorVisible = loginPage.isErrorBannerVisible();
        Assert.assertTrue(isErrorVisible,
                "Validation Failed: Error banner is not visible for deleted user login!");

        String actualErrorMessage = loginPage.getErrorMessage();
        String expectedErrorMessage = data.getExpectedErrorMessage();

        logger.info("Captured error message from UI: [{}]", actualErrorMessage);

        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error Message Mismatch!");

        logger.info("Deleted User Login Block test passed successfully.");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify Show/Hide password button functionality"
    )
    @TestDetails(
            id = "OPTVS-TC-012",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyPasswordVisibilityToggle(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Password Visibility Toggle Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();

        // 1 & 2. Launch / Enter valid username
        loginPage.enterUsername(data.getUsername());

        // 3. Enter password and verify it is masked (type="password")
        loginPage.enterPassword(data.getPassword());
        String initialType = loginPage.getPasswordInputType();
        Assert.assertEquals(initialType, "password",
                "Validation Failed: Password field should be masked by default!");

        // 4 & 5. Click show/hide button and verify password becomes visible (type="text")
        loginPage.clickPasswordToggle();
        String visibleType = loginPage.getPasswordInputType();
        Assert.assertEquals(visibleType, "text",
                "Validation Failed: Password field should be visible (type='text') after clicking show/hide!");

        // 6 & 7. Click again on show/hide button and verify password hides again (type="password")
        loginPage.clickPasswordToggle();
        String hiddenType = loginPage.getPasswordInputType();
        Assert.assertEquals(hiddenType, "password",
                "Validation Failed: Password field should be masked again after clicking toggle a second time!");

        logger.info("Password Visibility Toggle test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify successful logout functionality and redirection to login screen"
    )
    @TestDetails(
            id = "OPTVS-TC-013",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyLogoutFunctionality(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Logout Functionality Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        LogoutPage logoutPage = new LogoutPage();

        // 1 & 2. Enter Valid Credentials
        logger.info("Attempting login with valid user: [{}]", data.getUsername());
        loginPage.enterUsername(data.getUsername());
        loginPage.enterPassword(data.getPassword());

        // 3. Click Login button
        loginPage.clickSignIn();

        // 4. Verify user moves to the dashboard screen
        boolean isDashboardVisible = logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardVisible,
                "Login Failed: User was not redirected to the dashboard, cannot proceed with logout test!");

        // 5. Click on logout icon
        logoutPage.clickLogout();

        // 6. Verify user is successfully logged out and returns back to the login screen
        boolean isLoginScreenVisible = logoutPage.isLoginScreenVisible();
        Assert.assertTrue(isLoginScreenVisible,
                "Logout Failed: User was not redirected back to the login screen after clicking logout!");

        logger.info("Logout Functionality test passed successfully.");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify back button access after logout does not allow returning to dashboard"
    )
    @TestDetails(
            id = "OPTVS-TC-014",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyBackButtonAccessAfterLogout(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Back Button Access After Logout Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        LogoutPage logoutPage = new LogoutPage();

        // 1 & 2. Enter Valid Credentials
        logger.info("Attempting login with valid username: [{}]", data.getUsername());
        loginPage.enterUsername(data.getUsername());
        loginPage.enterPassword(data.getPassword());

        // 3. Click Login button
        loginPage.clickSignIn();

        // 4. Verify user moves to the dashboard screen
        boolean isDashboardVisible = logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardVisible,
                "Login Failed: User was not redirected to the dashboard!");

        // 5. Click on logout icon
        logoutPage.clickLogout();

        // Verify successfully logged out to login screen
        boolean isLoginScreenVisible = logoutPage.isLoginScreenVisible();
        Assert.assertTrue(isLoginScreenVisible,
                "Logout Failed: User was not redirected back to the login screen!");

        // 6. Click on browser back button
        logoutPage.clickBrowserBack();

        // Verify user remains on the login screen (and does NOT re-enter the dashboard)
        boolean isStillOnLoginScreen = logoutPage.isLoginScreenVisible();
        Assert.assertTrue(isStillOnLoginScreen,
                "Security Breach / Failure: Clicking back button after logout allowed access back into the application!");

        // Optional extra assertion to ensure dashboard is NOT visible
        boolean isDashboardRestricted = !logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardRestricted,
                "Validation Failed: Dashboard elements are visible after clicking back button post-logout!");

        logger.info("Back Button Access After Logout test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify user can log in again using valid credentials after logging out"
    )
    @TestDetails(
            id = "OPTVS-TC-015",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyReLoginAfterLogout(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Re-Login After Logout Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        LogoutPage logoutPage = new LogoutPage();

        String username = data.getUsername();
        String password = data.getPassword();

        // 1 & 2. Enter Valid Credentials (First Login)
        logger.info("Attempting initial login with user: [{}]", username);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);

        // 3. Click Login button
        loginPage.clickSignIn();

        // 4. Verify user moves to the dashboard screen
        boolean isDashboardVisible = logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardVisible,
                "Initial Login Failed: User was not redirected to the dashboard!");

        // 5. Click on logout icon
        logoutPage.clickLogout();

        // Verify successfully returned to the login screen
        boolean isLoginScreenVisible = logoutPage.isLoginScreenVisible();
        Assert.assertTrue(isLoginScreenVisible,
                "Logout Failed: User was not redirected back to the login screen!");

        // 6. Enter valid credentials again and click login
        logger.info("Attempting re-login with user: [{}]", username);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // Verify user successfully logs in and returns to the dashboard again
        boolean isDashboardVisibleAfterReLogin = logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardVisibleAfterReLogin,
                "Re-Login Failed: User was not able to log back into the dashboard after previous logout!");

        logger.info("Re-Login After Logout test passed successfully.");
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify user remains on the login screen after refreshing the browser post-logout"
    )
    @TestDetails(
            id = "OPTVS-TC-016",
            author = "Amit",
            priority = TestDetails.Priority.MEDIUM,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyLogoutSessionAfterRefresh(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Logout Session Validation After Refresh Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        LogoutPage logoutPage = new LogoutPage();

        // 1 & 2. Enter Valid Credentials
        logger.info("Attempting login with valid username : [{}]", data.getUsername());
        loginPage.enterUsername(data.getUsername());
        loginPage.enterPassword(data.getPassword());

        // 3. Click Login button
        loginPage.clickSignIn();

        // 4. Verify user moves to the dashboard screen
        boolean isDashboardVisible = logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardVisible,
                "Login Failed: User was not redirected to the dashboard!");

        // 5. Click on logout icon
        logoutPage.clickLogout();

        // Verify successfully returned to the login screen
        boolean isLoginScreenVisible = logoutPage.isLoginScreenVisible();
        Assert.assertTrue(isLoginScreenVisible,
                "Logout Failed: User was not redirected back to the login screen!");

        // 6. Refresh the browser
        loginPage.refreshBrowser();

        // Verify user still remains on the login screen and dashboard is not re-exposed
        boolean isStillOnLoginScreen = logoutPage.isLoginScreenVisible();
        Assert.assertTrue(isStillOnLoginScreen,
                "Session Validation Failed: User was redirected or lost state incorrectly after page refresh!");

        boolean isDashboardRestricted = !logoutPage.isDashboardVisible();
        Assert.assertTrue(isDashboardRestricted,
                "Security Breach / Failure: Dashboard became visible again after refreshing the login screen!");

        logger.info("Logout Session Validation After Refresh test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            enabled = false,
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify auto logout functionality when logged into Chrome and then Firefox"

    )
    @TestDetails(
            id = "OPTVS-TC-017",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome", "firefox"},
            device = "Desktop"
    )
    public void verifyAutoLogoutFunctionality(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Auto Logout Functionality Test across Chrome and Firefox for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        LogoutPage logoutPage = new LogoutPage();

        String username = data.getUsername();
        String password = data.getPassword();

        // 1. Launch Firefox as the second independent browser
        logger.info("Step 1: Launching Firefox as the second independent browser");
        Playwright playwright = Playwright.create();
        Browser secondaryBrowser = playwright.firefox().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        com.microsoft.playwright.BrowserContext secondaryContext = secondaryBrowser.newContext();
        com.microsoft.playwright.Page secondaryPage = secondaryContext.newPage();

        try {
            // Get current app URL from primary browser (Chrome)
            String appUrl = BaseClass.getPage().url();
            if (!appUrl.contains("/login")) {
                appUrl = appUrl.split("/")[0] + "//" + appUrl.split("/")[2] + "/login";
            }

            // Navigate Firefox secondary browser to the application URL
            secondaryPage.navigate(appUrl);
            logger.info("Navigated Firefox secondary browser to: [{}]", appUrl);

            // 2 & 3. Enter valid username, password, and click Login on Chrome (Primary)
            logger.info("Step 2 & 3: Entering credentials and logging in on Browser 1 (Chrome - Primary)");
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickSignIn();

            // Verify Chrome is successfully on the dashboard
            boolean isDashboardVisible = logoutPage.isDashboardVisible();
            Assert.assertTrue(isDashboardVisible, "Chrome (Browser 1) Login Failed: Dashboard not visible!");

            // Now log in using the exact same credentials on Firefox (Secondary Browser) with your updated locators
            logger.info("Logging in on Browser 2 (Firefox - Secondary Browser) with the same user");
            secondaryPage.locator("input[name='username']").fill(username);
            secondaryPage.locator("input[name='password']").fill(password);
            secondaryPage.locator("button.login-button[type='submit']").click();

            // Allow adequate time for backend session override/invalidation
            secondaryPage.waitForTimeout(5000);

            // 4. System is idle / session updates, then verify Chrome (Browser 1) auto-logs out
            logger.info("Step 4: System is idle. Refreshing Chrome (Browser 1) to check auto-logout status");
            BaseClass.getPage().reload();

            // Verify that Chrome has been automatically logged out and returned to the login screen
            boolean isPrimaryOnLoginScreen = logoutPage.isLoginScreenVisible();
            Assert.assertTrue(isPrimaryOnLoginScreen,
                    "Auto Logout Failed: User on Chrome remained logged in after logging in from Firefox!");

            logger.info("Auto Logout Functionality test passed successfully for Test ID: [{}]", testId);

        } finally {
            // Clean up Firefox resources and Playwright instance
            secondaryContext.close();
            secondaryBrowser.close();
            playwright.close();
            logger.info("Firefox secondary browser instance and Playwright resources closed successfully.");
        }
    }

    @Test(
            enabled = false,
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify all UI content, text correctness, labels, placeholders, footer, and hover consistency on the Forgot Password screen"
    )
    @TestDetails(
            id = "OPTVS-TC-018",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyForgotPasswordScreenUIContent(LoginPojo data) {
        logger.info("Starting Comprehensive Forgot Password Screen UI Content & Validation Test");

        String testId = data.getTcId();
        logger.info("Checking RunMode for Test ID [{}]: [{}]", testId, data.getRunMode());

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "UI Validation Failed: 'Forgot Password' link is missing on login screen!");
        loginPage.clickForgotPassword();

        // 3. Verify the presence of all UI elements on the Forgot Password screen
        Assert.assertTrue(forgotPasswordPage.isAppLogoVisible(), "UI Validation Failed: Application logo is missing on Forgot Password screen!");
        Assert.assertTrue(forgotPasswordPage.isPageHeadingVisible(), "UI Validation Failed: Forgot Password page heading is missing!");
        Assert.assertTrue(forgotPasswordPage.isUsernameLabelVisible(), "UI Validation Failed: Username field label is missing!");
        Assert.assertTrue(forgotPasswordPage.isUsernameInputVisible(), "UI Validation Failed: Username input field is missing!");
        Assert.assertTrue(forgotPasswordPage.isSubmitButtonVisible(), "UI Validation Failed: Submit button is missing!");
//        Assert.assertTrue(forgotPasswordPage.isCloseButtonVisible(), "UI Validation Failed: Close button is missing!");
        Assert.assertTrue(forgotPasswordPage.isFooterTextVisible(), "UI Validation Failed: Footer text / copyright info is missing!");

        // Capture text details for correctness validation
        String pageHeadingText = forgotPasswordPage.getPageHeadingText();
        String usernamePlaceholder = forgotPasswordPage.getUsernamePlaceholder();
        String submitButtonText = forgotPasswordPage.getSubmitButtonText();
        String footerText = forgotPasswordPage.getFooterText();

        logger.info("Captured Forgot Password Page Heading: [{}]", pageHeadingText);
        logger.info("Captured Username Placeholder: [{}]", usernamePlaceholder);
        logger.info("Captured Submit Button Text: [{}]", submitButtonText);
        logger.info("Captured Footer Text: [{}]", footerText);

        // 4. Text correctness & assertions (Adjust expected text strings as per your exact application build if needed)
        Assert.assertEquals(pageHeadingText, "Forgot Password", "Forgot Password page heading text mismatch!");
        Assert.assertEquals(submitButtonText, "Submit", "Submit button text mismatch!");

        String expectedFooter = "©2025-2026 Bar Code India Limited. All rights reserved.";
        Assert.assertEquals(footerText, expectedFooter, "Footer copyright text content mismatch!");

        // 5. Verify button hover/interactive consistency
        // 4. Verify button hover/interactive consistency
        boolean isSubmitHoverWorking = forgotPasswordPage.isSubmitButtonHoverable();
        Assert.assertTrue(isSubmitHoverWorking, "Hover functionality failed or inconsistent on the Submit button!");

        boolean isCloseHoverWorking = forgotPasswordPage.isCloseButtonHoverable();
        Assert.assertTrue(isCloseHoverWorking, "Hover functionality failed or inconsistent on the Close button!");

        logger.info("Forgot Password Screen UI Content and Validation test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify validation error when username field is blank on Forgot Password screen"
    )
    @TestDetails(
            id = "OPTVS-TC-019",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyForgotPasswordBlankUsernameValidation(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Forgot Password Blank Username Validation Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Forgot Password link is not visible!");
        loginPage.clickForgotPassword();

        // 3 & 4. Leave Username field blank
        forgotPasswordPage.clearUsername();

        // 5. Click on the submit / send reset code button
        forgotPasswordPage.clickSubmit();

        // 6. Verify validation error appears
        boolean isErrorVisible = forgotPasswordPage.isErrorMessageVisible();
        Assert.assertTrue(isErrorVisible,
                "Validation Failed: Error message alert is not visible when leaving username blank!");

        // Capture actual error message and compare with expected text
        String actualErrorMessage = forgotPasswordPage.getErrorMessage();
        String expectedErrorMessage = data.getExpectedErrorMessage();

        logger.info("Captured Forgot Password Error Message: [{}]", actualErrorMessage);

        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error message text validation failed! Expected: '" + expectedErrorMessage + "' but found: '" + actualErrorMessage + "'");

        logger.info("Forgot Password Blank Username Validation test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify successful Send Reset Code functionality with a valid username"
    )
    @TestDetails(
            id = "OPTVS-TC-020",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifySendResetCodeSuccess(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Send Reset Code Success Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Forgot Password link is not visible on login screen!");
        loginPage.clickForgotPassword();

        // 3 & 4. Enter a valid username (e.g., Admin)
        String validUsername = data.getUsername();
        logger.info("Entering valid username for password reset: [{}]", validUsername);
        forgotPasswordPage.enterUsername(validUsername);

        // 5. Click on the Send Reset Code / Submit button
        forgotPasswordPage.clickSubmit();

        // 6. Verify response message container is visible
        boolean isMessageVisible = forgotPasswordPage.isResponseMessageVisible();
        Assert.assertTrue(isMessageVisible,
                "Validation Failed: Response message paragraph is not visible after submitting reset request!");

        // Capture actual message text (includes paragraph + nested link text)
        String actualMessage = forgotPasswordPage.getResponseMessage();
        logger.info("Captured Response Message: [{}]", actualMessage);

        // Build expected dynamic text snippet based on the username entered
        String expectedCoreMessage = "If " + validUsername + " has an email address on file, a 6-digit code is on its way. Enter it below and choose a new password.";

        // Assert that the response contains the expected message
        Assert.assertTrue(actualMessage.contains(expectedCoreMessage),
                "Reset code confirmation message text mismatch! Expected to contain: '" + expectedCoreMessage + "' but found: '" + actualMessage + "'");

        logger.info("Send Reset Code Success test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            enabled = false,
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify 'Enter Reset Code' button functionality redirects to the Reset Password screen"
    )
    @TestDetails(
            id = "OPTVS-TC-021",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.CRITICAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyEnterResetCodeButtonFunctionality(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Enter Reset Code Button Functionality Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Forgot Password link is not visible!");
        loginPage.clickForgotPassword();

        // 3. Enter a valid username and click Send Reset Code button
        String validUsername = data.getUsername();
        logger.info("Entering username: [{}]", validUsername);
        forgotPasswordPage.enterUsername(validUsername);
        forgotPasswordPage.clickSubmit();

        // 4. Verify confirmation/response message appears
        Assert.assertTrue(forgotPasswordPage.isResponseMessageVisible(),
                "Validation Failed: Response message is not visible after submitting reset request!");

        // 5. Click on the 'Enter Reset Code' button/link
        forgotPasswordPage.clickEnterResetCodeButton();

        // 6. Verify that the Reset Password screen appears
        boolean isResetPasswordScreenVisible = forgotPasswordPage.isResetPasswordScreenVisible();
        Assert.assertTrue(isResetPasswordScreenVisible,
                "Navigation Failed: Reset Password screen did not appear after clicking 'Enter Reset Code'!");

        logger.info("Enter Reset Code Button Functionality test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify Forgot Password functionality with an invalid username"
    )
    @TestDetails(
            id = "OPTVS-TC-022",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyForgotPasswordInvalidUsername(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Forgot Password Invalid Username Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Forgot Password link is not visible!");
        loginPage.clickForgotPassword();

        // 4. Enter the invalid username in the username field (fetched from Excel row data)
        String invalidUsername = data.getUsername();
        logger.info("Entering invalid username for password reset: [{}]", invalidUsername);
        forgotPasswordPage.enterUsername(invalidUsername);

        // 5. Click on the Send Reset Code button
        forgotPasswordPage.clickSubmit();

        // 6. Verify that the system blocks the request and displays an error message
        boolean isErrorVisible = forgotPasswordPage.isErrorMessageVisible();
        Assert.assertTrue(isErrorVisible,
                "Validation Failed: Error message banner is not visible when submitting an invalid username!");

        // Capture actual error message and compare against expected text from Excel
        String actualErrorMessage = forgotPasswordPage.getErrorMessage();
        String expectedErrorMessage = data.getExpectedErrorMessage();

        logger.info("Captured Forgot Password Error Message: [{}]", actualErrorMessage);

        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error message text validation failed! Expected: '" + expectedErrorMessage + "' but found: '" + actualErrorMessage + "'");

        logger.info("Forgot Password Invalid Username test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify application behavior when same user sends password reset request again"
    )
    @TestDetails(
            id = "OPTVS-TC-023",
            author = "Amit",
            priority = TestDetails.Priority.HIGH,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyDuplicatePasswordResetRequest(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Duplicate Password Reset Request Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Forgot Password link is not visible!");
        loginPage.clickForgotPassword();

        String username = data.getUsername();

        // 3, 4 & 5. Enter valid username and submit the first password reset request
        logger.info("Submitting first password reset request for user: [{}]", username);
        forgotPasswordPage.enterUsername(username);
        forgotPasswordPage.clickSubmit();

        // Verify first request success/response state appears
        Assert.assertTrue(forgotPasswordPage.isResponseMessageVisible(),
                "Setup Failed: Response message for the first request is not visible!");

        // 6. Click 'Back to sign in', then click 'Forgot Password' again to return to the input form
        logger.info("Navigating back to sign in, then re-opening forgot password to test duplicate request.");
        forgotPasswordPage.clickBackToSignIn();

        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Failed to return to login screen!");
        loginPage.clickForgotPassword();

        // Enter the same username again
        forgotPasswordPage.enterUsername(username);

        // 7. Click on Submit button again
        forgotPasswordPage.clickSubmit();

        // Verify that an appropriate error/restriction message is displayed
        boolean isErrorVisible = forgotPasswordPage.isErrorMessageVisible();
        Assert.assertTrue(isErrorVisible,
                "Validation Failed: Error message banner is not visible when submitting a duplicate password reset request!");

        // Capture actual error message and compare against expected text from Excel
        String actualErrorMessage = forgotPasswordPage.getErrorMessage();
        String expectedErrorMessage = data.getExpectedErrorMessage();

        logger.info("Captured Duplicate Request Error Message: [{}]", actualErrorMessage);

        Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
                "Error message text validation failed! Expected: '" + expectedErrorMessage + "' but found: '" + actualErrorMessage + "'");

        logger.info("Duplicate Password Reset Request test passed successfully for Test ID: [{}]", testId);
    }

    @Test(
            dataProvider = "OPTIVUSLOGINDATA",
            dataProviderClass = DataProviders.class,
            description = "Verify Forgot password functionality on page refresh"
    )
    @TestDetails(
            id = "OPTVS-TC-024",
            author = "Amit",
            priority = TestDetails.Priority.MEDIUM,
            severity = TestDetails.Severity.NORMAL,
            browsers = {"chrome"},
            device = "Desktop"
    )
    public void verifyForgotPasswordPageRefresh(LoginPojo data) {
        String testId = data.getTcId();
        logger.info("Executing Forgot Password Page Refresh Test for Test ID: [{}]", testId);

        LoginPage loginPage = new LoginPage();
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

        // 1 & 2. Ensure we are on the login screen and click the forgot password link
        Assert.assertTrue(loginPage.isForgotPasswordVisible(), "Setup Failed: Forgot Password link is not visible!");
        loginPage.clickForgotPassword();

        // 3 & 4. Enter a valid username in the username field
        String username = data.getUsername();
        logger.info("Entering username into Forgot Password screen: [{}]", username);
        forgotPasswordPage.enterUsername(username);

        // 5. Refresh the browser page
        logger.info("Refreshing the browser window while on the Forgot Password screen.");
        loginPage.refreshBrowser();

        // 6. Verify user should still be on the Forgot Password screen (heading and input elements visible)
        boolean isPageHeadingVisible = forgotPasswordPage.isPageHeadingVisible();
        Assert.assertTrue(isPageHeadingVisible,
                "Validation Failed: User was navigated away or forgot password page heading is missing after browser refresh!");

        boolean isInputVisible = forgotPasswordPage.isUsernameInputVisible();
        Assert.assertTrue(isInputVisible,
                "Validation Failed: Username input field is not visible after refreshing the page!");

        boolean isSubmitVisible = forgotPasswordPage.isSubmitButtonVisible();
        Assert.assertTrue(isSubmitVisible,
                "Validation Failed: Submit button is not visible after refreshing the page!");

        // 7. Verify input field is cleared / reset (no password reset request was persisted or auto-submitted)
        String inputFieldValue = forgotPasswordPage.getUsernameInputValue();
        Assert.assertEquals(inputFieldValue, "",
                "State Bug: Username input field retained value or request state was persisted upon refreshing the page!");

        logger.info("Forgot Password Page Refresh test passed successfully for Test ID: [{}]", testId);
    }


}
