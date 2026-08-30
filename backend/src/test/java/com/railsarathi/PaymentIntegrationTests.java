package com.railsarathi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.railsarathi.dto.payment.CreatePaymentOrderRequest;
import com.railsarathi.dto.payment.PaymentOrderDto;
import com.railsarathi.dto.payment.RefundRequestDto;
import com.railsarathi.dto.payment.VerifyPaymentRequest;
import com.railsarathi.entity.User;
import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentMethod;
import com.railsarathi.enums.PaymentStatus;
import com.railsarathi.enums.Role;
import com.railsarathi.repository.PaymentTransactionRepository;
import com.railsarathi.repository.RefundTransactionRepository;
import com.railsarathi.repository.UserRepository;
import com.railsarathi.security.JwtTokenProvider;
import com.railsarathi.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PaymentIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private RefundTransactionRepository refundRepository;

    @Autowired
    private PaymentService paymentService;

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

        refundRepository.deleteAll();
        transactionRepository.deleteAll();

        testUser = userRepository.findByEmail("test_payment_user@railsarathi.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName("Payment Test User")
                        .username("payment_tester")
                        .email("test_payment_user@railsarathi.com")
                        .password(passwordEncoder.encode("Password@123"))
                        .role(Role.ROLE_PASSENGER)
                        .phone("+919876543210")
                        .build()));

        jwtToken = tokenProvider.generateToken(testUser, UUID.randomUUID().toString());
    }

    @Test
    void testCreatePaymentOrderSandbox() throws Exception {
        CreatePaymentOrderRequest request = CreatePaymentOrderRequest.builder()
                .amount(new BigDecimal("1565.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .gatewayType(PaymentGatewayType.MOCK_SANDBOX)
                .description("Vande Bharat Ticket Booking")
                .build();

        mockMvc.perform(post("/api/v1/payments/create-order")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionReference", startsWith("TXN-")))
                .andExpect(jsonPath("$.data.amount").value(1565.00))
                .andExpect(jsonPath("$.data.status").value("INITIATED"));
    }

    @Test
    void testVerifyPaymentSuccess() throws Exception {
        // 1. Create order
        PaymentOrderDto order = paymentService.createOrder(testUser, CreatePaymentOrderRequest.builder()
                .amount(new BigDecimal("2450.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .gatewayType(PaymentGatewayType.MOCK_SANDBOX)
                .build());

        // 2. Verify payment
        VerifyPaymentRequest verifyRequest = VerifyPaymentRequest.builder()
                .transactionReference(order.getTransactionReference())
                .paymentMethod(PaymentMethod.UPI)
                .simulateSuccess(true)
                .build();

        mockMvc.perform(post("/api/v1/payments/verify")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.verified").value(true))
                .andExpect(jsonPath("$.data.gatewayPaymentId", startsWith("pay_")));
    }

    @Test
    void testIdempotencyProtection() throws Exception {
        String idempotencyKey = "IDEMP-BOOKING-994182";

        CreatePaymentOrderRequest req = CreatePaymentOrderRequest.builder()
                .amount(new BigDecimal("1200.00"))
                .idempotencyKey(idempotencyKey)
                .gatewayType(PaymentGatewayType.MOCK_SANDBOX)
                .build();

        // First attempt
        PaymentOrderDto first = paymentService.createOrder(testUser, req);

        // Second duplicate attempt
        PaymentOrderDto second = paymentService.createOrder(testUser, req);

        assertEquals(first.getTransactionReference(), second.getTransactionReference());
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void testInstantRefundProcessing() throws Exception {
        // 1. Create and complete transaction
        PaymentOrderDto order = paymentService.createOrder(testUser, CreatePaymentOrderRequest.builder()
                .amount(new BigDecimal("1565.00"))
                .gatewayType(PaymentGatewayType.MOCK_SANDBOX)
                .build());

        paymentService.verifyPayment(testUser, VerifyPaymentRequest.builder()
                .transactionReference(order.getTransactionReference())
                .simulateSuccess(true)
                .build());

        // 2. Execute refund
        RefundRequestDto refundRequest = RefundRequestDto.builder()
                .transactionReference(order.getTransactionReference())
                .refundAmount(new BigDecimal("1445.00"))
                .reason("Train Cancelled by Passenger")
                .build();

        mockMvc.perform(post("/api/v1/payments/refund")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PROCESSED"))
                .andExpect(jsonPath("$.data.refundReference", startsWith("RFD-")));
    }

    @Test
    void testPaymentHistoryRetrieval() throws Exception {
        paymentService.createOrder(testUser, CreatePaymentOrderRequest.builder()
                .amount(new BigDecimal("800.00"))
                .gatewayType(PaymentGatewayType.MOCK_SANDBOX)
                .build());

        mockMvc.perform(get("/api/v1/payments/history")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }
}
