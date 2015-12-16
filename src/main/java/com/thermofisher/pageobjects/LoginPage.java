package com.thermofisher.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends AbstractLoadablePage<LoginPage> {

    private final String disabledExplanationCss = ".disabled-explanation";
    private final String sessionCheckMessageId = "lifetechSessionCheckMsg";

    @FindBy(css = disabledExplanationCss)
    private WebElement disableExplanation;

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "signInButton")
    private WebElement signInButton;

    @FindBy(id = sessionCheckMessageId)
    private WebElement sessionCheckMessage;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    public StartNewDesignPage login(){
        SignInPage signInPage = new SignInPage(getWebDriver());
        LoginPage loginPage = signInPage.clickSignIn();
        String username = getUserName();
        String password = getUserPassword();
        loginPage.loginAs(username, password);
        return new StartNewDesignPage(getWebDriver()).get();
    }

    public StartNewDesignPage loginAs(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        signInButton.click();
        return new StartNewDesignPage(getWebDriver()).get();
    }

    @Override
    protected void load() {
        SignInPage signInPage = new SignInPage(getWebDriver()).get();
        signInPage.clickSignIn();
    }

    @Override
    protected void isLoaded() throws Error {
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(disabledExplanationCss)));
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.id(sessionCheckMessageId)));
        waitForSomething(ExpectedConditions.elementToBeClickable(signInButton));
    }
}
