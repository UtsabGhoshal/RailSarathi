import React, { useState, useEffect } from 'react';
import {
  X,
  ShieldCheck,
  QrCode,
  CreditCard,
  Building2,
  Lock,
  CheckCircle2,
  AlertCircle,
  Zap,
  Smartphone,
} from 'lucide-react';
import { PaymentOrderDto, PaymentMethod, PaymentVerificationResultDto } from '../../types/payment.types';
import { LoadingSpinner } from '../common/LoadingSpinner';

interface PaymentModalProps {
  isOpen: boolean;
  order: PaymentOrderDto | null;
  onClose: () => void;
  onSuccess: (result: PaymentVerificationResultDto) => void;
  onVerify: (method: PaymentMethod, simulateSuccess?: boolean) => Promise<PaymentVerificationResultDto>;
}

export const PaymentModal: React.FC<PaymentModalProps> = ({
  isOpen,
  order,
  onClose,
  onSuccess,
  onVerify,
}) => {
  const [selectedMethod, setSelectedMethod] = useState<PaymentMethod>('UPI');
  const [isAuthorizing, setIsAuthorizing] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [timerSeconds, setTimerSeconds] = useState<number>(300);

  // Card mock state
  const [cardNumber, setCardNumber] = useState('4532 •••• •••• 8821');
  const [cardExpiry, setCardExpiry] = useState('11/28');
  const [cardCvv, setCardCvv] = useState('•••');

  // Countdown timer for QR code payment
  useEffect(() => {
    if (!isOpen) return;
    setTimerSeconds(300);
    const interval = setInterval(() => {
      setTimerSeconds((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [isOpen]);

  if (!isOpen || !order) return null;

  const formatTimer = (sec: number) => {
    const mins = Math.floor(sec / 60);
    const s = sec % 60;
    return `${mins}:${s < 10 ? '0' : ''}${s}`;
  };

  const formatCurrency = (amt: number) => `₹${amt.toLocaleString('en-IN')}`;

  const handlePayNow = async (simulateSuccess: boolean = true) => {
    setIsAuthorizing(true);
    setError(null);
    try {
      const result = await onVerify(selectedMethod, simulateSuccess);
      if (result.verified) {
        onSuccess(result);
      } else {
        setError(result.message || 'Payment authorization failed.');
      }
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Payment verification failed.');
      }
    } finally {
      setIsAuthorizing(false);
    }
  };

  return (
    <div className="backdrop" onClick={onClose}>
      <div className="payment-modal-card glass" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="payment-modal-header">
          <div className="gateway-branding">
            <span className="eyebrow">RAILSARATHI SECURE CHECKOUT</span>
            <h2>Complete Payment</h2>
            <span className="txn-ref-tag">{order.transactionReference}</span>
          </div>

          <div className="amount-highlight-box">
            <span className="amt-label">TOTAL PAYABLE</span>
            <strong className="amt-value">{formatCurrency(order.amount)}</strong>
          </div>

          <button type="button" className="close-btn" onClick={onClose} title="Cancel Checkout">
            <X size={18} />
          </button>
        </div>

        {error && (
          <div className="payment-error-banner">
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        {/* Method Selector Tabs */}
        <div className="payment-method-tabs">
          <button
            type="button"
            className={`method-tab ${selectedMethod === 'UPI' ? 'active' : ''}`}
            onClick={() => setSelectedMethod('UPI')}
          >
            <Smartphone size={16} />
            <span>UPI Instant</span>
          </button>

          <button
            type="button"
            className={`method-tab ${selectedMethod === 'CREDIT_CARD' ? 'active' : ''}`}
            onClick={() => setSelectedMethod('CREDIT_CARD')}
          >
            <CreditCard size={16} />
            <span>Cards</span>
          </button>

          <button
            type="button"
            className={`method-tab ${selectedMethod === 'NET_BANKING' ? 'active' : ''}`}
            onClick={() => setSelectedMethod('NET_BANKING')}
          >
            <Building2 size={16} />
            <span>Net Banking</span>
          </button>

          <button
            type="button"
            className={`method-tab ${selectedMethod === 'MOCK' ? 'active' : ''}`}
            onClick={() => setSelectedMethod('MOCK')}
          >
            <Zap size={16} />
            <span>Sandbox</span>
          </button>
        </div>

        {/* Method Body Content */}
        <div className="method-content-area">
          {selectedMethod === 'UPI' && (
            <div className="upi-view text-center">
              <div className="qr-preview-box glass mx-auto">
                <QrCode size={120} className="text-cyan mx-auto" />
                <span className="upi-id-text">UPI ID: railsarathi@irctc</span>
              </div>
              <div className="timer-badge">
                <span>QR expires in <b>{formatTimer(timerSeconds)}</b></span>
              </div>
              <p className="muted text-sm mt-2">
                Scan with GPay, PhonePe, Paytm, or BHIM to authorize payment.
              </p>
            </div>
          )}

          {selectedMethod === 'CREDIT_CARD' && (
            <div className="card-form-view">
              <label>
                <span>Card Number</span>
                <div className="card-input-box">
                  <CreditCard size={16} />
                  <input
                    type="text"
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                    placeholder="4532 •••• •••• 8821"
                  />
                </div>
              </label>

              <div className="grid grid-cols-2 gap-4 mt-3">
                <label>
                  <span>Valid Thru</span>
                  <input
                    type="text"
                    value={cardExpiry}
                    onChange={(e) => setCardExpiry(e.target.value)}
                    placeholder="MM/YY"
                  />
                </label>
                <label>
                  <span>CVV / CVC</span>
                  <input
                    type="password"
                    value={cardCvv}
                    onChange={(e) => setCardCvv(e.target.value)}
                    placeholder="•••"
                  />
                </label>
              </div>
            </div>
          )}

          {selectedMethod === 'NET_BANKING' && (
            <div className="netbanking-grid">
              {['HDFC Bank', 'State Bank of India', 'ICICI Bank', 'Axis Bank', 'Kotak Mahindra', 'Punjab National Bank'].map((bank) => (
                <button
                  key={bank}
                  type="button"
                  className="bank-chip glass"
                  onClick={() => handlePayNow(true)}
                >
                  <Building2 size={16} className="text-cyan" />
                  <span>{bank}</span>
                </button>
              ))}
            </div>
          )}

          {selectedMethod === 'MOCK' && (
            <div className="sandbox-panel glass p-4 text-center">
              <Zap size={32} className="text-cyan mx-auto mb-2" />
              <h4>Zero-Credential Developer Sandbox</h4>
              <p className="muted text-sm mt-1 mb-3">
                Test instant authorization and payment rollback flows without external gateway setup.
              </p>
              <div className="flex gap-3 justify-center">
                <button
                  type="button"
                  className="btn primary small"
                  onClick={() => handlePayNow(true)}
                  disabled={isAuthorizing}
                >
                  Simulate Success
                </button>
                <button
                  type="button"
                  className="btn danger small"
                  onClick={() => handlePayNow(false)}
                  disabled={isAuthorizing}
                >
                  Simulate Failure
                </button>
              </div>
            </div>
          )}
        </div>

        {/* CTA Actions */}
        {selectedMethod !== 'MOCK' && (
          <button
            type="button"
            className="btn primary wide pay-cta-btn"
            onClick={() => handlePayNow(true)}
            disabled={isAuthorizing}
          >
            {isAuthorizing ? (
              <LoadingSpinner inline size={16} message="Authorizing with Gateway..." />
            ) : (
              <>
                <Lock size={16} /> Pay {formatCurrency(order.amount)} Now
              </>
            )}
          </button>
        )}

        {/* Security Footer */}
        <div className="payment-footer-trust">
          <ShieldCheck size={14} className="text-green" />
          <span>256-Bit SSL Encrypted · Instant Refund Protected · PCI-DSS Level 1</span>
        </div>
      </div>
    </div>
  );
};
