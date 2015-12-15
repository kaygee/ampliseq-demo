package com.thermofisher.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    private static final String CONFIG_FILE_NAME = "integration-test.properties";
    private static final Properties CONFIG_PROPERTIES = new Properties();
    private static final String REMOTE_PASSWORD_DEFAULT = "123456";

    /**
     * @return the properties
     */
    public static Properties getProperties() {
        return CONFIG_PROPERTIES;
    }

    /**
     * Returns a property/env var coerced to integer, or throws an exception if conversion fails.
     *
     * @param key the key to use
     * @param defaultValue the default value if neither an env variable nor property is set
     * @return the var/property or default
     */
    public static int getEnvVarOrPropertyAsIntWithDefault(String key, int defaultValue) {
        String propertyFound = getEnvVarOrPropertyWithDefault(key, null);
        return StringUtils.isEmpty(propertyFound) ? defaultValue : Integer.valueOf(propertyFound);
    }

    /**
     * For people who run tests from eclipse and need to be able to pass in properties as environment variables or
     * system properties.
     *
     * @param key the key to use
     * @param defaultValue the default value if neither an env variable nor property is set
     * @return the var/property or default
     */
    public static String getEnvVarOrPropertyWithDefault(String key, String defaultValue) {
        if (System.getenv().containsKey(key)) {
            return System.getenv(key);
        } else if (System.getProperties().containsKey(key)) {
            return System.getProperty(key);
        } else {
            return CONFIG_PROPERTIES.getProperty(key, defaultValue);
        }
    }

    /**
     * Load global properties.
     *
     * @throws IOException if the configuration file could not be read.
     */
    public static void loadProperties() throws IOException {
        InputStream is = null;
        try {
            is = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
            if (is != null) {
                CONFIG_PROPERTIES.load(is);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static boolean isTargetEndpointRemote() {
        return ConfigUtil.getEnvVarOrPropertyWithDefault("endpoint.target", "").equals("REMOTE");
    }

    public static String provideEndpointUrl() {
        String url = ConfigUtil.getEnvVarOrPropertyWithDefault("ionreporter.webdriver.url", "");
        try {
            // Test to see if it's a valid URL.
            new URL(url);
        } catch (MalformedURLException me) {
            throw new RuntimeException("The environment variable named 'ionreporter.webdriver"
                    + ".url' wasn't a valid URL. Please check the environment variable.", me);
        }
        return url;
    }

    public static String provideAdminUsername() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.orgadmin.username", "ion.reporter@lifetech.com");
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.orgadmin.username", "orgadmin");
        }
    }

    public static String provideAdminPassword() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.orgadmin.password", REMOTE_PASSWORD_DEFAULT);
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.orgadmin.password", "orgadmin");
        }
    }

    public static String provideAnalyzeUsername() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.analyze.username",
                    "ion.reporter.analyze@lifetech.com");
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.analyze.username", "analyze");
        }
    }

    public static String provideAnalyzePassword() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.analyze.password", REMOTE_PASSWORD_DEFAULT);
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.analyze.password", "analyze");
        }
    }

    public static String provideReportUsername() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.report.username",
                    "ion.reporter.report@lifetech.com");
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.report.username", "report");
        }
    }

    public static String provideReportPassword() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.report.password", REMOTE_PASSWORD_DEFAULT);
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.report.password", "report");
        }
    }

    public static String provideImportUsername() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.import.username",
                    "ion.reporter.import@lifetech.com");
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.import.username", "import");
        }
    }

    public static String provideImportPassword() {
        if (isTargetEndpointRemote()) {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("remote.import.password", REMOTE_PASSWORD_DEFAULT);
        } else {
            return ConfigUtil.getEnvVarOrPropertyWithDefault("stub.import.password", "import");
        }
    }
}
