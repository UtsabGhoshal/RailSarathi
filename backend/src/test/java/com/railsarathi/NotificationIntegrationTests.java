package com.railsarathi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.railsarathi.dto.SendNotificationRequest;
import com.railsarathi.entity.User;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.enums.NotificationPriority;
import com.railsarathi.enums.NotificationType;
import com.railsarathi.enums.Role;
import com.railsarathi.repository.NotificationRepository;
import com.railsarathi.repository.UserRepository;
import com.railsarathi.security.JwtTokenProvider;
import com.railsarathi.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class NotificationIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        notificationRepository.deleteAll();

        testUser = userRepository.findByEmail("test_notify_user@railsarathi.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName("Notification Test User")
                        .username("notify_tester")
                        .email("test_notify_user@railsarathi.com")
                        .password(passwordEncoder.encode("Password@123"))
                        .role(Role.ROLE_PASSENGER)
                        .phone("+919876543210")
                        .build()));

        jwtToken = tokenProvider.generateToken(testUser, java.util.UUID.randomUUID().toString());
    }

    @Test
    void testSendAndRetrieveInAppNotification() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientUserId(testUser.getId())
                .title("Train 22301 Confirmed")
                .message("Your booking is confirmed on Vande Bharat.")
                .type(NotificationType.BOOKING_CONFIRMATION)
                .priority(NotificationPriority.HIGH)
                .channel(NotificationChannel.IN_APP)
                .build();

        notificationService.send(request);

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title").value("Train 22301 Confirmed"))
                .andExpect(jsonPath("$.data.content[0].status").value("UNREAD"));
    }

    @Test
    void testUnreadCountAndMarkAsRead() throws Exception {
        // Create 2 notifications
        notificationService.sendToUser(testUser, NotificationType.INFO, "Notice 1", "Message 1", null, NotificationChannel.IN_APP);
        var n2 = notificationService.sendToUser(testUser, NotificationType.WARNING, "Notice 2", "Message 2", null, NotificationChannel.IN_APP);

        // Check count
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount").value(2));

        // Mark Notice 2 as read
        mockMvc.perform(put("/api/v1/notifications/" + n2.getId() + "/read")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("READ"));

        // Check updated unread count
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount").value(1));
    }

    @Test
    void testMarkAllAsRead() throws Exception {
        notificationService.sendToUser(testUser, NotificationType.INFO, "Msg A", "Body A", null, NotificationChannel.IN_APP);
        notificationService.sendToUser(testUser, NotificationType.INFO, "Msg B", "Body B", null, NotificationChannel.IN_APP);
        notificationService.sendToUser(testUser, NotificationType.INFO, "Msg C", "Body C", null, NotificationChannel.IN_APP);

        assertEquals(3, notificationService.getUnreadCount(testUser));

        mockMvc.perform(put("/api/v1/notifications/read-all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updatedCount").value(3));

        assertEquals(0, notificationService.getUnreadCount(testUser));
    }

    @Test
    void testSendEndpointWithGenericRequest() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .title("Urgent Schedule Alert")
                .message("Train 12301 platform changed to PF 4.")
                .type(NotificationType.PLATFORM_CHANGE)
                .priority(NotificationPriority.URGENT)
                .channel(NotificationChannel.ALL)
                .build();

        mockMvc.perform(post("/api/v1/notifications/send")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Urgent Schedule Alert"))
                .andExpect(jsonPath("$.data.type").value("PLATFORM_CHANGE"));
    }

    @Test
    void testSseStreamSubscription() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/notifications/stream?token=" + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result.getResponse().getContentType());
        assertEquals("text/event-stream", result.getResponse().getContentType());
    }
}
