package com.thermofisher;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.UnableToCreateProfileException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.thermofisher.util.WaitDriverWaitFactory;

public class SubmitAmpliSeqDesigns {

    private static final Logger LOG = LoggerFactory.getLogger(SubmitAmpliSeqDesigns.class);
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final Properties CONFIG_PROPERTIES = new Properties();
    private WebDriver driver;
    private WebDriverWait wait;

    private static final By DISABLED_EXPLANATION = By.cssSelector(".disabled-explanation");
    private static final By SESSION_CHECK_MESSAGE = By.cssSelector("#lifetechSessionCheckMsg");
    private static final By SIGN_IN_BUTTON = By.cssSelector("#signInButton");
    private static final By USERNAME_FIELD = By.cssSelector("#username");
    private static final By PASSWORD_FIELD = By.cssSelector("#password");
    private static final By MORE_BUTTON = By.cssSelector("#expandNewDesignFormDiv");
    private static final By SUBMIT_TARGETS_BUTTON = By.cssSelector("#submitDesignJob");
    private static final By NEXT_ADD_TARGETS_BUTTON = By.cssSelector("#saveDesign");
    private static final By FILE_CONTROL = By.cssSelector("[id$='_targetsFile']");

    @Before
    public void setUp() {
        loadProperties();
        driver = provideDriver(getProperty("driver.type"));
        Preconditions.checkNotNull(getWebDriver(), "Failed to set up the WebDriver");
        this.setWait(WaitDriverWaitFactory.createWait(getWebDriver()));
        getWebDriver().get(getProperty("target.url"));
    }

    @After
    public void tearDown() {
        if (getWebDriver() != null) {
            try {
                getWebDriver().manage().deleteAllCookies();
                getWebDriver().quit();
            } catch (UnreachableBrowserException ube) {
                LOG.error(ube.getMessage(), ube);
            }
        }
    }

    @Test
    public void addTargetsByUpload() {
        clickSignIn();
        login();
        setDesignName(RandomStringUtils.randomAlphanumeric(35));
        clickNextAddTargets();
        clickUploadFileTab();
        chooseFileAndClickUpload("resources/IAD28979_targets.csv");
    }

    private void chooseFileAndClickUpload(String file) {
        String absoluteFilePath = getAbsoluteFilePath(file);
        WebElement chooseFileElement = waitForSomething(ExpectedConditions.presenceOfElementLocated(FILE_CONTROL));
        chooseFileElement.sendKeys(absoluteFilePath);
        waitForVisiblityAndClick(By.cssSelector("#uploadTargets"));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".jquery-msg-content")));
    }

    private String getAbsoluteFilePath(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    private void clickUploadFileTab() {
        waitForVisiblityAndClick(By.cssSelector("[href='#tab-upload-regions']"));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadTargetsForm_fileType")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadTargets")));
    }

    private void clickSignIn() {
        waitForSomething(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON));
        getWebDriver().findElement(SIGN_IN_BUTTON).click();
    }

    private void login() {
        String username = getProperty("user.name");
        String password = getProperty("user.password");
        loginAs(username, password);
    }

    private void loginAs(String username, String password) {
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(DISABLED_EXPLANATION));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(SESSION_CHECK_MESSAGE));
        waitForSomething(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON));
        getWebDriver().findElement(USERNAME_FIELD).sendKeys(username);
        getWebDriver().findElement(PASSWORD_FIELD).sendKeys(password);
        getWebDriver().findElement(SIGN_IN_BUTTON).click();
    }

    private void clickSubmitTargets() {
        getWebDriver().findElement(SUBMIT_TARGETS_BUTTON);
    }

    private void clickNextAddTargets() {
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(MORE_BUTTON));
        waitForVisiblityAndClick(NEXT_ADD_TARGETS_BUTTON);
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetRegion_new_type_td")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetRegion_new_name_td")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetsTable_wrapper")));
    }

    private void setDesignName(String name) {
        waitForVisibilityAndSendKeys("#designName", name);
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(MORE_BUTTON));
    }

    private void waitForVisiblityAndClick(By locator) {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(locator));
        getWebDriver().findElement(locator).click();
    }

    private void waitForVisibilityAndSendKeys(String cssSelector, String text) {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
        getWebDriver().findElement(By.cssSelector(cssSelector)).sendKeys(text);
    }

    /**
     * @return the webDriver
     */
    private WebDriver getWebDriver() {
        return this.driver;
    }

    private WebDriver provideDriver(String driverType) {
        WebDriver driver;
        if ("chrome".equals(driverType)) {
            driver = provideChromeDriver();
        } else if ("firefox".equals(driverType)) {
            FirefoxBinary binary = getFirefoxBinary();
            FirefoxProfile profile = getFirefoxProfile();
            driver = provideFirefoxDriver(binary, profile);
        } else {
            throw new IllegalArgumentException("Illegal value for driver.type: " + driverType);
        }

        return driver;
    }

    private WebDriver provideChromeDriver() {
        System.setProperty("webdriver.chrome.driver", getProperty("chromedriver.binary.location"));
        final WebDriver driver = new ChromeDriver();
        return driver;
    }

    private WebDriver provideFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile) {
        WebDriver driver = new FirefoxDriver(binary, profile);
        return driver;
    }

    private FirefoxBinary getFirefoxBinary() {
        FirefoxBinary binary = null;
        File binaryDir = new File(getProperty("firefox.binary.location"));
        if (binaryDir.exists()) {
            binary = new FirefoxBinary(binaryDir);
            // Set timeout to wait for binary, (2 minutes).
            binary.setTimeout(2 * 60 * 1000);
        }
        return binary;
    }

    private FirefoxProfile getFirefoxProfile() {
        try {
            FirefoxProfile profile = new FirefoxProfile();
            profile.setAlwaysLoadNoFocusLib(true);
            profile.setEnableNativeEvents(false);
            profile.setPreference("app.update.auto", false);
            profile.setPreference("app.update.enabled", false);
            profile.setPreference("app.update.silent", false);
            LOG.info("Created Firefox Profile: " + profile + ", [" + profile.layoutOnDisk().getAbsolutePath() + "]");
            return profile;
        } catch (UnableToCreateProfileException e) {
            throw new RuntimeException("Unable to create profile [" + e.getMessage() + "]" + ".", e);
        }
    }

    private void loadProperties() {
        try {
            InputStream input = new FileInputStream(CONFIG_FILE_NAME);
            CONFIG_PROPERTIES.load(input);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private String getProperty(String key) {
        return CONFIG_PROPERTIES.getProperty(key);
    }

    public <U> U waitForSomething(ExpectedCondition<U> toWaitFor) {
        try {
            return getWait().until(toWaitFor);
        } catch (RuntimeException e) {
            LOG.info(e.getMessage());
            throw e;
        }
    }

    /**
     * @return the wait).
     */
    private WebDriverWait getWait() {
        return wait;
    }

    /**
     * @param wait the wait to set.
     */
    private void setWait(WebDriverWait wait) {
        this.wait = wait;
    }

}
