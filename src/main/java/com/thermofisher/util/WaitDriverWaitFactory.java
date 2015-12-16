package com.thermofisher.util;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class WaitDriverWaitFactory {

    /**
         * Let's keep this value *LOW* so we're not waiting longer than necessary in our tests.
         */
        public static final int SLEEP_IN_BETWEEN_POLLING_MILLIS = 50;

        public static final int LONG_TIMEOUT_IN_SECONDS = 30;

        public static final int DEFAULT_TIMEOUT_IN_SECONDS = 30;

        public static WebDriverWait createWait(WebDriver driver) {
            return createWait(driver, DEFAULT_TIMEOUT_IN_SECONDS);
        }

        public static WebDriverWait createLongWait(WebDriver driver) {
            return createWait(driver, LONG_TIMEOUT_IN_SECONDS);
        }

        public static WebDriverWait createWait(WebDriver driver, long timeout) {
            return new WebDriverWait(driver, timeout, SLEEP_IN_BETWEEN_POLLING_MILLIS);
        }

        public static <T> FluentWait<T> createFluentWait(T input, WebDriver driver) {
            return createFluentWait(input, driver, DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        }

        public static <T> FluentWait<T> createFluentWait(T input, WebDriver driver, long timeout, TimeUnit timeUnit) {
            return new FluentWait<T>(input)
                    .withTimeout(timeout, timeUnit)
                    .pollingEvery(SLEEP_IN_BETWEEN_POLLING_MILLIS, TimeUnit.MILLISECONDS)
                    .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        }
}
