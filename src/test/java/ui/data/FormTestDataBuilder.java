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
public class FormTestDataBuilder {

    private static final Random RANDOM = new Random();

    private static final String[] FIRST_NAMES = {"John", "Jane", "Michael", "Sarah", "David", "Emily", "James", "Emma"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
    private static final String[] SUBJECTS = {"Maths", "English", "Physics", "Chemistry", "Computer Science", "Biology", "History"};
    private static final String[] HOBBIES = {"Sports", "Reading", "Music"};
    private static final String[] STATES = {"NCR", "Uttar Pradesh", "Haryana", "Rajasthan"};
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gender;
    private final String mobile;
    private final String month;
    private final String year;
    private final String day;
    private final String[] subjects;
    private final String[] hobbies;
    private final String address;
    private final String state;
    private final String city;

    private FormTestDataBuilder() {
        this.firstName = randomElement(FIRST_NAMES);
        this.lastName = randomElement(LAST_NAMES);
        this.email = generateRandomEmail();
        this.gender = RANDOM.nextBoolean() ? "Male" : "Female";
        this.mobile = "1" + RandomStringUtils.randomNumeric(9);
        this.month = randomElement(MONTHS);
        this.year = String.valueOf(1980 + RANDOM.nextInt(25));
        this.day = String.format("%02d", 1 + RANDOM.nextInt(28));
        this.subjects = null;
        this.hobbies = null;
        this.address = null;
        this.state = null;
        this.city = null;
    }

    public static FormTestDataBuilder builder() {
        return new FormTestDataBuilder();
    }

    public static FormTestDataBuilder randomStudent() {
        return new FormTestDataBuilder()
                .withRandomSubjects(2)
                .withRandomHobbies(2)
                .withRandomAddress()
                .withRandomStateAndCity();
    }

    public static FormTestDataBuilder minimalStudent() {
        return new FormTestDataBuilder();
    }

    public FormTestDataBuilder withDateOfBirth(String month, String year, String day) {
        return this.withMonth(month).withYear(year).withDay(day);
    }

    public FormTestDataBuilder withSubjectsVarargs(String... subjects) {
        return this.withSubjects(subjects);
    }

    public FormTestDataBuilder withRandomSubjects(int count) {
        return this.withSubjects(randomElements(SUBJECTS, count));
    }

    public FormTestDataBuilder withHobbiesVarargs(String... hobbies) {
        return this.withHobbies(hobbies);
    }

    public FormTestDataBuilder withRandomHobbies(int count) {
        return this.withHobbies(randomElements(HOBBIES, count));
    }

    public FormTestDataBuilder withRandomAddress() {
        return this.withAddress(generateRandomAddress());
    }

    public FormTestDataBuilder withRandomStateAndCity() {
        String randomState = randomElement(STATES);
        return this.withState(randomState).withCity(getCityForState(randomState));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    private static <T> T randomElement(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    private static String[] randomElements(String[] source, int count) {
        int actualCount = Math.min(count, source.length);
        String[] result = new String[actualCount];
        boolean[] used = new boolean[source.length];

        for (int i = 0; i < actualCount; i++) {
            int index;
            do {
                index = RANDOM.nextInt(source.length);
            } while (used[index]);
            used[index] = true;
            result[i] = source[index];
        }
        return result;
    }

    private static String generateRandomEmail() {
        return "test" + RandomStringUtils.randomAlphanumeric(8).toLowerCase() + "@example.com";
    }

    private static String generateRandomAddress() {
        int streetNumber = 100 + RANDOM.nextInt(9900);
        String[] streets = {"Main St", "Oak Ave", "Park Blvd", "Elm Street", "Maple Drive"};
        String[] cities = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix"};
        return streetNumber + " " + randomElement(streets) + ", " + randomElement(cities);
    }

    private static String getCityForState(String state) {
        return switch (state) {
            case "NCR" -> RANDOM.nextBoolean() ? "Delhi" : "Gurgaon";
            case "Uttar Pradesh" -> RANDOM.nextBoolean() ? "Agra" : "Lucknow";
            case "Haryana" -> RANDOM.nextBoolean() ? "Karnal" : "Panipat";
            case "Rajasthan" -> RANDOM.nextBoolean() ? "Jaipur" : "Jaiselmer";
            default -> "Delhi";
        };
    }
}
