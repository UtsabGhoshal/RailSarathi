import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronDown, ChevronUp, Clock, MapPin, ArrowRight, ShieldCheck } from 'lucide-react';
import { TrainSearchResultDto, ClassAvailabilityDto } from '../../types/train.types';

interface TrainCardProps {
  train: TrainSearchResultDto;
  journeyDate?: string;
}

export const TrainCard: React.FC<TrainCardProps> = ({ train, journeyDate }) => {
  const navigate = useNavigate();
  const [selectedClassIndex, setSelectedClassIndex] = useState<number>(0);
  const [showSchedule, setShowSchedule] = useState<boolean>(false);

  const formatCurrency = (amount: number) => `₹${amount.toLocaleString('en-IN')}`;

  const selectedClass: ClassAvailabilityDto | undefined =
    train.availableClasses && train.availableClasses.length > 0
      ? train.availableClasses[selectedClassIndex]
      : undefined;

  const handleBookNow = () => {
    if (!selectedClass) return;

    // Navigate to booking page with state
    navigate('/booking/initiate', {
      state: {
        train,
        selectedClass: selectedClass.coachClass,
        fare: selectedClass.fare,
        className: selectedClass.className,
        classCode: selectedClass.classCode,
        journeyDate: journeyDate || new Date().toISOString().split('T')[0],
      },
    });
  };

  const getStatusClass = (statusCode: string) => {
    if (statusCode === 'AVL') return 'status-avl';
    if (statusCode === 'RAC') return 'status-rac';
    return 'status-wl';
  };

  return (
    <article className="train-card glass">
      {/* Top Header */}
      <div className="train-card-header">
        <div className="train-identity">
          <span className="train-number-badge">{train.trainNumber}</span>
          <h3 className="train-title">{train.trainName}</h3>
          <span className="train-type-pill">{train.trainType.replace('_', ' ')}</span>
        </div>

        <div className="train-header-actions">
          <button
            type="button"
            className="btn-text-link"
            onClick={() => navigate(`/train/${train.trainNumber}`)}
          >
            Live Schedule & Track →
          </button>
        </div>
      </div>

      {/* Route & Timings Timeline */}
      <div className="train-timeline-grid">
        <div className="departure-block">
          <span className="time">{train.departureTime}</span>
          <strong className="code">{train.boardingStation?.stationCode}</strong>
          <span className="station-name">{train.boardingStation?.stationName}</span>
        </div>

        <div className="duration-line-block">
          <div className="duration-pill">
            <Clock size={13} />
            <span>{train.durationFormatted}</span>
          </div>
          <div className="visual-line">
            <span className="dot origin"></span>
            <span className="line"></span>
            <span className="dot destination"></span>
          </div>
          <span className="distance-text">{train.travelDistanceKm} km</span>
        </div>

        <div className="arrival-block">
          <span className="time">{train.arrivalTime}</span>
          <strong className="code">{train.destinationStation?.stationCode}</strong>
          <span className="station-name">{train.destinationStation?.stationName}</span>
          {train.dayDifference > 0 && (
            <span className="day-diff-badge">+{train.dayDifference} Day</span>
          )}
        </div>
      </div>

      {/* Class Selection Grid */}
      <div className="class-selection-container">
        <span className="class-label">SELECT TRAVEL CLASS:</span>
        <div className="classes-grid">
          {train.availableClasses?.map((cls, idx) => {
            const isSelected = selectedClassIndex === idx;
            return (
              <button
                key={cls.classCode}
                type="button"
                className={`class-card ${isSelected ? 'selected' : ''}`}
                onClick={() => setSelectedClassIndex(idx)}
              >
                <div className="class-top">
                  <strong className="class-code">{cls.classCode}</strong>
                  <span className="class-fare">{formatCurrency(cls.fare)}</span>
                </div>
                <div className="class-bottom">
                  <span className="class-name">{cls.className}</span>
                  <span className={`seat-status-pill ${getStatusClass(cls.statusCode)}`}>
                    {cls.statusDescription}
                  </span>
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* Card Footer Actions */}
      <div className="train-card-footer">
        <button
          type="button"
          className="toggle-route-btn"
          onClick={() => setShowSchedule(!showSchedule)}
        >
          {showSchedule ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
          <span>{showSchedule ? 'Hide intermediate stops' : 'View full route schedule'}</span>
        </button>

        <div className="booking-cta-group">
          {selectedClass && (
            <div className="selected-fare-display">
              <small>Total Base Fare</small>
              <b>{formatCurrency(selectedClass.fare)}</b>
            </div>
          )}

          <button
            type="button"
            className="btn primary book-now-btn"
            onClick={handleBookNow}
          >
            <span>Book {selectedClass?.classCode || 'Seat'}</span>
            <ArrowRight size={16} />
          </button>
        </div>
      </div>

      {/* Expandable Route Stops */}
      {showSchedule && train.routeSchedule && (
        <div className="intermediate-schedule-drawer glass">
          <h4>ROUTE SCHEDULE & STOPS</h4>
          <div className="stops-timeline">
            {train.routeSchedule.map((stop) => (
              <div key={stop.stationCode} className="stop-item">
                <span className="stop-marker"></span>
                <div className="stop-info">
                  <b>{stop.stationName} ({stop.stationCode})</b>
                  <span className="stop-time">
                    Arr: {stop.arrivalTime || 'Origin'} | Dep: {stop.departureTime || 'Terminus'}
                    {stop.haltMinutes > 0 ? ` (${stop.haltMinutes}m halt)` : ''}
                  </span>
                </div>
                <span className="stop-dist">{stop.distanceFromSourceKm} km</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </article>
  );
};
