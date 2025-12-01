package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class WebTablePage extends BasePage {

    private final SelenideElement addButton = $("#addNewRecordButton");
    private final SelenideElement searchBox = $("#searchBox");
    private final ElementsCollection tableRows = $$(".rt-tr-group");
    private final ElementsCollection tableHeaders = $$(".rt-th");

    private final SelenideElement registrationFormModal = $(".modal-content");
    private final SelenideElement firstNameInput = $("#firstName");
    private final SelenideElement lastNameInput = $("#lastName");
    private final SelenideElement emailInput = $("#userEmail");
    private final SelenideElement ageInput = $("#age");
    private final SelenideElement salaryInput = $("#salary");
    private final SelenideElement departmentInput = $("#department");
    private final SelenideElement submitButton = $("#submit");

    @Step("Click Add button")
    public WebTablePage clickAdd() {
        addButton.click();
        registrationFormModal.shouldBe(visible);
        return this;
    }

    @Step("Fill first name: {firstName}")
    public WebTablePage fillFirstName(String firstName) {
        firstNameInput.setValue(firstName);
        return this;
    }

    @Step("Fill last name: {lastName}")
    public WebTablePage fillLastName(String lastName) {
        lastNameInput.setValue(lastName);
        return this;
    }

    @Step("Fill email: {email}")
    public WebTablePage fillEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    @Step("Fill age: {age}")
    public WebTablePage fillAge(String age) {
        ageInput.setValue(age);
        return this;
    }

    @Step("Fill salary: {salary}")
    public WebTablePage fillSalary(String salary) {
        salaryInput.setValue(salary);
        return this;
    }

    @Step("Fill department: {department}")
    public WebTablePage fillDepartment(String department) {
        departmentInput.setValue(department);
        return this;
    }

    @Step("Submit form")
    public WebTablePage submitForm() {
        submitButton.click();
        registrationFormModal.shouldNotBe(visible);
        return this;
    }

    @Step("Add new record")
    public WebTablePage addRecord(String firstName, String lastName, String email, String age, String salary, String department) {
        clickAdd();
        fillFirstName(firstName);
        fillLastName(lastName);
        fillEmail(email);
        fillAge(age);
        fillSalary(salary);
        fillDepartment(department);
        submitForm();
        return this;
    }

    @Step("Search for: {searchText}")
    public WebTablePage search(String searchText) {
        searchBox.setValue(searchText);
        return this;
    }

    @Step("Clear search")
    public WebTablePage clearSearch() {
        searchBox.clear();
        return this;
    }

    @Step("Edit record with email: {email}")
    public WebTablePage editRecord(String email) {
        String editButtonXpath = String.format("//div[text()='%s']/parent::div/parent::div//span[@title='Edit']", email);
        $x(editButtonXpath).click();
        registrationFormModal.shouldBe(visible);
        return this;
    }

    @Step("Update first name to: {newFirstName}")
    public WebTablePage updateFirstName(String newFirstName) {
        firstNameInput.clear();
        firstNameInput.setValue(newFirstName);
        return this;
    }

    @Step("Delete record with email: {email}")
    public WebTablePage deleteRecord(String email) {
        String deleteButtonXpath = String.format("//div[text()='%s']/parent::div/parent::div//span[@title='Delete']", email);
        $x(deleteButtonXpath).click();
        return this;
    }

    @Step("Verify record exists with email: {email}")
    public boolean isRecordPresent(String email) {
        return $$(".rt-td").findBy(text(email)).exists();
    }

    @Step("Verify record does not exist with email: {email}")
    public boolean isRecordNotPresent(String email) {
        return !isRecordPresent(email);
    }

    @Step("Get number of rows in table")
    public int getRowCount() {
        return (int) tableRows.asFixedIterable().stream()
                .filter(row -> !row.text().isEmpty())
                .count();
    }

    @Step("Verify record contains data")
    public WebTablePage verifyRecordData(String email, String firstName, String lastName) {
        String rowXpath = String.format("//div[text()='%s']/parent::div", email);
        SelenideElement row = $x(rowXpath);
        row.shouldHave(text(firstName));
        row.shouldHave(text(lastName));
        row.shouldHave(text(email));
        return this;
    }

    @Step("Click column header: {columnName}")
    public WebTablePage clickColumnHeader(String columnName) {
        tableHeaders.findBy(text(columnName)).click();
        return this;
    }

    @Step("Get first row email")
    public String getFirstRowEmail() {
        return $$(".rt-td").get(3).text();
    }

    @Step("Verify search results count: {expectedCount}")
    public WebTablePage verifySearchResultsCount(int expectedCount) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int actualCount = (int) tableRows.asFixedIterable().stream()
                .filter(row -> !row.text().isEmpty() && row.$$(".rt-td").size() > 0 && !row.$(".rt-td").text().trim().isEmpty())
                .count();

        assert actualCount == expectedCount : String.format("Expected %d results but got %d", expectedCount, actualCount);
        return this;
    }
}
