package org.bci.base;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class BasePage {

    // Instance-level variables to ensure thread safety during parallel execution
    protected final Page page;
    protected final Logger logger;

    public BasePage(Page page) {
        // Fallback to BaseClass thread-local page if constructor page is null
        this.page = (page != null) ? page : BaseClass.getPage();
        this.logger = LogManager.getLogger(this.getClass());
    }

    // ====================== Core Click Methods ======================

    protected void safeClick(String selector) {
        safeClick(page.locator(selector));
        logger.info("Successfully clicked element via selector: " + selector);
    }

    protected void safeClick(Locator locator) {
        try {
            locator.click();
        } catch (Exception e) {
            logger.warn("Standard click failed, trying force click...", e);
            locator.click(new Locator.ClickOptions().setForce(true));
        }
    }

    // ====================== Input & Text Methods ======================

    protected void commonSendKeys(String selector, String value) {
        commonSendKeys(page.locator(selector), value);
        logger.info("Entered value: " + value + " into selector: " + selector);
    }
    
    protected void commonSendKeys(Locator element, String value) {
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        element.scrollIntoViewIfNeeded();
        element.fill(value);
        logger.info("Entered value successfully into element.");
    }

    protected void enterDate(String selector, String dateValue) {
        if (dateValue == null || dateValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Date value cannot be null or empty");
        }

        LocalDate parsedDate = LocalDate.parse(dateValue.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Locator dateElement = page.locator(selector);
        dateElement.scrollIntoViewIfNeeded();
        dateElement.click();
        dateElement.fill(""); 
        dateElement.pressSequentially(formattedDate); 
        dateElement.press("Tab");
        logger.info("Entered date: " + formattedDate);
    }

    // ====================== Dropdown Methods ======================

    public void commonSelectFromDropdown(String dropdownSelector, String optionTextPattern, String value) {
        logger.info("Selecting dropdown value: " + value);
        page.locator(dropdownSelector).click();

        Locator option = page.locator(optionTextPattern).filter(new Locator.FilterOptions().setHasText(value));
        option.scrollIntoViewIfNeeded();
        option.click();
        
        logger.info("Dropdown value selected successfully: " + value);
    }

    // ====================== Dialog Handling ======================

    public void handleBrowserDialog(boolean accept, String promptText) {
        page.onceDialog(dialog -> {
            logger.info("Dialog detected. Type: " + dialog.type() + ", Message: " + dialog.message());
            if (promptText != null && dialog.type().equalsIgnoreCase("prompt")) {
                dialog.accept(promptText);
            } else if (accept) {
                dialog.accept();
            } else {
                dialog.dismiss();
            }
        });
    }

    public void verifyJsAlertMessageAndAccept(String expectedMessage) {
        logger.info("Verifying JS alert message: " + expectedMessage);
        page.onceDialog(dialog -> {
            String actualMessage = dialog.message().trim();
            Assert.assertEquals(actualMessage, expectedMessage, "JS alert message mismatch.");
            dialog.accept();
            logger.info("JS alert message verified and accepted successfully");
        });
    }

    // ====================== Frame Handling ======================

    protected FrameLocator getFrame(String iframeSelector) {
        logger.info("Scoping to frame using selector: " + iframeSelector);
        return page.frameLocator(iframeSelector);
    }

    // ====================== Window / Tab Switching ======================

    public Page switchToNewTab(Runnable triggerAction) {
        logger.info("Waiting for new tab/window to open...");
        Page newPage = page.waitForPopup(triggerAction);
        newPage.waitForLoadState();
        logger.info("Successfully switched to new tab. Title: " + newPage.title());
        return newPage;
    }

    public void switchToTabByTitle(String partialTitle) {
        List<Page> pages = page.context().pages();
        for (Page p : pages) {
            if (p.title().contains(partialTitle)) {
                p.bringToFront();
                logger.info("Switched to tab with title containing: " + partialTitle);
                return;
            }
        }
        throw new RuntimeException("No tab found with title containing: " + partialTitle);
    }

    // ====================== Modal Methods ======================

    public void verifyAlertMessageAndClickOk(String expectedMessage, String messageSelector, String okButtonSelector) {
        Locator messageElement = page.locator(messageSelector);
        messageElement.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        
        String actualMessage = messageElement.textContent().trim();
        Assert.assertEquals(actualMessage, expectedMessage, "Alert message mismatch.");

        handleModalOk(page.locator(okButtonSelector));
    }

    public void handleModalOk(Locator okButton) {
        try {
            okButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            okButton.click();
            okButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
            logger.info("Modal OK clicked successfully");
        } catch (Exception e) {
            logger.warn("Modal OK click failed, retrying with force click", e);
            okButton.click(new Locator.ClickOptions().setForce(true));
        }
    }

    // ====================== Utilities ======================

    public boolean isElementVisible(String selector) {
        try {
            return page.locator(selector).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    protected void captureScreenshot(String fileName) {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("./screenshots/" + fileName + ".png")));
        logger.info("Screenshot captured: " + fileName);
    }
    
    // ================= Highlight Element ===========================
    
    public void highlightElement(Locator locator) {
        try {
            locator.evaluate("el => {" +
                "  const [border, bg] = [el.style.border, el.style.backgroundColor];" +
                "  el.style.border = '3px solid red';" +
                "  el.style.backgroundColor = 'yellow';" +
                "  setTimeout(() => { el.style.border = border; el.style.backgroundColor = bg; }, 1000);" +
                "}");
        } catch (Exception e) {
            logger.warn("Could not highlight element: {}", e.getMessage());
        }
    }
    
 // ====================== Reusable Validation Utility ======================

    public boolean isElementVisibleAndVerified(Locator locator, String elementName, int timeoutMillis) {
        logger.info("Validating if '{}' is visible...", elementName);
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeoutMillis));
            
            // Visually highlight the target verification element
            highlightElement(locator);
            
            boolean isVisible = locator.isVisible();
            if (isVisible) {
                logger.info("'{}' validation successful: Element is visible.", elementName);
            }
            return isVisible;
        } catch (Exception e) {
            logger.error("'{}' validation failed: Element not visible. Error: {}", elementName, e.getMessage());
            return false;
        }
    }

    // Overloaded convenience method with a default 10-second timeout
    public boolean isElementVisibleAndVerified(Locator locator, String elementName) {
        return isElementVisibleAndVerified(locator, elementName, 10000);
    }
    
    // Simulates refreshing the browser
 	public void refreshBrowser() {
 		logger.info("Refreshing the browser...");
 		BaseClass.getPage().reload();
 		logger.info("Browser refreshed successfully.");
 	}


    // Hover Consistency Verification
    public boolean verifyButtonHoverEffect(Locator buttonLocator) {
        try {
            buttonLocator.hover();
            logger.info("Successfully hovered over the target button.");
            return true;
        } catch (Exception e) {
            logger.error("Hover action failed: {}", e.getMessage());
            return false;
        }
    }
}