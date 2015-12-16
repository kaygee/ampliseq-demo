package com.thermofisher.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class StartNewDesignPage extends AbstractLoadablePage<StartNewDesignPage> {

    @FindBy(id = "saveDesign")
    WebElement nextAddTargetsButton;

    @FindBy(id = "designName")
    WebElement designName;

    private final String moreButtonId = "expandNewDesignFormDiv";
    @FindBy(id = moreButtonId)
    WebElement moreButton;

    public StartNewDesignPage(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    public StartNewDesignPage setDesignName(String name) {
        waitForVisibilityAndSendKeys(designName, name);
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.id(moreButtonId)));
        return new StartNewDesignPage(getWebDriver()).get();
    }

    public ReviewDraftDesignsPage clickNextAddTargets() {
        waitForSomething(ExpectedConditions.invisibilityOfElementLocated(By.id(moreButtonId)));
        waitForVisiblityAndClick(nextAddTargetsButton);
        return new ReviewDraftDesignsPage(getWebDriver()).get();
    }

    @Override
    protected void load() {
        SignInPage signInPage = new SignInPage(getWebDriver()).get();
        LoginPage loginPage = signInPage.clickSignIn();
        String username = getUserName();
        String password = getUserPassword();
        loginPage.loginAs(username, password);
    }

    @Override
    protected void isLoaded() throws Error {
        waitForSomething(ExpectedConditions.visibilityOf(designName));
    }
}
