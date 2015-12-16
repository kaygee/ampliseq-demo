package com.thermofisher.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.thermofisher.pageobjects.AbstractLoadablePage;

public class ConfirmSubmissionDialog extends AbstractLoadablePage<ConfirmSubmissionDialog> {

    private static final By CONFIRM_SUBMISSION_PANEL = By.id("submissionConfirmationPanel");

    @FindBy(id = CONFIRM_SUBMISSION_COMMENTS)
    WebElement confirmSubmissionComments;

    @FindBy(id = OK_BUTTON)
    WebElement okButton;

    private static final String CONFIRM_SUBMISSION_COMMENTS = "submissionComments";
    private static final String OK_BUTTON = "submitOkBtn";

    public ConfirmSubmissionDialog(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    public ConfirmSubmissionDialog setComment(String comment) {
        waitForSomething(ExpectedConditions.visibilityOfElementLocated(CONFIRM_SUBMISSION_PANEL));
        waitForVisibilityAndSendKeys(confirmSubmissionComments, comment);
        return new ConfirmSubmissionDialog(getWebDriver()).get();
    }

    public void clickOk() {
        waitForVisiblityAndClick(okButton);
    }

    public void confirmAndSubmit(String comment) {
        setComment(comment);
        clickOk();
    }

    @Override
    protected void isLoaded() throws Error {
        waitForSomething(ExpectedConditions.visibilityOf(confirmSubmissionComments));
        waitForSomething(ExpectedConditions.visibilityOf(okButton));
    }
}
