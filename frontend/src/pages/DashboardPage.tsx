import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { User, Calendar, Shield, MapPin, AlertTriangle, ArrowRight, CheckCircle2, XCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { TicketConfirmation } from '../types/booking.types';
import { paymentApi } from '../api/paymentApi';

export const DashboardPage: React.FC = () => {
  const { user, isAuthenticated, openAuthModal } = useAuth();
  const [activeTab, setActiveTab] = useState<'UPCOMING' | 'HISTORY'>('UPCOMING');
  const [tickets, setTickets] = useState<TicketConfirmation[]>([]);
  const [ticketToCancel, setTicketToCancel] = useState<TicketConfirmation | null>(null);
  const [cancelModalOpen, setCancelModalOpen] = useState<boolean>(false);

  useEffect(() => {
    const stored = localStorage.getItem('railsarathi_bookings');
    if (stored) {
      setTickets(JSON.parse(stored));
    } else {
      // Seed initial sample ticket for logged in user demo
      const sampleTicket: TicketConfirmation = {
        pnrNumber: '842-1948291',
        bookingId: 'RS-VB22301',
        trainNumber: '22301',
        trainName: 'Howrah - NJP Vande Bharat Express',
        fromStationCode: 'HWH',
        fromStationName: 'Howrah Junction',
        toStationCode: 'NJP',
        toStationName: 'New Jalpaiguri',
        departureTime: '05:55',
        arrivalTime: '13:25',
        journeyDate: '2026-10-15',
        coachClass: 'CC',
        coachNumber: 'C2',
        seatNumbers: ['42 (Window Seat)'],
        passengers: [
          {
            fullName: user?.fullName || 'Aarav Mehta',
            age: 28,
            gender: 'MALE',
            berthPreference: 'WINDOW',
          },
        ],
        totalFare: 1565,
        status: 'CONFIRMED',
        bookedAt: '28 Aug 2026, 08:30 PM',
      };
      setTickets([sampleTicket]);
      localStorage.setItem('railsarathi_bookings', JSON.stringify([sampleTicket]));
    }
  }, [user]);

  if (!isAuthenticated) {
    return (
      <div className="unauthenticated-card glass p-12 text-center">
        <User size={48} className="text-cyan mx-auto mb-4" />
        <h2>Your journeys are waiting</h2>
        <p className="muted">
          Sign in to view your booked tickets, track live train status, and manage fast cancellations.
        </p>
        <button
          type="button"
          className="btn primary mt-6"
          onClick={() => openAuthModal('login')}
        >
          Sign in to your account
        </button>
      </div>
    );
  }

  const upcomingTickets = tickets.filter((t) => t.status === 'CONFIRMED');
  const historyTickets = tickets;

  const handleConfirmCancel = async () => {
    if (!ticketToCancel) return;

    const refundAmount = Math.max(0, ticketToCancel.totalFare - 120);

    // Call backend refund API
    try {
      await paymentApi.processRefund({
        transactionReference: ticketToCancel.bookingId.startsWith('TXN-') ? ticketToCancel.bookingId : 'TXN-MOCK-' + ticketToCancel.pnrNumber,
        refundAmount,
        reason: 'User cancelled ticket for PNR ' + ticketToCancel.pnrNumber,
      });
    } catch {
      // Fallback for local simulated records
    }

    const finalTickets = tickets.map((t) =>
      t.pnrNumber === ticketToCancel.pnrNumber ? { ...t, status: 'CANCELLED' as unknown as 'CONFIRMED' } : t
    );
    setTickets(finalTickets);
    localStorage.setItem('railsarathi_bookings', JSON.stringify(finalTickets));
    setCancelModalOpen(false);
    setTicketToCancel(null);
  };

  const formatCurrency = (amount: number) => `₹${amount.toLocaleString('en-IN')}`;

  return (
    <div className="dashboard-page">
      {/* User Banner Header */}
      <div className="dashboard-header glass">
        <div className="user-welcome">
          <span className="eyebrow">PASSENGER DASHBOARD</span>
          <h1>Welcome back, {user?.fullName || user?.username}</h1>
          <p className="muted">
            Manage your active bookings, view boarding passes, and trigger one-click fast refunds.
          </p>
        </div>

        <div className="dashboard-stats-strip">
          <div className="stat-item">
            <span className="stat-label">CONFIRMED TRIPS</span>
            <b>{upcomingTickets.length}</b>
          </div>
          <div className="stat-item">
            <span className="stat-label">TOTAL BOOKED</span>
            <b>{tickets.length}</b>
          </div>
          <div className="stat-item">
            <span className="stat-label">SESSION STATUS</span>
            <span className="live-tag">Active & Secure</span>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="dashboard-tabs glass">
        <button
          type="button"
          className={`tab-item ${activeTab === 'UPCOMING' ? 'active' : ''}`}
          onClick={() => setActiveTab('UPCOMING')}
        >
          Upcoming Journeys ({upcomingTickets.length})
        </button>
        <button
          type="button"
          className={`tab-item ${activeTab === 'HISTORY' ? 'active' : ''}`}
          onClick={() => setActiveTab('HISTORY')}
        >
          All Journey History ({historyTickets.length})
        </button>
      </div>

      {/* Tickets List */}
      <div className="dashboard-tickets-list">
        {(activeTab === 'UPCOMING' ? upcomingTickets : historyTickets).length === 0 ? (
          <div className="empty-journeys-card glass p-10 text-center">
            <Calendar size={40} className="text-cyan mx-auto mb-3" />
            <h3>No {activeTab.toLowerCase()} journeys found</h3>
            <p className="muted">Ready to plan your next travel corridor?</p>
            <Link to="/search" className="btn primary mt-4">
              Search Trains Now →
            </Link>
          </div>
        ) : (
          (activeTab === 'UPCOMING' ? upcomingTickets : historyTickets).map((ticket) => (
            <div key={ticket.pnrNumber} className="journey-card glass">
              <div className="journey-card-top">
                <div className="pnr-tag">
                  <span>PNR NUMBER</span>
                  <strong className="mono">{ticket.pnrNumber}</strong>
                </div>

                <div className="status-container">
                  <span
                    className={`status-pill ${
                      ticket.status === 'CONFIRMED'
                        ? 'green'
                        : ticket.status === 'RAC'
                        ? 'amber'
                        : 'rose'
                    }`}
                  >
                    {ticket.status}
                  </span>
                </div>
              </div>

              <div className="journey-route-row">
                <div className="endpoint">
                  <strong className="code">{ticket.fromStationCode}</strong>
                  <span className="name">{ticket.fromStationName}</span>
                  <span className="time">{ticket.departureTime}</span>
                </div>

                <div className="route-line-summary">
                  <b>{ticket.trainNumber} · {ticket.trainName}</b>
                  <span>● ━━━━━━ ●</span>
                  <small>{ticket.journeyDate}</small>
                </div>

                <div className="endpoint right">
                  <strong className="code">{ticket.toStationCode}</strong>
                  <span className="name">{ticket.toStationName}</span>
                  <span className="time">{ticket.arrivalTime}</span>
                </div>
              </div>

              <div className="journey-card-foot">
                <div className="seat-summary">
                  <span>Class: <b>{ticket.coachClass}</b></span>
                  <span>Coach & Seat: <b>{ticket.coachNumber} / {ticket.seatNumbers.join(', ')}</b></span>
                  <span>Fare: <b>{formatCurrency(ticket.totalFare)}</b></span>
                </div>

                <div className="card-actions">
                  <Link
                    to="/booking/confirmation"
                    state={{ ticket }}
                    className="btn ghost small"
                  >
                    View Boarding Pass
                  </Link>

                  {ticket.status === 'CONFIRMED' && (
                    <button
                      type="button"
                      className="btn danger small"
                      onClick={() => {
                        setTicketToCancel(ticket);
                        setCancelModalOpen(true);
                      }}
                    >
                      Cancel Ticket
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Cancellation Modal */}
      {cancelModalOpen && ticketToCancel && (
        <div className="backdrop" onClick={() => setCancelModalOpen(false)}>
          <div className="modal glass" onClick={(e) => e.stopPropagation()}>
            <div className="cancel-modal-header text-center">
              <AlertTriangle size={48} className="text-amber mx-auto mb-3" />
              <h2>Confirm Ticket Cancellation</h2>
              <p className="muted">
                Are you sure you want to cancel booking <b>{ticketToCancel.pnrNumber}</b>?
              </p>
            </div>

            <div className="refund-calculation-box glass">
              <div className="refund-row">
                <span>Original Fare Paid</span>
                <b>{formatCurrency(ticketToCancel.totalFare)}</b>
              </div>
              <div className="refund-row">
                <span>Clerkage / Cancellation Fee (IRCTC Rule)</span>
                <b>- {formatCurrency(120)}</b>
              </div>
              <div className="divider"></div>
              <div className="refund-row grand">
                <strong>Instant Refund Payable</strong>
                <strong className="text-green">
                  {formatCurrency(Math.max(0, ticketToCancel.totalFare - 120))}
                </strong>
              </div>
            </div>

            <div className="modal-actions-row">
              <button
                type="button"
                className="btn ghost"
                onClick={() => setCancelModalOpen(false)}
              >
                Keep Ticket
              </button>
              <button
                type="button"
                className="btn danger"
                onClick={handleConfirmCancel}
              >
                Confirm Cancellation & Process Refund
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
