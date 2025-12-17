package reflection;

import com.automation.qa.model.User;
import com.automation.qa.service.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static reflection.ReflectionTestUtils.getField;

/**
 * Tests demonstrating class structure validation using Reflection API.
 **/

@Feature("Reflection API - Class Structure Validation")
@DisplayName("Class Structure Validation Tests")
@Tag("reflection")
class ClassStructureValidationTest {

    @Test
    @DisplayName("Should validate User class has all required fields")
    @Description("Verify User model class contains all expected fields with correct types")
    @Severity(SeverityLevel.CRITICAL)
    void shouldValidateUserClassFields() {
        Class<User> userClass = User.class;

        List<Field> fields = Arrays.asList(userClass.getDeclaredFields());
        List<String> fieldNames = fields.stream()
            .map(Field::getName)
            .collect(Collectors.toList());

        assertThat(fieldNames)
            .as("User class should have id, username, email, and active fields")
            .containsExactlyInAnyOrder("id", "username", "email", "active");

        Field idField = getField(userClass, "id");
        assertThat(idField.getType())
            .as("id field should be of type Long")
            .isEqualTo(Long.class);

        Field usernameField = getField(userClass, "username");
        assertThat(usernameField.getType())
            .as("username field should be of type String")
            .isEqualTo(String.class);

        Field emailField = getField(userClass, "email");
        assertThat(emailField.getType())
            .as("email field should be of type String")
            .isEqualTo(String.class);

        Field activeField = getField(userClass, "active");
        assertThat(activeField.getType())
            .as("active field should be of type boolean")
            .isEqualTo(boolean.class);
    }

    @Test
    @DisplayName("Should validate all User class fields are private (encapsulation)")
    @Description("Ensure proper encapsulation by verifying all fields are private")
    @Severity(SeverityLevel.CRITICAL)
    void shouldValidateUserFieldsArePrivate() {
        Class<User> userClass = User.class;

        List<Field> fields = Arrays.asList(userClass.getDeclaredFields());
        List<Field> nonPrivateFields = fields.stream()
            .filter(field -> !Modifier.isPrivate(field.getModifiers()))
            .collect(Collectors.toList());

        assertThat(nonPrivateFields)
            .as("All fields should be private for proper encapsulation")
            .isEmpty();
    }

    @Test
    @DisplayName("Should validate User class has required public methods")
    @Description("Verify User class exposes required getter and setter methods")
    @Severity(SeverityLevel.CRITICAL)
    void shouldValidateUserHasRequiredPublicMethods() {
        Class<User> userClass = User.class;
        List<String> requiredMethods = Arrays.asList(
            "getId", "setId",
            "getUsername", "setUsername",
            "getEmail", "setEmail",
            "isActive", "setActive",
            "equals", "hashCode", "toString"
        );

        List<Method> publicMethods = Arrays.stream(userClass.getDeclaredMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .toList();

        List<String> publicMethodNames = publicMethods.stream()
            .map(Method::getName)
            .collect(Collectors.toList());

        assertThat(publicMethodNames)
            .as("User class should have all required public methods")
            .containsAll(requiredMethods);
    }

    @Test
    @DisplayName("Should validate User class has proper constructors")
    @Description("Verify User class has both default and parameterized constructors")
    @Severity(SeverityLevel.NORMAL)
    void shouldValidateUserConstructors() {
        Class<User> userClass = User.class;

        Constructor<?>[] constructors = userClass.getDeclaredConstructors();

        assertThat(constructors)
            .as("User class should have exactly 2 constructors")
            .hasSize(2);

        boolean hasDefaultConstructor = Arrays.stream(constructors)
            .anyMatch(c -> c.getParameterCount() == 0);

        assertThat(hasDefaultConstructor)
            .as("User class should have a no-arg constructor")
            .isTrue();

        boolean hasParameterizedConstructor = Arrays.stream(constructors)
            .anyMatch(c -> c.getParameterCount() == 4);

        assertThat(hasParameterizedConstructor)
            .as("User class should have a constructor with 4 parameters")
            .isTrue();
    }

    @Test
    @DisplayName("Should validate UserService class has final repository field")
    @Description("Verify immutability by checking repository field is final")
    @Severity(SeverityLevel.NORMAL)
    void shouldValidateServiceFieldsAreFinal() {
        Class<UserService> serviceClass = UserService.class;

        Field repositoryField = getField(serviceClass, "userRepository");

        assertThat(Modifier.isFinal(repositoryField.getModifiers()))
            .as("userRepository field should be final for immutability")
            .isTrue();

        assertThat(Modifier.isPrivate(repositoryField.getModifiers()))
            .as("userRepository field should be private")
            .isTrue();
    }

    @Test
    @DisplayName("Should validate UserService has all CRUD methods")
    @Description("Verify UserService implements complete CRUD interface")
    @Severity(SeverityLevel.CRITICAL)
    void shouldValidateServiceHasCrudMethods() {
        Class<UserService> serviceClass = UserService.class;
        List<String> expectedMethods = Arrays.asList(
            "createUser",
            "getUserById",
            "getUserByUsername",
            "getAllActiveUsers",
            "updateUser",
            "deleteUser",
            "deactivateUser"
        );

        List<String> publicMethods = Arrays.stream(serviceClass.getDeclaredMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .map(Method::getName)
            .collect(Collectors.toList());

        assertThat(publicMethods)
            .as("UserService should have all CRUD methods")
            .containsAll(expectedMethods);
    }

    @Test
    @DisplayName("Should validate method signatures")
    @Description("Verify methods have correct return types and parameters")
    @Severity(SeverityLevel.NORMAL)
    void shouldValidateMethodSignatures() throws NoSuchMethodException {
        Class<UserService> serviceClass = UserService.class;

        Method createUser = serviceClass.getMethod("createUser", User.class);
        assertThat(createUser.getReturnType())
            .as("createUser should return User")
            .isEqualTo(User.class);

        Method getUserById = serviceClass.getMethod("getUserById", Long.class);
        assertThat(getUserById.getReturnType().getName())
            .as("getUserById should return Optional")
            .contains("Optional");

        Method deleteUser = serviceClass.getMethod("deleteUser", Long.class);
        assertThat(deleteUser.getReturnType())
            .as("deleteUser should return void")
            .isEqualTo(void.class);
    }

    @Test
    @DisplayName("Should validate class is not final and can be extended")
    @Description("Verify service class can be extended for testing (not final)")
    @Severity(SeverityLevel.MINOR)
    void shouldValidateClassModifiers() {
        Class<UserService> serviceClass = UserService.class;

        assertThat(Modifier.isFinal(serviceClass.getModifiers()))
            .as("Service class should not be final to allow mocking/extending")
            .isFalse();

        assertThat(Modifier.isPublic(serviceClass.getModifiers()))
            .as("Service class should be public")
            .isTrue();
    }

    @Test
    @DisplayName("Should validate User class overrides Object methods")
    @Description("Verify proper equals, hashCode, and toString implementations")
    @Severity(SeverityLevel.NORMAL)
    void shouldValidateObjectMethodOverrides() throws NoSuchMethodException {
        Class<User> userClass = User.class;

        Method equals = userClass.getMethod("equals", Object.class);
        assertThat(equals.getDeclaringClass())
            .as("equals should be overridden in User class")
            .isEqualTo(User.class);

        Method hashCode = userClass.getMethod("hashCode");
        assertThat(hashCode.getDeclaringClass())
            .as("hashCode should be overridden in User class")
            .isEqualTo(User.class);

        Method toString = userClass.getMethod("toString");
        assertThat(toString.getDeclaringClass())
            .as("toString should be overridden in User class")
            .isEqualTo(User.class);
    }
}
