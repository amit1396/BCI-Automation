package org.bci.pageobjects.optivusloginmodule;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bci.base.BaseClass;
import org.bci.base.BasePage;

public class LogoutPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(LogoutPage.class);

    // Locators
    private final Locator logoutButton;
    private final Locator loginScreenBackMsg;
    private final Locator dashboardField;

    // Constructor
    public LogoutPage() {
        super(BaseClass.getPage());
        Page page = BaseClass.getPage();

        // Initialize locators
        this.logoutButton = page.locator("button.logout-btn");
        this.loginScreenBackMsg = page.locator("h1.brand-title");
        this.dashboardField = page.locator("span.brand-text");
    }

    // Actions with Log4j Logging
    // Click on the logout button/icon
    public void clickLogout() {
        logger.info("Clicking on the Logout button");
        safeClick(logoutButton);
    }

    // Verify if user has successfully navigated to the dashboard
    public boolean isDashboardVisible() {
        logger.info("Verifying if dashboard is visible");
        return isElementVisibleAndVerified(dashboardField, "Dashboard Screen Indicator");
    }

    // Verify if user has returned to the login screen after logout
    public boolean isLoginScreenVisible() {
        logger.info("Verifying if login screen indicator is visible");
        return isElementVisibleAndVerified(loginScreenBackMsg, "Login Screen Indicator");
    }

    // Navigate back using the browser history (Back button)
    public void clickBrowserBack() {
        logger.info("Clicking the browser's back button");
        page.goBack();
        logger.info("Navigated back using browser history");
    }

}
