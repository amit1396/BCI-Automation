package org.bci.pageobjects.administrationmodule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bci.base.BaseClass;
import org.bci.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class AdministrationUserCreationPage extends BasePage {
	
	private static final Logger logger = LogManager.getLogger(AdministrationUserCreationPage.class);
	
	// Navigation & Validation Locators
	private final Locator administrationMenu;
	private final Locator usersSubMenu;
	private final Locator userFieldValidatorLocator;
	private final Locator addButtonField;
	private final Locator addUserPageValidationLocator;
	
	// Add User Personal Information Form Locators
	private final Locator userTypeDropdown;
	private final Locator selectRoleDropdown;
	private final Locator usernameInputFieldLocator;
	private final Locator firstNameInputFieldLocator;
	private final Locator lastNameInputFieldLocator;
	private final Locator userCodeInputFieldLocator;
	private final Locator mobileNumberInputFieldLocator;
	private final Locator emailInputFieldLocator;
	
	// Add User Security & Credentials Information Form Locators
	private final Locator newPasswordInputFieldLocator;
	private final Locator confirmPasswordInputFieldLocator;
	
	// Add User Employee Details Information Form Locators
	private final Locator employeeCodeInputFieldLocator;
	private final Locator designationInputFieldLocator;
	private final Locator departmentInputFieldLocator;
	private final Locator employeeTypeInputFieldLocator;
	private final Locator supervisorInputFieldLocator;
	private final Locator joiningDateInputFieldLocator;
	
	// Add User Identity & Security Information Form Locators
	private final Locator rfidTagInputFieldLocator;
	private final Locator biometricIDFieldLocator;
	private final Locator accountStatusFieldLocator;
	private final Locator mfaToggleBtnLocator;
	private final Locator mfaTypeFieldLocator;
	
	// Add User Certification & Skills Information Form Locators
	private final Locator skillFieldLocator;
	private final Locator proficiencyLvlFieldLocator;
	private final Locator expYearsInputFieldLocator;
	private final Locator certifiedOnDateInputFieldLocator;
	private final Locator certificationStatusInputFieldLocator;
	private final Locator certificationNumberInputFieldLocator;
	private final Locator assessmentScoreInputFieldLocator;
	private final Locator validTillDateFieldLocator;
	private final Locator exitDateFieldLocator;
	
	private final Locator saveButton;
	
	public AdministrationUserCreationPage() {
		super(BaseClass.getPage());
		Page page = BaseClass.getPage(); 

		// Initialize Navigation & Validation Locators 
		this.administrationMenu = page.locator("//button[@data-sidebar=\"menu-button\"]//span[contains(text(),'Administration')]");
		this.usersSubMenu = page.locator("//a[@data-slot='sidebar-menu-sub-button']//span[contains(text(),'Users')]");
		this.userFieldValidatorLocator = page.locator("//h1[contains(@class,'text')]");
		this.addButtonField = page.locator("//button[contains(text(),'Add')]");
		this.addUserPageValidationLocator = page.locator("//h1[contains(@class,'text')]"); 
		
		// Initialize Form Input & Dropdown Locators
		this.userTypeDropdown = page.locator("input[placeholder='Select User Type']");
		this.selectRoleDropdown = page.locator("input[placeholder='Select Role']");
		this.usernameInputFieldLocator = page.locator("//input[@formcontrolname='userName']");
		this.firstNameInputFieldLocator = page.locator("//input[@formcontrolname='firstName']");
		this.lastNameInputFieldLocator = page.locator("//input[@formcontrolname='lastName']");
		this.userCodeInputFieldLocator = page.locator("//input[@formcontrolname='userCode']");
		this.mobileNumberInputFieldLocator = page.locator("//input[@formcontrolname='mobileNumber']");
		this.emailInputFieldLocator = page.locator("//input[@formcontrolname='email']");
		this.newPasswordInputFieldLocator = page.locator("//input[@formcontrolname='newPassword']");
		this.confirmPasswordInputFieldLocator = page.locator("//input[@formcontrolname='confirmPassword']");
		this.employeeCodeInputFieldLocator = page.locator("//input[@formcontrolname='employeeCode']");
		this.designationInputFieldLocator = page.locator("input[placeholder='Select Designation']");
		this.departmentInputFieldLocator = page.locator("input[placeholder='Select Department']");
		this.employeeTypeInputFieldLocator = page.locator("input[placeholder='Select Employment Type']");
		this.supervisorInputFieldLocator = page.locator("input[placeholder='Select Supervisor']");
		this.joiningDateInputFieldLocator = page.locator("//input[@formcontrolname='joiningDate']");
		this.rfidTagInputFieldLocator = page.locator("//input[@formcontrolname='rfidTag']");
		this.biometricIDFieldLocator = page.locator("//input[@formcontrolname='biometricId']");
		this.accountStatusFieldLocator = page.locator("input[placeholder='Select Status']");
		this.mfaToggleBtnLocator = page.locator("(//div[.//label[contains(text(),'MFA Enabled')]]//button[@role='switch'])[2]");
		this.mfaTypeFieldLocator = page.locator("//select[@formcontrolname='mfaType']");
		this.skillFieldLocator = page.locator("input[placeholder='Select Skill']");
		this.proficiencyLvlFieldLocator = page.locator("input[placeholder='Select Proficiency Level']");
		this.expYearsInputFieldLocator = page.locator("//input[@formcontrolname='experienceYears']");
		this.certifiedOnDateInputFieldLocator = page.locator("//input[@formcontrolname='certifiedOn']");
		this.certificationStatusInputFieldLocator = page.locator("//input[@formcontrolname='certificationStatus']");
		this.certificationNumberInputFieldLocator = page.locator("//input[@formcontrolname='certificationNumber']");
		this.assessmentScoreInputFieldLocator = page.locator("//input[@formcontrolname='assessmentScore']");
		this.validTillDateFieldLocator = page.locator("//input[@formcontrolname='validTill']");
		this.exitDateFieldLocator = page.locator("//input[@formcontrolname='exitDate']");
		this.saveButton = page.locator("//button[@type='submit']");
	}

	// ====================== Navigation Actions ======================

	public void clickAdministrationMenu() {
		logger.info("Clicking on Administration menu");
		highlightElement(administrationMenu);
		safeClick(administrationMenu);
		logger.info("Administration menu clicked successfully");
	}

	public void clickUsersSubMenu() {
		logger.info("Clicking on Users sub-menu");
		highlightElement(usersSubMenu);
		safeClick(usersSubMenu);
		logger.info("Users sub-menu clicked successfully");
	}

	public void clickOnAddUser() {
		logger.info("Clicking on Add User button");
		highlightElement(addButtonField);
		safeClick(addButtonField);
		logger.info("Add User button clicked successfully");
	}

	public void clickSaveButton() {
		logger.info("Clicking on Save button");
		highlightElement(saveButton);
		safeClick(saveButton);
		logger.info("Save button clicked successfully");
	}

	// ====================== Form Input & Dropdown Actions ======================

	public void selectUserType(String userType) {
		logger.info("Selecting User Type: {}", userType);
		highlightElement(userTypeDropdown);
		safeClick(userTypeDropdown);
		commonSendKeys(userTypeDropdown, userType);
		userTypeDropdown.press("Enter");
	}

	public void selectRole(String role) {
		logger.info("Selecting Role: {}", role);
		highlightElement(selectRoleDropdown);
		safeClick(selectRoleDropdown);
		commonSendKeys(selectRoleDropdown, role);
		selectRoleDropdown.press("Enter");
	}

	public void enterUsername(String username) {
		logger.info("Entering username: {}", username);
		highlightElement(usernameInputFieldLocator);
		commonSendKeys(usernameInputFieldLocator, username);
	}

	public void enterFirstName(String firstName) {
		logger.info("Entering first name: {}", firstName);
		highlightElement(firstNameInputFieldLocator);
		commonSendKeys(firstNameInputFieldLocator, firstName);
	}

	public void enterLastName(String lastName) {
		logger.info("Entering last name: {}", lastName);
		highlightElement(lastNameInputFieldLocator);
		commonSendKeys(lastNameInputFieldLocator, lastName);
	}

	public void enterUserCode(String userCode) {
		logger.info("Entering user code: {}", userCode);
		highlightElement(userCodeInputFieldLocator);
		commonSendKeys(userCodeInputFieldLocator, userCode);
	}

	public void enterMobileNumber(String mobileNumber) {
		logger.info("Entering mobile number: {}", mobileNumber);
		highlightElement(mobileNumberInputFieldLocator);
		commonSendKeys(mobileNumberInputFieldLocator, mobileNumber);
	}

	public void enterEmail(String email) {
		logger.info("Entering email: {}", email);
		highlightElement(emailInputFieldLocator);
		commonSendKeys(emailInputFieldLocator, email);
	}

	public void enterNewPassword(String password) {
		logger.info("Entering new password");
		highlightElement(newPasswordInputFieldLocator);
		commonSendKeys(newPasswordInputFieldLocator, password);
	}

	public void enterConfirmPassword(String confirmPassword) {
		logger.info("Entering confirm password");
		highlightElement(confirmPasswordInputFieldLocator);
		commonSendKeys(confirmPasswordInputFieldLocator, confirmPassword);
	}

	public void enterEmployeeCode(String employeeCode) {
		logger.info("Entering employee code: {}", employeeCode);
		highlightElement(employeeCodeInputFieldLocator);
		commonSendKeys(employeeCodeInputFieldLocator, employeeCode);
	}

	public void selectDesignation(String designation) {
		logger.info("Selecting Designation: {}", designation);
		highlightElement(designationInputFieldLocator);
		safeClick(designationInputFieldLocator);
		commonSendKeys(designationInputFieldLocator, designation);
		designationInputFieldLocator.press("Enter");
	}

	public void selectDepartment(String department) {
		logger.info("Selecting Department: {}", department);
		highlightElement(departmentInputFieldLocator);
		safeClick(departmentInputFieldLocator);
		commonSendKeys(departmentInputFieldLocator, department);
		departmentInputFieldLocator.press("Enter");
	}

	public void selectEmployeeType(String employeeType) {
		logger.info("Selecting Employment Type: {}", employeeType);
		highlightElement(employeeTypeInputFieldLocator);
		safeClick(employeeTypeInputFieldLocator);
		commonSendKeys(employeeTypeInputFieldLocator, employeeType);
		employeeTypeInputFieldLocator.press("Enter");
	}

	public void selectSupervisor(String supervisor) {
		logger.info("Selecting Supervisor: {}", supervisor);
		highlightElement(supervisorInputFieldLocator);
		safeClick(supervisorInputFieldLocator);
		commonSendKeys(supervisorInputFieldLocator, supervisor);
		supervisorInputFieldLocator.press("Enter");
	}

	private String formatDate(String dateStr) {
	    if (dateStr == null || dateStr.trim().isEmpty()) {
	        return dateStr;
	    }
	    
	    dateStr = dateStr.trim();

	    // Case 1: Format is DD/MM/YYYY (e.g., 01/01/2026)
	    if (dateStr.contains("/")) {
	        String[] parts = dateStr.split("/");
	        if (parts.length == 3) {
	            // Ensure 2-digit day/month and 4-digit year format
	            return parts[2] + "-" + parts[1] + "-" + parts[0];
	        }
	    }
	    
	    // Case 2: Format is continuous digits DDMMYYYY (e.g., 01012026)
	    if (dateStr.matches("\\d{8}")) {
	        String day = dateStr.substring(0, 2);
	        String month = dateStr.substring(2, 4);
	        String year = dateStr.substring(4, 8);
	        return year + "-" + month + "-" + day;
	    }

	    // Return as-is if it's already YYYY-MM-DD or doesn't match above patterns
	    return dateStr;
	}

	public void enterJoiningDate(String joiningDate) {
		logger.info("Entering Joining Date: {}", joiningDate);
		highlightElement(joiningDateInputFieldLocator);
		commonSendKeys(joiningDateInputFieldLocator, formatDate(joiningDate));
	}

	public void enterRfidTag(String rfidTag) {
		logger.info("Entering RFID Tag: {}", rfidTag);
		highlightElement(rfidTagInputFieldLocator);
		commonSendKeys(rfidTagInputFieldLocator, rfidTag);
	}

	public void enterBiometricId(String biometricId) {
		logger.info("Entering Biometric ID: {}", biometricId);
		highlightElement(biometricIDFieldLocator);
		commonSendKeys(biometricIDFieldLocator, biometricId);
	}

	public void selectAccountStatus(String status) {
		logger.info("Selecting Account Status: {}", status);
		highlightElement(accountStatusFieldLocator);
		safeClick(accountStatusFieldLocator);
		commonSendKeys(accountStatusFieldLocator, status);
		accountStatusFieldLocator.press("Enter");
	}

	public void toggleMfaSwitch() {
		logger.info("Toggling MFA Switch");
		highlightElement(mfaToggleBtnLocator);
		safeClick(mfaToggleBtnLocator);
	}

	public void selectMfaType(String mfaType) {
		logger.info("Selecting MFA Type: {}", mfaType);
		highlightElement(mfaTypeFieldLocator);
		mfaTypeFieldLocator.selectOption(mfaType);
	}

	public void selectSkill(String skill) {
		logger.info("Selecting Skill: {}", skill);
		highlightElement(skillFieldLocator);
		safeClick(skillFieldLocator);
		commonSendKeys(skillFieldLocator, skill);
		skillFieldLocator.press("Enter");
	}

	public void selectProficiencyLevel(String level) {
		logger.info("Selecting Proficiency Level: {}", level);
		highlightElement(proficiencyLvlFieldLocator);
		safeClick(proficiencyLvlFieldLocator);
		commonSendKeys(proficiencyLvlFieldLocator, level);
		proficiencyLvlFieldLocator.press("Enter");
	}

	public void enterExperienceYears(String years) {
		logger.info("Entering Experience Years: {}", years);
		highlightElement(expYearsInputFieldLocator);
		commonSendKeys(expYearsInputFieldLocator, years);
	}

	public void enterCertifiedOnDate(String date) {
		logger.info("Entering Certified On Date: {}", date);
		highlightElement(certifiedOnDateInputFieldLocator);
		commonSendKeys(certifiedOnDateInputFieldLocator, formatDate(date));
	}

	public void enterCertificationStatus(String status) {
		logger.info("Entering Certification Status: {}", status);
		highlightElement(certificationStatusInputFieldLocator);
		commonSendKeys(certificationStatusInputFieldLocator, status);
	}

	public void enterCertificationNumber(String certNumber) {
		logger.info("Entering Certification Number: {}", certNumber);
		highlightElement(certificationNumberInputFieldLocator);
		commonSendKeys(certificationNumberInputFieldLocator, certNumber);
	}

	public void enterAssessmentScore(String score) {
		logger.info("Entering Assessment Score: {}", score);
		highlightElement(assessmentScoreInputFieldLocator);
		commonSendKeys(assessmentScoreInputFieldLocator, score);
	}

	public void enterValidTillDate(String validTill) {
		logger.info("Entering Valid Till Date: {}", validTill);
		highlightElement(validTillDateFieldLocator);
		commonSendKeys(validTillDateFieldLocator, formatDate(validTill));
	}

	public void enterExitDate(String exitDate) {
		logger.info("Entering Exit Date: {}", exitDate);
		highlightElement(exitDateFieldLocator);
		commonSendKeys(exitDateFieldLocator, formatDate(exitDate));
	}

	// ====================== Validations ======================

	public boolean isUsersPageVisible() {
		return isElementVisibleAndVerified(userFieldValidatorLocator, "Users Page Header");
	}
	
	public boolean isAddUserPageVisible() {
	    return isElementVisibleAndVerified(addUserPageValidationLocator, "Add User Page Header");
	}
	
	// ====================== Combined Workflows ======================

	public boolean navigateToUsers() {
	    logger.info("Starting navigation to Administration -> Users");
	    clickAdministrationMenu();
	    clickUsersSubMenu();
	    return isUsersPageVisible();
	}

	public boolean navigateToAddUserPage() {
	    logger.info("Starting navigation to Add User form");
	    navigateToUsers(); 
	    clickOnAddUser();
	    return isAddUserPageVisible();
	}

	// Comprehensive workflow to fill out the complete Add User form across all sections and save
	public void fillAddUserForm(String userType, String role, String username, 
	                            String firstName, String lastName, String userCode, 
	                            String mobileNumber, String email, String password, 
	                            String confirmPassword, String employeeCode, 
	                            String designation, String department, String employeeType, 
	                            String supervisor, String joiningDate, String rfidTag, 
	                            String biometricId, String accountStatus, boolean enableMfa, 
	                            String mfaType, String skill, String proficiencyLevel, 
	                            String experienceYears, String certifiedOn, String certificationStatus, 
	                            String certificationNumber, String assessmentScore, String validTill, String exitDate) {
	    logger.info("Starting to fill out the complete Add User form (All Sections)...");
	    
	    // Personal Info
	    selectUserType(userType);
	    selectRole(role);
	    enterUsername(username);
	    enterFirstName(firstName);
	    enterLastName(lastName);
	    enterUserCode(userCode);
	    enterMobileNumber(mobileNumber);
	    enterEmail(email);
	    
	    // Security & Credentials
	    enterNewPassword(password);
	    enterConfirmPassword(confirmPassword);
	    
	    // Employee Details
	    enterEmployeeCode(employeeCode);
	    selectDesignation(designation);
	    selectDepartment(department);
	    selectEmployeeType(employeeType);
	    selectSupervisor(supervisor);
	    enterJoiningDate(joiningDate);
	    
	    // Identity & Security
	    enterRfidTag(rfidTag);
	    enterBiometricId(biometricId);
	    selectAccountStatus(accountStatus);
	    if (enableMfa) {
	        toggleMfaSwitch();
	        selectMfaType(mfaType);
	    }
	    
	    // Certification & Skills
	    selectSkill(skill);
	    selectProficiencyLevel(proficiencyLevel);
	    enterExperienceYears(experienceYears);
	    enterCertifiedOnDate(certifiedOn);
	    enterCertificationStatus(certificationStatus);
	    enterCertificationNumber(certificationNumber);
	    enterAssessmentScore(assessmentScore);
	    enterValidTillDate(validTill);
	    enterExitDate(exitDate);
	    
	    // Click Save to submit the complete form
	    clickSaveButton();
	    
	    logger.info("Complete Add User form filled and submitted successfully.");
	}
}