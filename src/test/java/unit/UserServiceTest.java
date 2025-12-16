package unit;

import com.automation.qa.model.User;
import com.automation.qa.repository.UserRepository;
import com.automation.qa.service.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Feature("User Service")
@DisplayName("User Service Unit Tests")
@Tag("unit")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", true);
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should successfully create a new user")
        @Description("Verify that a valid user is created and saved to the repository")
        @Severity(SeverityLevel.CRITICAL)
        void shouldCreateUserSuccessfully() {
            // Arrange
            User newUser = new User(null, "newuser", "newuser@example.com", false);
            User savedUser = new User(2L, "newuser", "newuser@example.com", true);

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // Act
            User result = userService.createUser(newUser);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getUsername()).isEqualTo("newuser");
            assertThat(result.getEmail()).isEqualTo("newuser@example.com");
            assertThat(result.isActive()).isTrue();

            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("newuser@example.com");
            verify(userRepository).save(newUser);
        }

        @Test
        @DisplayName("Should throw exception when user is null")
        @Description("Verify that IllegalArgumentException is thrown when attempting to create a null user")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUserIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User cannot be null");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when username is empty")
        @Description("Verify that IllegalArgumentException is thrown when username is empty")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUsernameIsEmpty() {
            // Arrange
            User invalidUser = new User(null, "", "test@example.com", false);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(invalidUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Username cannot be empty");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        @Description("Verify that IllegalArgumentException is thrown when email is empty")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // Arrange
            User invalidUser = new User(null, "testuser", "  ", false);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(invalidUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email cannot be empty");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        @Description("Verify that IllegalStateException is thrown when username is already taken")
        @Severity(SeverityLevel.CRITICAL)
        void shouldThrowExceptionWhenUsernameExists() {
            // Arrange
            User newUser = new User(null, "existinguser", "new@example.com", false);
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(newUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Username already exists: existinguser");

            verify(userRepository).existsByUsername("existinguser");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        @Description("Verify that IllegalStateException is thrown when email is already registered")
        @Severity(SeverityLevel.CRITICAL)
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            User newUser = new User(null, "newuser", "existing@example.com", false);
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(newUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Email already exists: existing@example.com");

            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("existing@example.com");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should set user as active when creating")
        @Description("Verify that newly created users are automatically set to active status")
        @Severity(SeverityLevel.NORMAL)
        void shouldSetUserAsActiveWhenCreating() {
            // Arrange
            User newUser = new User(null, "newuser", "new@example.com", false);
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            // Act
            userService.createUser(newUser);

            // Assert
            verify(userRepository).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should successfully retrieve user by ID")
        @Description("Verify that a user can be retrieved by their ID")
        @Severity(SeverityLevel.CRITICAL)
        void shouldGetUserByIdSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.getUserById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            assertThat(result.get().getUsername()).isEqualTo("testuser");

            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return empty Optional when user not found by ID")
        @Description("Verify that an empty Optional is returned when user ID doesn't exist")
        @Severity(SeverityLevel.NORMAL)
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userService.getUserById(99L);

            // Assert
            assertThat(result).isEmpty();
            verify(userRepository).findById(99L);
        }

        @Test
        @DisplayName("Should throw exception when user ID is null")
        @Description("Verify that IllegalArgumentException is thrown when ID is null")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUserIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID");

            verify(userRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when user ID is negative")
        @Description("Verify that IllegalArgumentException is thrown when ID is negative")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUserIdIsNegative() {
            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID");

            verify(userRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should successfully retrieve user by username")
        @Description("Verify that a user can be retrieved by their username")
        @Severity(SeverityLevel.CRITICAL)
        void shouldGetUserByUsernameSuccessfully() {
            // Arrange
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.getUserByUsername("testuser");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should throw exception when username is empty")
        @Description("Verify that IllegalArgumentException is thrown when username is empty")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUsernameIsEmpty() {
            // Act & Assert
            assertThatThrownBy(() -> userService.getUserByUsername(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Username cannot be empty");

            verify(userRepository, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("Should retrieve all active users")
        @Description("Verify that all active users can be retrieved")
        @Severity(SeverityLevel.NORMAL)
        void shouldGetAllActiveUsers() {
            // Arrange
            User user1 = new User(1L, "user1", "user1@example.com", true);
            User user2 = new User(2L, "user2", "user2@example.com", true);
            List<User> activeUsers = Arrays.asList(user1, user2);

            when(userRepository.findActiveUsers()).thenReturn(activeUsers);

            // Act
            List<User> result = userService.getAllActiveUsers();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(user1, user2);
            verify(userRepository).findActiveUsers();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should successfully update user")
        @Description("Verify that user information can be updated")
        @Severity(SeverityLevel.CRITICAL)
        void shouldUpdateUserSuccessfully() {
            // Arrange
            User updatedUser = new User(null, "updateduser", "updated@example.com", true);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateUser(1L, updatedUser);

            // Assert
            assertThat(result).isNotNull();
            verify(userRepository).findById(1L);
            verify(userRepository).existsByUsername("updateduser");
            verify(userRepository).existsByEmail("updated@example.com");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        @Description("Verify that IllegalArgumentException is thrown when updating non-existent user")
        @Severity(SeverityLevel.CRITICAL)
        void shouldThrowExceptionWhenUserNotFoundForUpdate() {
            // Arrange
            User updatedUser = new User(null, "updateduser", "updated@example.com", true);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.updateUser(99L, updatedUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User not found with id: 99");

            verify(userRepository).findById(99L);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when updated user is null")
        @Description("Verify that IllegalArgumentException is thrown when updated user data is null")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUpdatedUserIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> userService.updateUser(1L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Updated user cannot be null");

            verify(userRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when new username already exists")
        @Description("Verify that IllegalStateException is thrown when updating to an existing username")
        @Severity(SeverityLevel.CRITICAL)
        void shouldThrowExceptionWhenNewUsernameExists() {
            // Arrange
            User updatedUser = new User(null, "existinguser", "test@example.com", true);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.updateUser(1L, updatedUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Username already exists: existinguser");

            verify(userRepository).findById(1L);
            verify(userRepository).existsByUsername("existinguser");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Deactivate User Tests")
    class DeactivateUserTests {

        @Test
        @DisplayName("Should successfully deactivate user")
        @Description("Verify that a user can be deactivated")
        @Severity(SeverityLevel.CRITICAL)
        void shouldDeactivateUserSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.deactivateUser(1L);

            // Assert
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).findById(1L);
            verify(userRepository).save(userCaptor.capture());

            User deactivatedUser = userCaptor.getValue();
            assertThat(deactivatedUser.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when user not found for deactivation")
        @Description("Verify that IllegalArgumentException is thrown when deactivating non-existent user")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUserNotFoundForDeactivation() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.deactivateUser(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User not found with id: 99");

            verify(userRepository).findById(99L);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should successfully delete user")
        @Description("Verify that a user can be deleted from the system")
        @Severity(SeverityLevel.CRITICAL)
        void shouldDeleteUserSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository).findById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when user not found for deletion")
        @Description("Verify that IllegalArgumentException is thrown when deleting non-existent user")
        @Severity(SeverityLevel.CRITICAL)
        void shouldThrowExceptionWhenUserNotFoundForDeletion() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User not found with id: 99");

            verify(userRepository).findById(99L);
            verify(userRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when user ID is invalid for deletion")
        @Description("Verify that IllegalArgumentException is thrown when ID is null or negative")
        @Severity(SeverityLevel.NORMAL)
        void shouldThrowExceptionWhenUserIdIsInvalidForDeletion() {
            // Act & Assert
            assertThatThrownBy(() -> userService.deleteUser(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID");

            assertThatThrownBy(() -> userService.deleteUser(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID");

            verify(userRepository, never()).findById(anyLong());
            verify(userRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should verify delete is called exactly once")
        @Description("Verify that deleteById is called exactly once when deleting a user")
        @Severity(SeverityLevel.NORMAL)
        void shouldVerifyDeleteIsCalledOnce() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository, times(1)).deleteById(1L);
        }
    }
}
