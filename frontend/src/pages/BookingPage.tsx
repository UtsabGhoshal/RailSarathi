import React, { useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Shield, CheckCircle2, Lock } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { usePayment } from '../hooks/usePayment';
import { TrainSearchResultDto, CoachClass } from '../types/train.types';
import { Passenger, TicketConfirmation } from '../types/booking.types';
import { PassengerForm } from '../components/booking/PassengerForm';
import { FareSummary } from '../components/booking/FareSummary';
import { PaymentModal } from '../components/payment/PaymentModal';

export const BookingPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isAuthenticated, openAuthModal } = useAuth();
  const {
    activeOrder,
    isModalOpen,
    isProcessing: isPaymentProcessing,
    initiateCheckout,
    completeVerification,
    closeModal,
  } = usePayment();

  // State passed from TrainCard or TrainDetailsPage
  const state = location.state as {
    train?: TrainSearchResultDto;
    selectedClass?: CoachClass;
    fare?: number;
    className?: string;
    classCode?: string;
    journeyDate?: string;
  } | null;

  // Fallback train if direct URL access
  const train = state?.train || {
    trainId: 1,
    trainNumber: '22301',
    trainName: 'Howrah - NJP Vande Bharat Express',
    trainType: 'VANDE_BHARAT' as const,
    originStation: { id: 1, stationCode: 'HWH', stationName: 'Howrah Junction', city: 'Kolkata', state: 'West Bengal' },
    terminusStation: { id: 6, stationCode: 'NJP', stationName: 'New Jalpaiguri', city: 'Siliguri', state: 'West Bengal' },
    boardingStation: { id: 1, stationCode: 'HWH', stationName: 'Howrah Junction', city: 'Kolkata', state: 'West Bengal' },
    destinationStation: { id: 6, stationCode: 'NJP', stationName: 'New Jalpaiguri', city: 'Siliguri', state: 'West Bengal' },
    departureTime: '05:55',
    arrivalTime: '13:25',
    durationFormatted: '07h 30m',
    travelDistanceKm: 565,
    dayDifference: 0,
    runsOnDays: 'MON,TUE,THU,FRI,SAT,SUN',
    availableClasses: [],
    routeSchedule: [],
  };

  const selectedClass = state?.selectedClass || ('AC_CHAIR_CAR' as CoachClass);
  const farePerPassenger = state?.fare || 1565;
  const journeyDate = state?.journeyDate || new Date().toISOString().split('T')[0];

  const [passengers, setPassengers] = useState<Passenger[]>([
    {
      fullName: user?.fullName || 'Aarav Mehta',
      age: 28,
      gender: 'MALE',
      berthPreference: 'LOWER',
    },
  ]);

  const [contactEmail, setContactEmail] = useState<string>(user?.email || 'traveller@railsarathi.com');
  const [contactPhone, setContactPhone] = useState<string>(user?.phoneNumber || user?.phone || '+91 98765 43210');

  const baseFareTotal = farePerPassenger * passengers.length;
  const totalAmount = baseFareTotal + 40 + Math.round(baseFareTotal * 0.05);

  const handleProceedPayment = async () => {
    if (!isAuthenticated) {
      openAuthModal('login');
      return;
    }

    // Validate passengers
    for (const p of passengers) {
      if (!p.fullName.trim()) {
        alert('Please enter full name for all passengers.');
        return;
      }
    }

    try {
      await initiateCheckout({
        amount: totalAmount,
        currency: 'INR',
        paymentMethod: 'UPI',
        gatewayType: 'MOCK_SANDBOX',
        customerName: passengers[0].fullName,
        customerEmail: contactEmail,
        customerPhone: contactPhone,
        description: `RailSarathi Ticket Booking: ${train.trainNumber} (${train.boardingStation.stationCode} -> ${train.destinationStation.stationCode})`,
      });
    } catch {
      // Error handled in hook
    }
  };

  const handlePaymentSuccess = (verificationResult: { transactionReference: string; receiptNumber?: string }) => {
    const pnrPrefix = Math.floor(100 + Math.random() * 900);
    const pnrSuffix = Math.floor(1000000 + Math.random() * 9000000);
    const generatedPnr = `${pnrPrefix}-${pnrSuffix}`;
    const bookingId = verificationResult.receiptNumber || `RS-${Math.random().toString(36).substring(2, 9).toUpperCase()}`;

    const seatNumbers = passengers.map(
      (_, i) => `${(i + 1) * 6 + 4} (${passengers[i].berthPreference.replace('_', ' ')})`
    );

    const ticket: TicketConfirmation = {
      pnrNumber: generatedPnr,
      bookingId,
      trainNumber: train.trainNumber,
      trainName: train.trainName,
      fromStationCode: train.boardingStation.stationCode,
      fromStationName: train.boardingStation.stationName,
      toStationCode: train.destinationStation.stationCode,
      toStationName: train.destinationStation.stationName,
      departureTime: train.departureTime,
      arrivalTime: train.arrivalTime,
      journeyDate,
      coachClass: state?.classCode || 'CC',
      coachNumber: 'C2',
      seatNumbers,
      passengers,
      totalFare: totalAmount,
      status: 'CONFIRMED',
      bookedAt: new Date().toLocaleString('en-IN'),
    };

    // Save to local storage for dashboard history
    const history = JSON.parse(localStorage.getItem('railsarathi_bookings') || '[]');
    localStorage.setItem('railsarathi_bookings', JSON.stringify([ticket, ...history]));

    navigate('/booking/confirmation', { state: { ticket } });
  };

  return (
    <div className="booking-page">
      <Link to="/search" className="back-link">
        <ArrowLeft size={16} /> Back to Train Search
      </Link>

      <div className="booking-header glass">
        <div>
          <span className="eyebrow">STEP 1 OF 2 · PASSENGER & BERTH SELECTION</span>
          <h1>Review & Complete Reservation</h1>
          <p className="muted">
            {train.trainNumber} · {train.trainName} | {train.boardingStation.stationCode} → {train.destinationStation.stationCode} on {journeyDate}
          </p>
        </div>

        {!isAuthenticated && (
          <div className="auth-alert-pill">
            <Lock size={15} />
            <span>Sign in required to finalize ticket</span>
          </div>
        )}
      </div>

      <div className="booking-main-grid">
        {/* Left Column: Passenger Form */}
        <section className="passenger-section">
          <PassengerForm
            passengers={passengers}
            onChange={setPassengers}
            contactEmail={contactEmail}
            onEmailChange={setContactEmail}
            contactPhone={contactPhone}
            onPhoneChange={setContactPhone}
          />
        </section>

        {/* Right Column: Dynamic Fare Summary */}
        <aside className="fare-summary-column">
          <FareSummary
            train={train}
            selectedClass={selectedClass}
            farePerPassenger={farePerPassenger}
            passengerCount={passengers.length}
            onProceed={handleProceedPayment}
            isProcessing={isPaymentProcessing}
          />
        </aside>
      </div>

      {/* Payment Gateway Checkout Modal */}
      <PaymentModal
        isOpen={isModalOpen}
        order={activeOrder}
        onClose={closeModal}
        onSuccess={handlePaymentSuccess}
        onVerify={completeVerification}
      />
    </div>
  );
};
