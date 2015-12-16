package com.thermofisher.pageobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thermofisher.util.WaitDriverWaitFactory;

public abstract class AbstractLoadablePage<T extends LoadableComponent<T>> extends LoadableComponent<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLoadablePage.class);
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final Properties CONFIG_PROPERTIES = new Properties();

    private final WebDriver driver;
    private WebDriverWait wait;

    public AbstractLoadablePage(WebDriver driver) {
        loadProperties();
        this.driver = driver;
        this.setWait(WaitDriverWaitFactory.createWait(driver));
    }

    protected void load(){
        // Don't do anything...
    }

    protected WebDriver getWebDriver() {
        return this.driver;
    }

    private WebDriverWait getWait() {
        return wait;
    }

    private void setWait(WebDriverWait wait) {
        this.wait = wait;
    }

    public void waitForVisiblityAndClick(WebElement webElement) {
        waitForSomething(ExpectedConditions.visibilityOf(webElement));
        webElement.click();
    }

    public void waitForVisibilityAndSendKeys(WebElement webElement, String text) {
        waitForSomething(ExpectedConditions.visibilityOf(webElement));
        webElement.sendKeys(text);
    }

    protected String getAbsoluteFilePath(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public <U> U waitForSomething(ExpectedCondition<U> toWaitFor) {
        try {
            return getWait().until(toWaitFor);
        } catch (RuntimeException e) {
            LOG.info(e.getMessage());
            throw e;
        }
    }

    private void loadProperties() {
        try {
            InputStream input = new FileInputStream(CONFIG_FILE_NAME);
            CONFIG_PROPERTIES.load(input);
        } catch (IOException e) {
        }
    }

    private String getProperty(String key) {
        return CONFIG_PROPERTIES.getProperty(key);
    }

    public String getUserName() {
        return getProperty("user.name");
    }

    public String getUserPassword() {
        return getProperty("user.password");
    }
}
