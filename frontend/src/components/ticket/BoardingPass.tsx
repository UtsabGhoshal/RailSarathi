import React from 'react';
import { TrainFront, Printer, Download, QrCode, CheckCircle2, Shield } from 'lucide-react';
import { TicketConfirmation } from '../../types/booking.types';

interface BoardingPassProps {
  ticket: TicketConfirmation;
}

export const BoardingPass: React.FC<BoardingPassProps> = ({ ticket }) => {
  const formatCurrency = (amount: number) => `₹${amount.toLocaleString('en-IN')}`;

  const handlePrint = () => {
    window.print();
  };

  const handleDownload = () => {
    alert(`Downloading Official RailSarathi E-Ticket for PNR: ${ticket.pnrNumber}`);
  };

  return (
    <div className="boarding-pass-wrapper">
      <div className="boarding-pass-card glass" id="printable-ticket">
        {/* Top Header */}
        <div className="ticket-header">
          <div className="brand">
            <i>
              <TrainFront size={20} />
            </i>
            Rail<span>Sarathi</span>
          </div>
          <div className="ticket-status-badge">
            <CheckCircle2 size={16} />
            <span>{ticket.status}</span>
          </div>
        </div>

        {/* PNR Banner */}
        <div className="pnr-banner">
          <div>
            <span className="label">PNR NUMBER</span>
            <h2 className="pnr-text">{ticket.pnrNumber}</h2>
          </div>
          <div>
            <span className="label">BOOKING ID</span>
            <strong className="booking-id-text">{ticket.bookingId}</strong>
          </div>
        </div>

        {/* Route Details */}
        <div className="ticket-route-grid">
          <div className="station-endpoint">
            <span className="label">FROM</span>
            <strong className="code">{ticket.fromStationCode}</strong>
            <span className="name">{ticket.fromStationName}</span>
            <span className="time">{ticket.departureTime}</span>
          </div>

          <div className="route-connector">
            <span>● ━━━━━━ ●</span>
            <small>{ticket.journeyDate}</small>
          </div>

          <div className="station-endpoint right">
            <span className="label">TO</span>
            <strong className="code">{ticket.toStationCode}</strong>
            <span className="name">{ticket.toStationName}</span>
            <span className="time">{ticket.arrivalTime}</span>
          </div>
        </div>

        {/* Info Matrix */}
        <div className="ticket-info-matrix">
          <div className="info-cell">
            <span className="label">TRAIN</span>
            <b>{ticket.trainNumber} · {ticket.trainName}</b>
          </div>

          <div className="info-cell">
            <span className="label">CLASS / QUOTA</span>
            <b>{ticket.coachClass} (General)</b>
          </div>

          <div className="info-cell">
            <span className="label">COACH & SEAT(S)</span>
            <b>
              {ticket.coachNumber} / {ticket.seatNumbers.join(', ')}
            </b>
          </div>

          <div className="info-cell">
            <span className="label">TOTAL FARE PAID</span>
            <b>{formatCurrency(ticket.totalFare)}</b>
          </div>
        </div>

        {/* Passengers Table */}
        <div className="ticket-passengers-section">
          <h4>PASSENGERS ({ticket.passengers.length})</h4>
          <div className="passengers-table">
            {ticket.passengers.map((p, i) => (
              <div key={i} className="passenger-row">
                <span className="num">#{i + 1}</span>
                <strong className="p-name">{p.fullName}</strong>
                <span className="p-age">{p.age} yrs · {p.gender}</span>
                <span className="p-berth">Berth: {ticket.seatNumbers[i] || 'Allocated'} ({p.berthPreference})</span>
              </div>
            ))}
          </div>
        </div>

        {/* Security & QR Verification */}
        <div className="ticket-footer">
          <div className="security-note">
            <Shield size={16} />
            <p>
              This is a digitally verified e-ticket. Carry a valid Government Photo ID during journey.
            </p>
          </div>

          <div className="qr-box">
            <QrCode size={56} />
            <small>Scan for TTE Check</small>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="ticket-actions no-print">
        <button type="button" className="btn ghost" onClick={handlePrint}>
          <Printer size={16} /> Print Ticket
        </button>
        <button type="button" className="btn primary" onClick={handleDownload}>
          <Download size={16} /> Download PDF
        </button>
      </div>
    </div>
  );
};
