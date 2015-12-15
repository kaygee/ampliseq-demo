package com.thermofisher;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.UnableToCreateProfileException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.thermofisher.util.WaitDriverWaitFactory;

public class SubmitAmpliSeqDesigns {

    private static final Logger LOG = LoggerFactory.getLogger(SubmitAmpliSeqDesigns.class);
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String DRIVER_TYPE = "chrome";
    private static final String FIREFOX_BINARY = "/Applications/Firefox.31.7.0.esr.app/Contents/MacOS/firefox-bin";
    private static final String CHROME_BINARY = "/usr/local/bin/chromedriver";
    private static final String TARGETS_CSV_FILE = "resources/IAD28979_targets.csv";

    private static final By DISABLED_EXPLANATION = By.cssSelector(".disabled-explanation");
    private static final By SESSION_CHECK_MESSAGE = By.cssSelector("#lifetechSessionCheckMsg");
    private static final By SIGN_IN_BUTTON = By.cssSelector("#signInButton");
    private static final By USERNAME_FIELD = By.cssSelector("#username");
    private static final By PASSWORD_FIELD = By.cssSelector("#password");
    private static final By MORE_BUTTON = By.cssSelector("#expandNewDesignFormDiv");
    private static final By SUBMIT_TARGETS_BUTTON = By.cssSelector("#submitDesignJob");
    private static final By NEXT_ADD_TARGETS_BUTTON = By.cssSelector("#saveDesign");
    private static final By FILE_CONTROL = By.cssSelector("[id$='_targetsFile']");

    @Test
    public void addTargetsToDesign() {
        navigateToGetTargets();
        addGeneCdsOnlyTarget("BRCA1");
        addRegionTarget("name", "chr14", "95577648", "95577797");
    }

    @Test
    public void addTargetsFromCSVFile() {
        navigateToGetTargets();

        List<String[]> targets = getTargetsFromCSV(TARGETS_CSV_FILE);
        for (String[] currentLine : targets) {
            if (currentLine[0].equals("REGION")) {
                addRegionTarget(currentLine[1], currentLine[2], currentLine[3], currentLine[4]);
            } else if (currentLine[0].equals("GENE_CDS")) {
                addGeneCdsOnlyTarget(currentLine[1]);
            }
        }
    }

    @Test
    public void addTargetsByUpload() {
        navigateToGetTargets();
        clickUploadFileTab();
        chooseFileAndClickUpload("/Users/kgann/src/ampliseq-demo/" + TARGETS_CSV_FILE);
    }

    private void chooseFileAndClickUpload(String file) {
        WebElement chooseFileElement = waitForSomething(ExpectedConditions.presenceOfElementLocated(FILE_CONTROL));
        chooseFileElement.sendKeys(file);
        waitForVisiblityAndClick(By.cssSelector("#uploadTargets"));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".jquery-msg-content")));
    }

    private void clickUploadFileTab() {
        waitForVisiblityAndClick(By.cssSelector("[href='#tab-upload-regions']"));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadTargetsForm_fileType")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadTargets")));
    }

    private void navigateToGetTargets() {
        getWebDriver().get("https://www.ampliseq.com");
        clickSignIn();
        login("kgann@5amsolutions.com", "undebeC8");
        setDesignName(RandomStringUtils.randomAlphanumeric(35));
        setDesignDescription("Everything is awesome!");
        if (isMoreDisplayed()) {
            clickMore();
        }
        clickNextAddTargets();
    }

    private boolean isMoreDisplayed() {
        List<WebElement> elements = getWebDriver().findElements(By.cssSelector("#expandNewDesignFormDiv"));
        if (elements.size() > 1) {
            fail("Didn't expect more than one More button.");
        }
        WebElement moreElement = elements.get(0);
        String style = moreElement.getAttribute("style");
        if (style.equals("display: none;")) {
            return false;
        }
        return true;
    }

    private List<String[]> getTargetsFromCSV(String filename) {
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(filename), ',', '\'', 1);
            return reader.readAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clickSignIn() {
        waitForSomething(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON));
        getWebDriver().findElement(SIGN_IN_BUTTON).click();
    }

    private void login(String username, String password) {
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(DISABLED_EXPLANATION));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(SESSION_CHECK_MESSAGE));
        waitForSomething(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON));
        getWebDriver().findElement(USERNAME_FIELD).sendKeys(username);
        getWebDriver().findElement(PASSWORD_FIELD).sendKeys(password);
        getWebDriver().findElement(SIGN_IN_BUTTON).click();
    }

    private void clickMore() {
        waitForVisiblityAndClick(MORE_BUTTON);
    }

    private void clickSubmitTargets() {
        getWebDriver().findElement(SUBMIT_TARGETS_BUTTON);
    }

    private void clickNextAddTargets() {
        waitForVisiblityAndClick(NEXT_ADD_TARGETS_BUTTON);
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetRegion_new_type_td")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetRegion_new_name_td")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetsTable_wrapper")));
    }

    private void setDesignName(String name) {
        waitForVisibilityAndSendKeys("#designName", name);
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(MORE_BUTTON));
    }

    private void setDesignDescription(String description) {
        waitForVisibilityAndSendKeys("#designDescription", description);
    }

    private void waitForVisiblityAndClick(By locator) {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(locator));
        getWebDriver().findElement(locator).click();
    }

    private void waitForVisibilityAndSendKeys(String cssSelector, String text) {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
        getWebDriver().findElement(By.cssSelector(cssSelector)).sendKeys(text);
    }

    private void addGeneCdsOnlyTarget(String name) {
        getWebDriver().findElement(By.cssSelector("[value='GENE_CDS']")).click();
        getWebDriver().findElement(By.cssSelector("#targetRegion_new_name")).sendKeys(name);
        clickAddTarget();
    }

    private void addGeneCdsAndUtrTarget(String name) {
        getWebDriver().findElement(By.cssSelector("[value='GENE_EXONS']")).click();
        getWebDriver().findElement(By.cssSelector("#targetRegion_new_name")).sendKeys(name);
        clickAddTarget();
    }

    private void addRegionTarget(String name, String chrom, String start, String end) {
        getWebDriver().findElement(By.cssSelector("[value='REGION']")).click();
        clearAndSendKeys(By.cssSelector("#targetRegion_new_name"), name);
        clearAndSendKeys(By.cssSelector("input#targetRegion_new_chr"), chrom);
        clearAndSendKeys(By.cssSelector("input#targetRegion_new_startPos"), start);
        clearAndSendKeys(By.cssSelector("input#targetRegion_new_endPos"), end);
        clickAddTarget();
    }

    private void clearAndSendKeys(By by, String text) {
        getWebDriver().findElement(by).clear();
        getWebDriver().findElement(by).sendKeys(text);
    }

    private void clickAddTarget() {
        int beforeAddCount = getWebDriver().findElements(By.cssSelector("#targetsTable .target-edit")).size();

        waitForSomething(ExpectedConditions.elementToBeClickable(By.cssSelector("#targetRegion_new_saveTarget")))
                .click();
        waitForSomething(ExpectedConditions.elementToBeClickable(By.cssSelector("#targetRegion_new_saveTarget")));

        waitUntilTargetAddedSuccessfully(String.valueOf(beforeAddCount));
    }

    private boolean waitUntilTargetAddedSuccessfully(String amount) {
        Wait<String> wait = new FluentWait<>(amount).withTimeout(10, TimeUnit.SECONDS).pollingEvery(500,
                TimeUnit.MILLISECONDS);
        try {
            return wait.until(count -> {
                int oldCount = Integer.valueOf(count);
                return getWebDriver().findElements((By.cssSelector("#targetsTable .target-edit"))).size() > oldCount;

            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Rule
    public ExternalResource externalResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            driver = provideDriver(DRIVER_TYPE);
        }

        @Override
        protected void after() {
            try {
                getWebDriver().manage().deleteAllCookies();
                getWebDriver().quit();
            } catch (UnreachableBrowserException ube) {
                LOG.error(ube.getMessage(), ube);
            }
        }
    };

    /**
     * Prepare the test.
     *
     * @exception Exception if set up fails.
     */
    @Before
    public void setUp() throws Exception {
        Preconditions.checkNotNull(getWebDriver(), "Failed to set up the WebDriver");
        this.setWait(WaitDriverWaitFactory.createWait(driver));
    }

    /**
     * @return the webDriver
     */
    public WebDriver getWebDriver() {
        return this.driver;
    }

    public WebDriver provideDriver(String driverType) {
        WebDriver driver;
        if ("chrome".equals(driverType)) {
            driver = provideChromeDriver();
        } else if ("firefox".equals(driverType)) {
            FirefoxBinary binary = getFirefoxBinary();
            FirefoxProfile profile = getFirefoxProfile();
            driver = provideFirefoxDriver(binary, profile);
        } else {
            throw new IllegalArgumentException("Illegal value for selenium.driver.type: " + driverType);
        }

        return driver;
    }

    private WebDriver provideChromeDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_BINARY);
        final WebDriver driver = new ChromeDriver();
        return driver;
    }

    private WebDriver provideFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile) {
        WebDriver driver = new FirefoxDriver(binary, profile);
        return driver;
    }

    private FirefoxBinary getFirefoxBinary() {
        FirefoxBinary binary = null;
        File binaryDir = new File(FIREFOX_BINARY);
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
    protected WebDriverWait getWait() {
        return wait;
    }

    /**
     * @param wait the wait to set.
     */
    protected void setWait(WebDriverWait wait) {
        this.wait = wait;
    }

}
