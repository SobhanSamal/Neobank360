package com.neobank.controller;
 
import com.neobank.dto.UserActivityDTO;
import com.neobank.dto.UserResponse;
import com.neobank.entity.User;
import com.neobank.repository.LoginEventRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;
import com.neobank.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Tests")
class AdminControllerTest {
 
    @Mock
    private UserRepository userRepository;
 
    @Mock
    private TransactionRepository transactionRepository;
 
    @Mock
    private LoginEventRepository loginEventRepository;
 
    @Mock
    private AuditLogService auditLogService;
 
    @Mock
    private Authentication authentication;
 
    @InjectMocks
    private AdminController controller;
 
    private User admin;
    private User customer;
 
    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@neobank.com");
        admin.setFullName("Admin");
        admin.setRole(User.Role.ADMIN);
        admin.setIsActive(true);
        admin.setCreatedAt(LocalDateTime.now());
 
        customer = new User();
        customer.setId(2L);
        customer.setEmail("customer@neobank.com");
        customer.setFullName("Customer");
        customer.setRole(User.Role.CUSTOMER);
        customer.setIsActive(true);
        customer.setCreatedAt(LocalDateTime.now());
    }
 
    @Test
    @DisplayName("getAllUsers returns paginated response")
    void getAllUsersReturnsPage() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(customer)));
 
        ResponseEntity<?> response = controller.getAllUsers(0, 20);
 
        assertEquals(200, response.getStatusCodeValue());
    }
 
    @Test
    @DisplayName("updateUserStatus blocks self-deactivation")
    void updateUserStatusBlocksSelfDeactivate() {
        when(authentication.getName()).thenReturn(admin.getEmail());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
 
        Map<String, Boolean> payload = Map.of("isActive", false);
 
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.updateUserStatus(1L, payload, authentication));
 
        assertEquals(400, ex.getStatusCode().value());
    }
 
    @Test
    @DisplayName("getUserActivity returns transactions and login events")
    void getUserActivityReturnsData() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findTop20ByAccountUserIdOrderByTransactionDateDesc(2L)).thenReturn(List.of());
        when(loginEventRepository.findTop5ByUser_IdOrderByLoginAtDesc(2L)).thenReturn(List.of());
 
        ResponseEntity<UserActivityDTO> response = controller.getUserActivity(2L);
 
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2L, response.getBody().getUserId());
    }
}
 