package org.bci.pageobjects.loginmodule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bci.base.BaseClass;
import org.bci.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

public class LogoutPage extends BasePage {

	private static final Logger logger = LogManager.getLogger(LogoutPage.class);

	// Locators
	private final Locator signOutButton;
	private final Locator loginScreenBackMsg;
	private final Locator dashboardField;

	// Constructor
	public LogoutPage() {
		super(BaseClass.getPage());
		Page page = BaseClass.getPage();

		// Initialize locators
		this.signOutButton = page.locator("//button[@title='Sign Out']");
		this.loginScreenBackMsg = page.locator("//h2[contains(text(),'Welcome back')]");
		this.dashboardField = page.locator("//span[@aria-current='page']");
	}

	// Dynamic locator for the goodbye message based on username
	public Locator getGoodbyeMessageLocator(String username) {
		return BaseClass.getPage().locator(String.format("//*[contains(text(), 'Goodbye, %s !')]", username));
	}

	public Locator getGoodbyeMessageLocatorWithUsername(String username) {
		// This safely matches "Goodbye, " followed by the username, ignoring extra
		// whitespace
		String xpath = String.format("//*[contains(normalize-space(text()), 'Goodbye, %s')]", username);
		return BaseClass.getPage().locator(xpath);
	}

	// Actions with Log4j Logging and Validation
	public void clickSignOut() {
		logger.info("Clicking 'Sign Out' button");
		safeClick(signOutButton);
		logger.info("'Sign Out' button clicked successfully");
	}

	public void verifySuccessfulLogout(String username) {
		logger.info("Verifying successful logout for user: {}", username);

		// Verify goodbye message appears
		Locator goodbyeMsg = getGoodbyeMessageLocatorWithUsername(username);
		assertThat(goodbyeMsg).isVisible();
		logger.info("Goodbye message 'Goodbye, {} !' verified successfully", username);

		// Verify return to login screen
		assertThat(loginScreenBackMsg).isVisible();
		logger.info("Successfully returned to the login screen");
	}

	// Security Check
	public void verifyBackButtonSecurity() {
		logger.info("Simulating browser 'Back' button action...");
		BaseClass.getPage().goBack();

		logger.info("Verifying that the dashboard element is NOT visible after clicking back...");
		// 1. Assert that the dashboard is NOT visible (session is cleared)
		assertThat(dashboardField).not().isVisible();

		logger.info("Verifying that the user has successfully landed back on the login screen...");
		// 2. Assert that we ARE back on the login page (Welcome back heading IS
		// visible)
		assertThat(loginScreenBackMsg).isVisible();

		logger.info("Verified: Back button did not bypass security. User remains safely on the login screen.");
	}

	// Reusable security check for Back Button and Browser Refresh
    public void verifySessionIsTerminatedAfterBack() {
        logger.info("Verifying session is terminated (Security check)...");
        
        // Assert that the dashboard is NOT visible (meaning session is cleared)
        assertThat(dashboardField).not().isVisible();
        
        // Assert that we are safely back on the login page (Welcome back heading IS visible)
        assertThat(loginScreenBackMsg).isVisible();
        
        logger.info("Session security verified: User remains securely on the login screen.");
    }
}