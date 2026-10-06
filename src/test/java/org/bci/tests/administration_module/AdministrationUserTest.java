package org.bci.tests.administration_module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.bci.base.BaseClass;
import org.bci.dataproviders.DataProviders;
import org.bci.pageobjects.administrationmodule.AdministrationUserCreationPage;
import org.bci.pageobjects.loginmodule.LoginPage;
import org.bci.pojoclasses.AdministrationUserPojo;
import org.bci.utilities.TestDetails;

public class AdministrationUserTest extends BaseClass {

	private static final Logger logger = LogManager.getLogger(AdministrationUserTest.class);

	@Test
	@TestDetails(
		id = "WMS-TC-0002",
		author = "Author 1",
		priority = TestDetails.Priority.HIGH,
		severity = TestDetails.Severity.NORMAL,
		browsers = {"chrome"},
		device = "Desktop"
	)
	public void testNavigateToUsers() {
		logger.info("Starting Administration Users Navigation Test execution");

		// 1. Initialize Page Objects
		LoginPage loginPage = new LoginPage();
		AdministrationUserCreationPage adminPage = new AdministrationUserCreationPage();

		// 2. Get credentials from configuration
		String username = getSecureConfigValue("demo.username", "DEMO_USERNAME", "username");
		String password = getSecureConfigValue("demo.password", "DEMO_PASSWORD", "password");

		// 3. Perform Login first to get into the application
		boolean isLoginSuccessful = loginPage.login(username, password);
		Assert.assertTrue(isLoginSuccessful, "Prerequisite Failed: Login failed before testing Administration navigation!");

		// 4. Execute the Administration -> Users navigation workflows
		boolean isUsersPageVisible = adminPage.navigateToUsers();
		
		// 5. Assert that the Users sub-page loaded successfully
		Assert.assertTrue(isUsersPageVisible, "Administration Test Failed: Users page validator element is not visible!");

		logger.info("Administration Users Navigation Test execution completed successfully");
	}
	
	@Test
	@TestDetails(
	    id = "WMS-TC-0003",
	    author = "Author 1",
	    priority = TestDetails.Priority.HIGH,
	    severity = TestDetails.Severity.NORMAL,
	    browsers = {"chrome"},
	    device = "Desktop"
	)
	public void testNavigateToAddUser() {
	    logger.info("Starting Add User Page Navigation Test execution");

	    LoginPage loginPage = new LoginPage();
	    AdministrationUserCreationPage adminPage = new AdministrationUserCreationPage();

	    String username = getSecureConfigValue("demo.username", "DEMO_USERNAME", "username");
	    String password = getSecureConfigValue("demo.password", "DEMO_PASSWORD", "password");

	    boolean isLoginSuccessful = loginPage.login(username, password);
	    Assert.assertTrue(isLoginSuccessful, "Prerequisite Failed: Login failed!");

	    // Execute the full navigation to the Add User form
	    boolean isAddUserPageVisible = adminPage.navigateToAddUserPage();
	    
	    Assert.assertTrue(isAddUserPageVisible, "Administration Test Failed: Add User page header is not visible!");

	    logger.info("Add User Page Navigation Test execution completed successfully");
	}
	
	@Test(
			dataProvider = "UsersData",
			dataProviderClass = DataProviders.class,
			description = "Administration Menu - Create New User Test"
		)
	
    @TestDetails(
        id = "WMS-TC-0004",
        author = "Amit",
        priority = TestDetails.Priority.CRITICAL,
        severity = TestDetails.Severity.BLOCKER,
        browsers = {"chrome"},
        device = "Desktop"
    )
    public void testCreateNewUser(AdministrationUserPojo data) {
        logger.info("Starting Create New User Test execution");

        LoginPage loginPage = new LoginPage();
        AdministrationUserCreationPage adminPage = new AdministrationUserCreationPage();

        String username = getSecureConfigValue("demo.username", "DEMO_USERNAME", "username");
        String password = getSecureConfigValue("demo.password", "DEMO_PASSWORD", "password");

        // 1. Login Prerequisite
        boolean isLoginSuccessful = loginPage.login(username, password);
        Assert.assertTrue(isLoginSuccessful, "Prerequisite Failed: Login failed!");

        // 2. Navigate to Add User Form
        boolean isAddUserPageVisible = adminPage.navigateToAddUserPage();
        Assert.assertTrue(isAddUserPageVisible, "Navigation Failed: Add User page header is not visible!");

        // 3. Fill out the complete form
        
        adminPage.fillAddUserForm(
                data.getUserType(),          // userType
                data.getRole(),              // role
                data.getUsername(),          // username
                data.getFirstName(),         // firstName
                data.getLastName(),          // lastName
                data.getUserCode(),          // userCode
                data.getMobileNumber(),      // mobileNumber
                data.getEmail(),             // email
                data.getPassword(),          // password
                data.getConfirmPassword(),   // confirmPassword
                data.getEmployeeCode(),      // employeeCode
                data.getDesignation(),       // designation
                data.getDepartment(),        // department
                data.getEmployeeType(),      // employeeType
                data.getSupervisor(),        // supervisor
                data.getJoiningDate(),       // joiningDate
                data.getRfidTag(),           // rfidTag
                data.getBiometricId(),       // biometricId
                data.getAccountStatus(),     // accountStatus
                data.isEnableMfa(),          // enableMfa (boolean)
                data.getMfaType(),           // mfaType
                data.getSkill(),             // skill
                data.getProficiencyLevel(),  // proficiencyLevel
                data.getExperienceYears(),   // experienceYears
                data.getCertifiedOn(),       // certifiedOn
                data.getCertificationStatus(),// certificationStatus
                data.getCertificationNumber(),// certificationNumber
                data.getAssessmentScore(),   // assessmentScore
                data.getValidTill(),         // validTill
                data.getExitDate()           // exitDate
            );

        logger.info("Create New User Test execution completed successfully");
    }
}