package ui.tests;

import config.ConfigProvider;
import io.qameta.allure.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junitpioneer.jupiter.RetryingTest;
import ui.data.WebTableTestDataBuilder;
import ui.pages.WebTablePage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Testing")
@Feature("Web Tables")
@Tag("ui")
public class WebTableTest extends BaseTest {

    private WebTablePage webTablePage;
    private static final String WEB_TABLES_URL = "/webtables";

    @BeforeEach
    @Override
    public void setUp() {
        webTablePage = new WebTablePage();
        webTablePage.open(ConfigProvider.getConfig().uiBaseUrl() + WEB_TABLES_URL);
    }

    @RetryingTest(3)
    @DisplayName("Should add new record to table")
    @Description("Verify that a new record can be added to the web table")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddNewRecord() {
        WebTableTestDataBuilder employee = WebTableTestDataBuilder.randomEmployee()
                .withUniqueEmail("test");

        webTablePage.addRecord(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getSalary(),
                employee.getDepartment()
        );

        assertThat(webTablePage.isRecordPresent(employee.getEmail()))
                .as("New record should be present in the table")
                .isTrue();

        webTablePage.verifyRecordData(employee.getEmail(), employee.getFirstName(), employee.getLastName());
    }

    @RetryingTest(3)
    @DisplayName("Should edit existing record in table")
    @Description("Verify that an existing record can be edited")
    @Severity(SeverityLevel.CRITICAL)
    public void testEditRecord() {
        WebTableTestDataBuilder employee = WebTableTestDataBuilder.randomEmployee()
                .withUniqueEmail("edit")
                .withFirstName("Original");

        webTablePage.addRecord(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getSalary(),
                employee.getDepartment()
        );

        webTablePage
                .editRecord(employee.getEmail())
                .updateFirstName("Updated")
                .submitForm();

        webTablePage.verifyRecordData(employee.getEmail(), "Updated", employee.getLastName());
    }

    @RetryingTest(3)
    @DisplayName("Should delete record from table")
    @Description("Verify that a record can be deleted from the web table")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteRecord() {
        WebTableTestDataBuilder employee = WebTableTestDataBuilder.randomEmployee()
                .withUniqueEmail("delete");

        webTablePage.addRecord(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getSalary(),
                employee.getDepartment()
        );

        assertThat(webTablePage.isRecordPresent(employee.getEmail()))
                .as("Record should be present before deletion")
                .isTrue();

        webTablePage.deleteRecord(employee.getEmail());

        assertThat(webTablePage.isRecordNotPresent(employee.getEmail()))
                .as("Record should not be present after deletion")
                .isTrue();
    }

    @RetryingTest(3)
    @DisplayName("Should search for records in table")
    @Description("Verify that table search functionality works correctly")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchFunctionality() {
        String uniqueName = "Unique" + RandomStringUtils.randomAlphabetic(6);
        WebTableTestDataBuilder employee = WebTableTestDataBuilder.randomEmployee()
                .withFirstName(uniqueName)
                .withEmail(uniqueName.toLowerCase() + "@test.com");

        webTablePage.addRecord(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getSalary(),
                employee.getDepartment()
        );

        webTablePage.search(uniqueName);

        assertThat(webTablePage.isRecordPresent(employee.getEmail()))
                .as("Search should find the record")
                .isTrue();

        webTablePage.verifySearchResultsCount(1);

        webTablePage.clearSearch();
        int totalRows = webTablePage.getRowCount();
        assertThat(totalRows)
                .as("After clearing search, multiple records should be visible")
                .isGreaterThan(1);
    }

    @RetryingTest(3)
    @DisplayName("Should handle search with no results")
    @Description("Verify that search returns empty results when no match is found")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchWithNoResults() {
        String nonExistentSearch = "NonExistent" + RandomStringUtils.randomAlphanumeric(10);

        webTablePage.search(nonExistentSearch);

        webTablePage.verifySearchResultsCount(0);
    }

    @RetryingTest(3)
    @DisplayName("Should handle multiple record operations")
    @Description("Verify that multiple add, edit, and delete operations work correctly")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleOperations() {
        WebTableTestDataBuilder employee1 = WebTableTestDataBuilder.randomEmployee()
                .withUniqueEmail("multi1")
                .withFirstName("First");
        WebTableTestDataBuilder employee2 = WebTableTestDataBuilder.randomEmployee()
                .withUniqueEmail("multi2")
                .withFirstName("Second");

        webTablePage.addRecord(
                employee1.getFirstName(),
                employee1.getLastName(),
                employee1.getEmail(),
                employee1.getAge(),
                employee1.getSalary(),
                employee1.getDepartment()
        );
        webTablePage.addRecord(
                employee2.getFirstName(),
                employee2.getLastName(),
                employee2.getEmail(),
                employee2.getAge(),
                employee2.getSalary(),
                employee2.getDepartment()
        );

        assertThat(webTablePage.isRecordPresent(employee1.getEmail()))
                .as("First record should be present")
                .isTrue();
        assertThat(webTablePage.isRecordPresent(employee2.getEmail()))
                .as("Second record should be present")
                .isTrue();

        webTablePage
                .editRecord(employee1.getEmail())
                .updateFirstName("FirstUpdated")
                .submitForm();

        webTablePage.deleteRecord(employee2.getEmail());

        webTablePage.verifyRecordData(employee1.getEmail(), "FirstUpdated", employee1.getLastName());
        assertThat(webTablePage.isRecordNotPresent(employee2.getEmail()))
                .as("Second record should be deleted")
                .isTrue();
    }

    @RetryingTest(3)
    @DisplayName("Should add complete record successfully")
    @Description("Verify that form accepts all fields")
    @Severity(SeverityLevel.MINOR)
    public void testCompleteRecordAdd() {
        WebTableTestDataBuilder employee = WebTableTestDataBuilder.randomEmployee()
                .withUniqueEmail("complete");

        webTablePage.addRecord(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getSalary(),
                employee.getDepartment()
        );

        assertThat(webTablePage.isRecordPresent(employee.getEmail()))
                .as("Complete record should be added successfully")
                .isTrue();

        webTablePage.verifyRecordData(employee.getEmail(), employee.getFirstName(), employee.getLastName());
    }
}
