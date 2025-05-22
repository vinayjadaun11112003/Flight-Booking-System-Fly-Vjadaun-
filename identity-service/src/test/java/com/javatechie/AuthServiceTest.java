package com.javatechie;
import com.javatechie.Exceptions.UserAlreadyExistsException;
import com.javatechie.dto.Msg;
import com.javatechie.entity.UserCredential;
import com.javatechie.repository.UserCredentialRepository;
import com.javatechie.service.AuthService;
import com.javatechie.service.JwtService;
import com.javatechie.utils.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Notification notification;

    private UserCredential testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new UserCredential();
        testUser.setId("1");
        testUser.setName("john");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");
    }

    @Test
    void shouldReturnAllUsers() {
        when(repository.findAll()).thenReturn(List.of(testUser));

        List<UserCredential> users = authService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("john", users.get(0).getName());
    }

    @Test
    void shouldReturnUserById() {
        when(repository.findById("1")).thenReturn(Optional.of(testUser));

        Optional<UserCredential> result = authService.getUserById("1");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getName());
    }

    @Test
    void shouldUpdateUserIfExists() {
        when(repository.findById("1")).thenReturn(Optional.of(testUser));
        when(repository.save(any(UserCredential.class))).thenReturn(testUser);

        testUser.setEmail("newemail@example.com");
        ResponseEntity<Map<String, String>> response = authService.updateUser("1", testUser);

        assertEquals("successfully udated user credentials with id : 1", response.getBody().get("message"));
    }

    @Test
    void shouldReturnUserNotFoundOnUpdate() {
        when(repository.findById("2")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = authService.updateUser("2", testUser);

        assertEquals("user not found with the id : 2", response.getBody().get("message"));
    }

    @Test
    void shouldReturnUserByName() {
        when(repository.findByName("john")).thenReturn(Optional.of(testUser));

        Optional<UserCredential> user = authService.getUserByName("john");

        assertTrue(user.isPresent());
        assertEquals("john@example.com", user.get().getEmail());
    }

    @Test
    void shouldDeleteUserIfExists() {
        when(repository.findById("1")).thenReturn(Optional.of(testUser));

        Map<String, String> result = authService.deleteUser("1");

        verify(repository, times(1)).deleteById("1");
        assertEquals("successfully Deleted user credentials with id : 1", result.get("message"));
    }

    @Test
    void shouldNotDeleteIfUserNotFound() {
        when(repository.findById("2")).thenReturn(Optional.empty());

        Map<String, String> result = authService.deleteUser("2");

        assertEquals("user not found with the id : 2", result.get("message"));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        when(repository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.saveUser(testUser);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameExists() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.existsByName(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.saveUser(testUser);
        });
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.existsByName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        ResponseEntity<Map<String, String>> response = authService.saveUser(testUser);

        verify(repository).save(any(UserCredential.class));
        verify(notification).send(any(Msg.class));
        assertEquals("User created successfully!", response.getBody().get("message"));
    }

    @Test
    void shouldGenerateToken() {
        when(jwtService.generateToken("john")).thenReturn("dummy-token");

        ResponseEntity<Map<String, String>> response = authService.generateToken("john");

        assertEquals("dummy-token", response.getBody().get("token"));
    }

    @Test
    void shouldCallValidateToken() {
        authService.validateToken("some-token");

        verify(jwtService).validateToken("some-token");
    }
}
