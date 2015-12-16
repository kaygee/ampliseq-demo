package com.thermofisher;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.UnableToCreateProfileException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSeleniumTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSeleniumTest.class);
    private WebDriver driver;
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final Properties CONFIG_PROPERTIES = new Properties();

    @Before
    public void setUp() {
        loadProperties();
        driver = provideDriver(getProperty("driver.type"));
        Preconditions.checkNotNull(driver, "Failed to set up the WebDriver");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            try {
                driver.manage().deleteAllCookies();
                driver.quit();
            } catch (UnreachableBrowserException ube) {
                LOG.error(ube.getMessage(), ube);
            }
        }
    }

    public WebDriver getWebdriver(){
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

    public String getTargetUrl(){
        return CONFIG_PROPERTIES.getProperty("target.url");
    }

}
