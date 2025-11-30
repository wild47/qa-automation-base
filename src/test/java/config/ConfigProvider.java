package config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigProvider {

    private static final TestConfig CONFIG = ConfigFactory.create(TestConfig.class, System.getProperties());

    public static TestConfig getConfig() {
        return CONFIG;
    }
}
