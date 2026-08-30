import React from 'react';
import { ShieldCheck, ArrowRight } from 'lucide-react';
import { TrainSearchResultDto, CoachClass } from '../../types/train.types';

interface FareSummaryProps {
  train: TrainSearchResultDto;
  selectedClass: CoachClass;
  farePerPassenger: number;
  passengerCount: number;
  onProceed: () => void;
  isProcessing?: boolean;
}

export const FareSummary: React.FC<FareSummaryProps> = ({
  train,
  selectedClass,
  farePerPassenger,
  passengerCount,
  onProceed,
  isProcessing = false,
}) => {
  const formatCurrency = (amount: number) => `₹${amount.toLocaleString('en-IN')}`;

  const baseFareTotal = farePerPassenger * passengerCount;
  const convenienceFee = 40;
  const gst = Math.round(baseFareTotal * 0.05);
  const totalPayable = baseFareTotal + convenienceFee + gst;

  return (
    <aside className="fare-summary-panel glass">
      <span className="eyebrow">PAYMENT SUMMARY</span>
      <h2 className="total-fare-highlight">{formatCurrency(totalPayable)}</h2>

      <div className="fare-breakdown">
        <div className="fare-row">
          <span>
            Base Fare ({passengerCount} × {formatCurrency(farePerPassenger)})
          </span>
          <b>{formatCurrency(baseFareTotal)}</b>
        </div>

        <div className="fare-row">
          <span>IRCTC Convenience Fee</span>
          <b>{formatCurrency(convenienceFee)}</b>
        </div>

        <div className="fare-row">
          <span>GST (5%)</span>
          <b>{formatCurrency(gst)}</b>
        </div>

        <div className="divider"></div>

        <div className="fare-row grand-total">
          <strong>Total Amount</strong>
          <strong className="grand-total-amount">{formatCurrency(totalPayable)}</strong>
        </div>
      </div>

      <div className="booking-details-snippet">
        <div>
          <span className="label">TRAIN</span>
          <b>{train.trainNumber} · {train.trainName}</b>
        </div>
        <div>
          <span className="label">ROUTE</span>
          <b>{train.boardingStation.stationCode} → {train.destinationStation.stationCode}</b>
        </div>
      </div>

      <button
        type="button"
        className="btn primary wide proceed-pay-btn"
        onClick={onProceed}
        disabled={isProcessing}
      >
        <span>{isProcessing ? 'Issuing Ticket...' : 'Proceed to Payment →'}</span>
      </button>

      <div className="secure-badge">
        <ShieldCheck size={14} />
        <span>100% Safe & Instant Refund Guarantee</span>
      </div>
    </aside>
  );
};
