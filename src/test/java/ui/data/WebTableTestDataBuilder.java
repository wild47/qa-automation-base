package ui.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

@Getter
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WebTableTestDataBuilder {

    private static final Random RANDOM = new Random();

    private static final String[] FIRST_NAMES = {"John", "Jane", "Michael", "Sarah", "David", "Emily", "James", "Emma", "Robert", "Lisa"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Martinez", "Taylor"};
    private static final String[] DEPARTMENTS = {"Engineering", "QA", "Marketing", "HR", "Finance", "IT", "Support", "Sales", "Operations"};

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String age;
    private final String salary;
    private final String department;

    private WebTableTestDataBuilder() {
        this.firstName = randomElement(FIRST_NAMES);
        this.lastName = randomElement(LAST_NAMES);
        this.email = generateRandomEmail();
        this.age = String.valueOf(22 + RANDOM.nextInt(43));
        this.salary = String.valueOf(30000 + RANDOM.nextInt(120000));
        this.department = randomElement(DEPARTMENTS);
    }

    public static WebTableTestDataBuilder builder() {
        return new WebTableTestDataBuilder();
    }

    public static WebTableTestDataBuilder randomEmployee() {
        return new WebTableTestDataBuilder();
    }

    public static WebTableTestDataBuilder minimalEmployee() {
        return new WebTableTestDataBuilder()
                .withAge("25")
                .withSalary("40000");
    }

    public WebTableTestDataBuilder withUniqueEmail(String prefix) {
        return this.withEmail(prefix + RandomStringUtils.randomAlphabetic(5).toLowerCase() + "@test.com");
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    private static <T> T randomElement(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    private static String generateRandomEmail() {
        return "emp" + RandomStringUtils.randomAlphanumeric(8).toLowerCase() + "@test.com";
    }
}
