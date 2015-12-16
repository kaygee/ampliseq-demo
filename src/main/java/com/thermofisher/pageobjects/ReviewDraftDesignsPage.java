package com.thermofisher.pageobjects;

import com.thermofisher.components.ConfirmSubmissionDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.base.Preconditions;

public class ReviewDraftDesignsPage extends AbstractLoadablePage<ReviewDraftDesignsPage> {

    @FindBy(css = "[href='#tab-upload-regions']")
    private WebElement uploadTab;

    @FindBy(css = "#uploadTargets")
    private WebElement uploadTargets;

    private static final String WAIT_SCREEN = ".waitscreen";
    @FindBy(css = WAIT_SCREEN)
    private WebElement waitScreen;

    @FindBy(css = SUBMIT_TARGETS)
    WebElement submitTargets;

    private static final String SPINNER = ".spinner";
    private static final String SUBMIT_TARGETS = "a[id='submitDesignJob']";
    private static final String FILE_CONTROL = "[id$='_targetsFile']";
    private static final By UPLOAD_SUCCESS_MESSAGE = By.id("data-message");
    private static final By DESIGN_SUMMARY = By.id("design_summary_section");

    public ReviewDraftDesignsPage(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    public void clickUploadFileTab() {
        waitForVisiblityAndClick(uploadTab);
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadTargetsForm_fileType")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadTargets")));
    }

    public void chooseFileAndClickUpload(String file) {
        String absoluteFilePath = getAbsoluteFilePath(file);
        Preconditions.checkNotNull(absoluteFilePath);
        WebElement chooseFileElement = waitForSomething(ExpectedConditions.presenceOfElementLocated(By
                .cssSelector(FILE_CONTROL)));
        chooseFileElement.sendKeys(absoluteFilePath);
        waitForVisiblityAndClick(uploadTargets);
        // Attempt to wait for the processing steps to finish.
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".jquery-msg-content")));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(WAIT_SCREEN)));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(SPINNER)));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(SUBMIT_TARGETS)));
        waitForSuccessfulUpload();
    }

    private void waitForSuccessfulUpload() {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(UPLOAD_SUCCESS_MESSAGE));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(DESIGN_SUMMARY));
    }

    public ConfirmSubmissionDialog clickSubmitTargetsExpectingConfirmation() {
        waitForVisiblityAndClick(submitTargets);
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(SPINNER)));
        return new ConfirmSubmissionDialog(getWebDriver()).get();
    }

    @Override
    protected void isLoaded() throws Error {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetRegion_new_type_td")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetRegion_new_name_td")));
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#targetsTable_wrapper")));
    }
}
