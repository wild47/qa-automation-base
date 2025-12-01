package ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.ConfigProvider;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    @BeforeAll
    public static void setUpAll() {
        // Configure Selenide from config.properties
        Configuration.browser = ConfigProvider.getConfig().browser();
        Configuration.headless = ConfigProvider.getConfig().headless();

        Configuration.timeout = System.getProperty("ui.timeout") != null
            ? Integer.parseInt(System.getProperty("ui.timeout"))
            : ConfigProvider.getConfig().timeout();

        Configuration.baseUrl = ConfigProvider.getConfig().uiBaseUrl();

        Configuration.pageLoadTimeout = System.getProperty("selenide.pageLoadTimeout") != null
            ? Integer.parseInt(System.getProperty("selenide.pageLoadTimeout"))
            : 30000;

        Configuration.browserSize = "1920x1080";
        Configuration.screenshots = ConfigProvider.getConfig().screenshotsOnFailure();
        Configuration.savePageSource = false;
        Configuration.reopenBrowserOnFail = false;
        Configuration.fastSetValue = false;

        if (Configuration.headless) {
            Configuration.browserCapabilities.setCapability("goog:chromeOptions",
                java.util.Map.of("args", java.util.List.of(
                    "--headless",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--disable-extensions",
                    "--disable-blink-features=AutomationControlled",
                    "--window-size=1920,1080"
                ))
            );
        }

        // Add Allure listener for Selenide
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false));
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}
