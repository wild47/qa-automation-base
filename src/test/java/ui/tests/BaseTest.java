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
        Configuration.timeout = ConfigProvider.getConfig().timeout();
        Configuration.baseUrl = ConfigProvider.getConfig().uiBaseUrl();
        Configuration.pageLoadTimeout = 30000;
        Configuration.browserSize = "1920x1080";
        Configuration.screenshots = ConfigProvider.getConfig().screenshotsOnFailure();
        Configuration.savePageSource = false;

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
