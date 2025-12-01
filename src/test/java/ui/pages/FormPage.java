package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class FormPage extends BasePage {

    private final SelenideElement firstNameInput = $("#firstName");
    private final SelenideElement lastNameInput = $("#lastName");
    private final SelenideElement emailInput = $("#userEmail");
    private final SelenideElement mobileInput = $("#userNumber");
    private final SelenideElement subjectsInput = $("#subjectsInput");
    private final SelenideElement currentAddressInput = $("#currentAddress");
    private final SelenideElement stateDropdown = $("#state");
    private final SelenideElement cityDropdown = $("#city");
    private final SelenideElement submitButton = $("#submit");
    private final SelenideElement uploadPictureInput = $("#uploadPicture");
    private final SelenideElement dateOfBirthInput = $("#dateOfBirthInput");

    private final SelenideElement successModal = $(".modal-dialog");
    private final SelenideElement successModalTitle = $("#example-modal-sizes-title-lg");
    private final SelenideElement closeModalButton = $("#closeLargeModal");

    @Step("Fill first name: {firstName}")
    public FormPage fillFirstName(String firstName) {
        firstNameInput.setValue(firstName);
        return this;
    }

    @Step("Fill last name: {lastName}")
    public FormPage fillLastName(String lastName) {
        lastNameInput.setValue(lastName);
        return this;
    }

    @Step("Fill email: {email}")
    public FormPage fillEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    @Step("Select gender: {gender}")
    public FormPage selectGender(String gender) {
        String genderLabelXpath = String.format("//label[contains(text(),'%s')]", gender);
        $x(genderLabelXpath).click();
        return this;
    }

    @Step("Fill mobile number: {mobile}")
    public FormPage fillMobile(String mobile) {
        mobileInput.setValue(mobile);
        return this;
    }

    @Step("Select date of birth: {date}")
    public FormPage selectDateOfBirth(String month, String year, String day) {
        dateOfBirthInput.click();
        $(".react-datepicker__month-select").selectOption(month);
        $(".react-datepicker__year-select").selectOption(year);
        String daySelector = String.format(".react-datepicker__day--0%s:not(.react-datepicker__day--outside-month)", day);
        $(daySelector).click();
        return this;
    }

    @Step("Fill subjects: {subjects}")
    public FormPage fillSubjects(String... subjects) {
        for (String subject : subjects) {
            subjectsInput.setValue(subject).pressEnter();
        }
        return this;
    }

    @Step("Select hobbies: {hobbies}")
    public FormPage selectHobbies(String... hobbies) {
        for (String hobby : hobbies) {
            String hobbyLabelXpath = String.format("//label[contains(text(),'%s')]", hobby);
            scrollToElement($x(hobbyLabelXpath));
            $x(hobbyLabelXpath).click();
        }
        return this;
    }

    @Step("Upload picture: {fileName}")
    public FormPage uploadPicture(File file) {
        uploadPictureInput.uploadFile(file);
        return this;
    }

    @Step("Fill current address: {address}")
    public FormPage fillCurrentAddress(String address) {
        currentAddressInput.setValue(address);
        return this;
    }

    @Step("Select state: {state}")
    public FormPage selectState(String state) {
        scrollToElement(stateDropdown);
        $("#react-select-3-input").setValue(state).pressEnter();
        return this;
    }

    @Step("Select city: {city}")
    public FormPage selectCity(String city) {
        $("#react-select-4-input").setValue(city).pressEnter();
        return this;
    }

    @Step("Submit form")
    public FormPage submitButton() {
        scrollToElement(submitButton);
        clickWithJS(submitButton);
        return this;
    }

    @Step("Verify success modal is displayed")
    public FormPage verifySuccessModalDisplayed() {
        successModal.shouldBe(visible);
        successModalTitle.shouldHave(text("Thanks for submitting the form"));
        return this;
    }

    @Step("Verify submitted value in modal: {label} = {value}")
    public FormPage verifySubmittedValue(String label, String value) {
        String xpath = String.format("//td[text()='%s']/following-sibling::td[text()='%s']", label, value);
        $x(xpath).shouldBe(visible);
        return this;
    }

    @Step("Close success modal")
    public FormPage closeModal() {
        closeModalButton.click();
        successModal.shouldNotBe(visible);
        return this;
    }

    @Step("Verify form submission success")
    public boolean isSubmissionSuccessful() {
        return successModal.is(visible);
    }
}
