# 💳 Universal Payment Gateway & Service – Boilerplate & Starter Reference

This document provides the **complete architectural guide and reusable code blueprint** for dropping the RailSarathi Payment Gateway Service into any Spring Boot + React project (e-commerce, SaaS billing, subscriptions).

---

## 🌟 Architectural Features
1. **Pluggable Payment Gateway Strategy Pattern (Open-Closed Principle):** Core payment service routes checkout orders and verifications to matching `PaymentGatewayProvider` implementations (`MOCK_SANDBOX`, `RAZORPAY`, `STRIPE`).
2. **Extensibility in 1 Step:** To add PhonePe, Paytm, PayPal, or Adyen, create a single `@Component` class implementing `PaymentGatewayProvider`.
3. **Double-Charge Idempotency Engine:** Unique `idempotencyKey` prevents duplicate transaction charges on network timeouts or double clicks.
4. **Automated Instant Refunds:** Full and partial refund processing with transaction ledgers and gateway reconciliation.
5. **Drop-in React Checkout UI:** `usePayment()` hook + `PaymentModal` multi-instrument checkout (UPI QR code, Net Banking, Cards, and Developer Sandbox).

---

## 🏗️ 1. Backend Architecture Blueprint

### 1.1 Provider Contract (`PaymentGatewayProvider.java`)
```java
public interface PaymentGatewayProvider {
    PaymentGatewayType getGatewayType();
    PaymentOrderDto createOrder(CreatePaymentOrderRequest request, String transactionReference);
    PaymentVerificationResultDto verifyPayment(VerifyPaymentRequest request, PaymentTransaction transaction);
    RefundResultDto processRefund(RefundRequestDto request, PaymentTransaction transaction);
}
```

### 1.2 Adding a New Gateway (e.g. PhonePe / PayPal)
To integrate any new payment provider into any future project, implement `PaymentGatewayProvider`:

```java
@Component
@Slf4j
public class PhonePePaymentProvider implements PaymentGatewayProvider {

    @Override
    public PaymentGatewayType getGatewayType() {
        return PaymentGatewayType.PHONEPE;
    }

    @Override
    public PaymentOrderDto createOrder(CreatePaymentOrderRequest request, String transactionRef) {
        // Call PhonePe /v3/charge API and return checkout redirect URL
        return PaymentOrderDto.builder()
                .transactionReference(transactionRef)
                .gatewayOrderId("phonepe_order_123")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.INITIATED)
                .build();
    }

    @Override
    public PaymentVerificationResultDto verifyPayment(VerifyPaymentRequest request, PaymentTransaction transaction) {
        // Verify PhonePe checksum X-VERIFY
        return PaymentVerificationResultDto.builder()
                .transactionReference(transaction.getTransactionReference())
                .status(PaymentStatus.SUCCESS)
                .verified(true)
                .build();
    }

    @Override
    public RefundResultDto processRefund(RefundRequestDto request, PaymentTransaction transaction) {
        // Call PhonePe /v3/credit API
        return RefundResultDto.builder()
                .refundReference("RFD-PHONEPE-123")
                .status(RefundStatus.PROCESSED)
                .build();
    }
}
```

---

## ⚛️ 2. Frontend React Integration Blueprint

### 2.1 Custom Hook (`usePayment.ts`)
```typescript
import { usePayment } from '@/hooks/usePayment';

function CheckoutPage() {
  const { isModalOpen, activeOrder, initiateCheckout, completeVerification, closeModal } = usePayment();

  const handleCheckout = async () => {
    await initiateCheckout({
      amount: 1565,
      currency: 'INR',
      paymentMethod: 'UPI',
      gatewayType: 'MOCK_SANDBOX',
      description: 'Order Checkout',
    });
  };

  return (
    <div>
      <button onClick={handleCheckout}>Pay ₹1,565</button>

      <PaymentModal
        isOpen={isModalOpen}
        order={activeOrder}
        onClose={closeModal}
        onSuccess={(receipt) => console.log('Paid successfully:', receipt)}
        onVerify={completeVerification}
      />
    </div>
  );
}
```

---

## 📡 3. REST API Contract

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/payments/create-order` | Initiates checkout order & returns gateway tokens / UPI QR |
| `POST` | `/api/v1/payments/verify` | Verifies signature or authorizes sandbox transaction |
| `POST` | `/api/v1/payments/refund` | Executes instant full or partial refund |
| `GET` | `/api/v1/payments/transactions/{ref}` | Retrieves detailed transaction ledger |
| `GET` | `/api/v1/payments/history` | Paginated user payment & refund history |
| `POST` | `/api/v1/payments/webhook/{provider}` | Asynchronous gateway callback receiver |

---

## 📋 4. How to Copy to a New Project (3 Steps)

1. **Copy Backend Package:** Copy `com.railsarathi.service.payment`, `entity/PaymentTransaction.java`, `entity/RefundTransaction.java`, `enums/Payment*.java`, and `controller/PaymentController.java`.
2. **Copy Frontend Module:** Copy `src/components/payment/`, `src/hooks/usePayment.ts`, `src/api/paymentApi.ts`, and `src/types/payment.types.ts`.
3. **Mount Checkout Modal:** Use `<PaymentModal />` in any purchase/checkout view!
