package reflection;

import com.automation.qa.model.User;
import com.automation.qa.repository.UserRepository;
import com.automation.qa.service.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static reflection.ReflectionTestUtils.*;

/**
 * Tests demonstrating private member access using Reflection API.
 **/

@Feature("Reflection API - Private Member Access")
@DisplayName("Private Member Access Tests")
@Tag("reflection")
class PrivateMemberAccessTest {

    private UserService userService;
    private UserRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(mockRepository);
    }

    @Test
    @DisplayName("Should access private field using reflection")
    @Description("Demonstrate reading private field value to verify internal state")
    @Severity(SeverityLevel.NORMAL)
    void shouldAccessPrivateField() {
        User user = new User();
        user.setUsername("testuser");

        Object usernameValue = getFieldValue(user, "username");

        assertThat(usernameValue)
            .as("Should successfully read private username field")
            .isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should modify private field using reflection")
    @Description("Demonstrate setting private field value to prepare test fixtures")
    @Severity(SeverityLevel.NORMAL)
    void shouldModifyPrivateField() {
        User user = new User();

        setFieldValue(user, "id", 999L);
        setFieldValue(user, "username", "reflectionUser");
        setFieldValue(user, "email", "reflection@test.com");
        setFieldValue(user, "active", false);

        assertThat(user.getId()).isEqualTo(999L);
        assertThat(user.getUsername()).isEqualTo("reflectionUser");
        assertThat(user.getEmail()).isEqualTo("reflection@test.com");
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should inject mock dependency into private field")
    @Description("Simulate dependency injection by setting private repository field")
    @Severity(SeverityLevel.NORMAL)
    void shouldInjectDependencyIntoPrivateField() {
        UserRepository customMockRepository = Mockito.mock(UserRepository.class);
        User expectedUser = new User(123L, "injected", "injected@test.com", true);
        when(customMockRepository.findById(123L)).thenReturn(Optional.of(expectedUser));

        setFieldValue(userService, "userRepository", customMockRepository);
        Optional<User> result = userService.getUserById(123L);

        assertThat(result)
            .as("Should use injected mock repository")
            .isPresent()
            .hasValueSatisfying(user -> {
                assertThat(user.getId()).isEqualTo(123L);
                assertThat(user.getUsername()).isEqualTo("injected");
            });
    }

    @Test
    @DisplayName("Should verify private final field immutability")
    @Description("Demonstrate that private final field maintains reference")
    @Severity(SeverityLevel.MINOR)
    void shouldVerifyPrivateFinalField() {
        UserRepository actualRepository = (UserRepository) getFieldValue(userService, "userRepository");

        assertThat(actualRepository)
            .as("Should be able to access private final repository field")
            .isSameAs(mockRepository);
    }

    @Test
    @DisplayName("Should handle reflection exception for non-existent field")
    @Description("Verify proper error handling when accessing non-existent field")
    @Severity(SeverityLevel.MINOR)
    void shouldHandleNonExistentField() {
        User user = new User();

        assertThatThrownBy(() -> getFieldValue(user, "nonExistentField"))
            .isInstanceOf(ReflectionTestUtils.ReflectionException.class)
            .hasMessageContaining("Failed to get field 'nonExistentField'");
    }

    @Test
    @DisplayName("Should create test fixture with complex internal state")
    @Description("Use reflection to prepare object with specific internal state for testing")
    @Severity(SeverityLevel.NORMAL)
    void shouldCreateComplexTestFixture() {
        User user = new User();

        setFieldValue(user, "id", null);
        setFieldValue(user, "username", "");
        setFieldValue(user, "email", "invalid-email");
        setFieldValue(user, "active", false);

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isEmpty();
        assertThat(user.getEmail()).isEqualTo("invalid-email");
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should access and verify multiple private fields at once")
    @Description("Demonstrate batch verification of internal state")
    @Severity(SeverityLevel.NORMAL)
    void shouldVerifyMultiplePrivateFields() {
        User user = new User(100L, "multifield", "multi@test.com", true);

        Long id = (Long) getFieldValue(user, "id");
        String username = (String) getFieldValue(user, "username");
        String email = (String) getFieldValue(user, "email");
        Boolean active = (Boolean) getFieldValue(user, "active");

        assertThat(id).isEqualTo(100L);
        assertThat(username).isEqualTo("multifield");
        assertThat(email).isEqualTo("multi@test.com");
        assertThat(active).isTrue();
    }

    @Test
    @DisplayName("Should modify private field and verify through public API")
    @Description("Ensure consistency between private field modification and public getter")
    @Severity(SeverityLevel.NORMAL)
    void shouldEnsureConsistencyBetweenPrivateAndPublicAccess() {
        User user = new User(1L, "original", "original@test.com", true);

        setFieldValue(user, "username", "modified");

        assertThat(user.getUsername())
            .as("Public getter should return the value set via reflection")
            .isEqualTo("modified");
    }

    @Test
    @DisplayName("Should handle primitive field types correctly")
    @Description("Verify reflection works with primitive boolean field")
    @Severity(SeverityLevel.MINOR)
    void shouldHandlePrimitiveFieldTypes() {
        User user = new User();

        setFieldValue(user, "active", true);
        assertThat(user.isActive()).isTrue();

        setFieldValue(user, "active", false);
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should demonstrate testing scenario: verify service internal state")
    @Description("Real-world example: verify service correctly stores repository reference")
    @Severity(SeverityLevel.NORMAL)
    void shouldVerifyServiceInternalState() {
        UserRepository newRepository = Mockito.mock(UserRepository.class);
        UserService newService = new UserService(newRepository);

        UserRepository storedRepository = (UserRepository) getFieldValue(newService, "userRepository");

        assertThat(storedRepository)
            .as("Service should correctly store repository reference passed in constructor")
            .isSameAs(newRepository);
    }
}
