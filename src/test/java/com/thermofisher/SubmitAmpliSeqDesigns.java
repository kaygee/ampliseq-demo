package com.thermofisher;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.thermofisher.components.ConfirmSubmissionDialog;
import com.thermofisher.pageobjects.LoginPage;
import com.thermofisher.pageobjects.ReviewDraftDesignsPage;
import com.thermofisher.pageobjects.SignInPage;
import com.thermofisher.pageobjects.StartNewDesignPage;

public class SubmitAmpliSeqDesigns extends AbstractSeleniumTest {

    @Test
    public void uploadAndSubmitTargets() {
        SignInPage signInPage = new SignInPage(getWebdriver()).get();
        LoginPage loginPage = signInPage.clickSignIn();
        StartNewDesignPage startNewDesignPage = loginPage.login();
        startNewDesignPage.setDesignName(RandomStringUtils.randomAlphanumeric(25));
        ReviewDraftDesignsPage reviewDraftDesignsPage = startNewDesignPage.clickNextAddTargets();
        reviewDraftDesignsPage.clickUploadFileTab();
        reviewDraftDesignsPage.chooseFileAndClickUpload("resources/IAD28979_targets.csv");
        ConfirmSubmissionDialog confirmSubmissionDialog = reviewDraftDesignsPage
                .clickSubmitTargetsExpectingConfirmation();
        confirmSubmissionDialog.setComment(RandomStringUtils.randomAlphabetic(25));
    }

}
