package com.thermofisher.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SignInPage extends AbstractLoadablePage<SignInPage> {

    private int numLoadTries = 0;

    @FindBy(id = "signInButton")
    private WebElement signInButton;

    public SignInPage(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    public LoginPage clickSignIn() {
        waitForSomething(ExpectedConditions.elementToBeClickable(signInButton));
        signInButton.click();
        return new LoginPage(getWebDriver()).get();
    }

    @Override
    protected void load() {
        getWebDriver().get("https://www.ampliseq.com");
    }

    @Override
    protected void isLoaded() throws Error {
        if (numLoadTries <= 0) {
            numLoadTries++;
            throw new Error("First Try...");
        }
        waitForSomething(ExpectedConditions.visibilityOf(signInButton));
    }
}
