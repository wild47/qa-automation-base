package ui.tests;

import config.ConfigProvider;
import io.qameta.allure.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junitpioneer.jupiter.RetryingTest;
import ui.data.FormTestDataBuilder;
import ui.pages.FormPage;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Testing")
@Feature("Form")
@Tag("ui")
public class FormTest extends BaseTest {

    private FormPage formPage;
    private static final String FORM_URL = "/automation-practice-form";
    private static final String PATH_TO_FILES = "src/test/resources/testData/";

    @BeforeEach
    @Override
    public void setUp() {
        formPage = new FormPage();
        formPage.open(ConfigProvider.getConfig().uiBaseUrl() + FORM_URL);
    }

    @RetryingTest(3)
    @DisplayName("Should submit student registration form successfully")
    @Description("Verify that student registration form can be filled and submitted with all fields")
    @Severity(SeverityLevel.CRITICAL)
    public void testCompleteFormSubmission() {
        File file = FileUtils.getFile(PATH_TO_FILES + "test_image.jpg");
        FormTestDataBuilder student = FormTestDataBuilder.randomStudent();

        formPage
                .fillFirstName(student.getFirstName())
                .fillLastName(student.getLastName())
                .fillEmail(student.getEmail())
                .selectGender(student.getGender())
                .fillMobile(student.getMobile())
                .selectDateOfBirth(student.getMonth(), student.getYear(), student.getDay())
                .fillSubjects(student.getSubjects())
                .selectHobbies(student.getHobbies())
                .uploadPicture(file)
                .fillCurrentAddress(student.getAddress())
                .selectState(student.getState())
                .selectCity(student.getCity())
                .submitButton();

        formPage
                .verifySuccessModalDisplayed()
                .verifySubmittedValue("Student Name", student.getFullName())
                .verifySubmittedValue("Student Email", student.getEmail())
                .verifySubmittedValue("Gender", student.getGender())
                .verifySubmittedValue("Mobile", student.getMobile())
                .verifySubmittedValue("State and City", student.getState() + " " + student.getCity());

        assertThat(formPage.isSubmissionSuccessful())
                .as("Form submission should be successful")
                .isTrue();
    }

    @RetryingTest(3)
    @DisplayName("Should submit form with required fields only")
    @Description("Verify that form can be submitted with only required fields")
    @Severity(SeverityLevel.NORMAL)
    public void testMinimalFormSubmission() {
        FormTestDataBuilder student = FormTestDataBuilder.minimalStudent();

        formPage
                .fillFirstName(student.getFirstName())
                .fillLastName(student.getLastName())
                .selectGender(student.getGender())
                .fillMobile(student.getMobile())
                .submitButton();

        formPage
                .verifySuccessModalDisplayed()
                .verifySubmittedValue("Student Name", student.getFullName())
                .verifySubmittedValue("Gender", student.getGender())
                .verifySubmittedValue("Mobile", student.getMobile());

        assertThat(formPage.isSubmissionSuccessful())
                .as("Form submission should be successful with required fields")
                .isTrue();
    }

    @RetryingTest(3)
    @DisplayName("Should handle date picker selection")
    @Description("Verify that date of birth can be selected from date picker")
    @Severity(SeverityLevel.NORMAL)
    public void testDatePickerSelection() {
        FormTestDataBuilder student = FormTestDataBuilder.minimalStudent()
                .withDateOfBirth("July", "2000", "25");

        formPage
                .fillFirstName(student.getFirstName())
                .fillLastName(student.getLastName())
                .selectGender(student.getGender())
                .fillMobile(student.getMobile())
                .selectDateOfBirth(student.getMonth(), student.getYear(), student.getDay())
                .submitButton();

        formPage
                .verifySuccessModalDisplayed()
                .verifySubmittedValue("Date of Birth", "25 July,2000");

        assertThat(formPage.isSubmissionSuccessful())
                .as("Form should accept date picker selection")
                .isTrue();
    }

    @RetryingTest(3)
    @DisplayName("Should handle subject selection with autocomplete")
    @Description("Verify that subjects can be selected using autocomplete dropdown")
    @Severity(SeverityLevel.NORMAL)
    public void testSubjectAutocomplete() {
        FormTestDataBuilder student = FormTestDataBuilder.minimalStudent()
                .withSubjectsVarargs("Computer Science", "Physics", "Chemistry");

        formPage
                .fillFirstName(student.getFirstName())
                .fillLastName(student.getLastName())
                .selectGender(student.getGender())
                .fillMobile(student.getMobile())
                .fillSubjects(student.getSubjects())
                .submitButton();

        formPage
                .verifySuccessModalDisplayed()
                .verifySubmittedValue("Subjects", "Computer Science, Physics, Chemistry");

        assertThat(formPage.isSubmissionSuccessful())
                .as("Form should accept multiple subjects")
                .isTrue();
    }

    @RetryingTest(3)
    @DisplayName("Should handle state and city dropdown selection")
    @Description("Verify that state and city can be selected from dependent dropdowns")
    @Severity(SeverityLevel.NORMAL)
    public void testStateAndCityDropdowns() {
        FormTestDataBuilder student = FormTestDataBuilder.minimalStudent()
                .withState("Uttar Pradesh")
                .withCity("Agra");

        formPage
                .fillFirstName(student.getFirstName())
                .fillLastName(student.getLastName())
                .selectGender(student.getGender())
                .fillMobile(student.getMobile())
                .selectState(student.getState())
                .selectCity(student.getCity())
                .submitButton();

        formPage
                .verifySuccessModalDisplayed()
                .verifySubmittedValue("State and City", student.getState() + " " + student.getCity());

        assertThat(formPage.isSubmissionSuccessful())
                .as("Form should accept state and city selection")
                .isTrue();
    }
}
