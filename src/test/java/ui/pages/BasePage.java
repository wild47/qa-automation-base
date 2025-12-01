package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public abstract class BasePage {

    @Step("Open page: {url}")
    public void open(String url) {
        Selenide.open(url);
    }

    @Step("Scroll element into view")
    protected void scrollToElement(SelenideElement element) {
        executeJavaScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    @Step("Click element using JavaScript")
    protected void clickWithJS(SelenideElement element) {
        executeJavaScript("arguments[0].click();", element);
    }
}
