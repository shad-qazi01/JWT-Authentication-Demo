package com.smartbiz.auth;

import com.smartbiz.auth.dto.LoginRequest;
import com.smartbiz.auth.dto.RegisterRequest;
import com.smartbiz.user.UserEntity;
import com.smartbiz.security.JwtService;
import com.smartbiz.user.Role;
import com.smartbiz.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
        @Mock
        private UserRepository userRepo;
        @Mock
        private JwtService jwtService;
        @Mock
        private PasswordEncoder encoder;
        @Mock
        private AuthenticationManager authManager;
        @InjectMocks
        private AuthService authService;

        @Test
        void register_shouldThrowException_ifEmailExists() {
                RegisterRequest request = new RegisterRequest("test@mail.com", "password");

                when(userRepo.existsByEmail(request.email()))
                                .thenReturn(true);

                assertThrows(IllegalArgumentException.class,
                                () -> authService.register(request));

//                verify(userRepo, never()).save(any());
        }

        // test: register success
        @Test
        void register_shouldSaveUser_whenEmailNotExists() {
                var request = new RegisterRequest("new@mail.com", "pwd");
                when(userRepo.existsByEmail("new@mail.com")).thenReturn(false);
                when(encoder.encode("pwd")).thenReturn("encodedPwd");

                authService.register(request);

                ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
                verify(userRepo).save(captor.capture());
                UserEntity saved = captor.getValue();

                assertEquals("new@mail.com", saved.getEmail());
                assertEquals("encodedPwd", saved.getPassword());
                assertEquals(Role.USER, saved.getRole());
        }

        // test: login success
        @Test
        void login_shouldReturnAuthResponse_whenCredentialsValid() {

            var request = new LoginRequest("user@mail.com", "pwd");

            UserEntity user = UserEntity.builder()
                    .email("user@mail.com")
                    .password("encoded")
                    .role(Role.USER)
                    .build();

            when(authManager.authenticate(any()))
                    .thenReturn(mock(Authentication.class));   // 🔥 THIS IS IMPORTANT

            when(userRepo.findByEmail("user@mail.com"))
                    .thenReturn(Optional.of(user));

            when(jwtService.generateToken(anyString(), anyMap()))
                    .thenReturn("token-123");

            var response = authService.login(request);

            assertNotNull(response);
            assertEquals("token-123", response.accessToken());
            assertEquals("Bearer", response.tokenType());
            assertTrue(response.expiresInSeconds() > 0);

            verify(authManager).authenticate(any());
        }

}
