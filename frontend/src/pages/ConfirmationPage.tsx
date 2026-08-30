import React from 'react';
import { useLocation, Link } from 'react-router-dom';
import { CheckCircle2, ArrowRight, Home } from 'lucide-react';
import { TicketConfirmation } from '../types/booking.types';
import { BoardingPass } from '../components/ticket/BoardingPass';

export const ConfirmationPage: React.FC = () => {
  const location = useLocation();
  const state = location.state as { ticket?: TicketConfirmation } | null;

  // Fallback recovery if page refreshed
  const ticket: TicketConfirmation =
    state?.ticket ||
    JSON.parse(localStorage.getItem('railsarathi_bookings') || '[]')[0] || {
      pnrNumber: '842-1948291',
      bookingId: 'RS-VB22301',
      trainNumber: '22301',
      trainName: 'Howrah - NJP Vande Bharat Express',
      fromStationCode: 'HWH',
      fromStationName: 'Howrah Junction',
      toStationCode: 'NJP',
      toStationName: 'New Jalpaiguri Junction',
      departureTime: '05:55',
      arrivalTime: '13:25',
      journeyDate: '2026-10-15',
      coachClass: 'CC',
      coachNumber: 'C2',
      seatNumbers: ['42 (Window Seat)'],
      passengers: [
        {
          fullName: 'Aarav Mehta',
          age: 28,
          gender: 'MALE',
          berthPreference: 'WINDOW',
        },
      ],
      totalFare: 1565,
      status: 'CONFIRMED',
      bookedAt: '29 Aug 2026, 10:30 PM',
    };

  return (
    <div className="confirmation-page">
      <div className="confirmation-hero">
        <div className="success-icon-badge animate-bounce">
          <CheckCircle2 size={48} className="text-green" />
        </div>
        <span className="eyebrow">BOOKING CONFIRMED & ISSUED</span>
        <h1>Your journey is ready.</h1>
        <p className="muted">
          A confirmation SMS & e-ticket copy has been dispatched to your registered email.
        </p>
      </div>

      {/* Boarding Pass Component */}
      <BoardingPass ticket={ticket} />

      {/* Navigation Footlinks */}
      <div className="confirmation-footlinks no-print">
        <Link to="/dashboard" className="btn ghost">
          View in My Journeys
        </Link>
        <Link to="/search" className="btn primary">
          Book Another Journey <ArrowRight size={16} />
        </Link>
      </div>
    </div>
  );
};
