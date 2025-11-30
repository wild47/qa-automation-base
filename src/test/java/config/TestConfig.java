package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:config.properties"
})
public interface TestConfig extends Config {

    @Key("api.base.url")
    String apiBaseUrl();

    @Key("api.auth.username")
    String apiUsername();

    @Key("api.auth.password")
    String apiPassword();

    @Key("ui.base.url")
    String uiBaseUrl();

    @Key("ui.browser")
    String browser();

    @Key("ui.headless")
    boolean headless();

    @Key("ui.timeout")
    int timeout();

    @Key("ui.screenshots.on.failure")
    boolean screenshotsOnFailure();

    @Key("test.retry.count")
    int retryCount();
}
