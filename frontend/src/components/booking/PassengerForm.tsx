import React from 'react';
import { User, Plus, Trash2, Check } from 'lucide-react';
import { Passenger, BerthPreference } from '../../types/booking.types';

interface PassengerFormProps {
  passengers: Passenger[];
  onChange: (passengers: Passenger[]) => void;
  contactEmail: string;
  onEmailChange: (email: string) => void;
  contactPhone: string;
  onPhoneChange: (phone: string) => void;
}

const BERTH_OPTIONS: { label: string; value: BerthPreference }[] = [
  { label: 'Lower Berth', value: 'LOWER' },
  { label: 'Middle Berth', value: 'MIDDLE' },
  { label: 'Upper Berth', value: 'UPPER' },
  { label: 'Side Lower', value: 'SIDE_LOWER' },
  { label: 'Side Upper', value: 'SIDE_UPPER' },
  { label: 'Window Seat', value: 'WINDOW' },
  { label: 'No Preference', value: 'NO_PREFERENCE' },
];

export const PassengerForm: React.FC<PassengerFormProps> = ({
  passengers,
  onChange,
  contactEmail,
  onEmailChange,
  contactPhone,
  onPhoneChange,
}) => {
  const handlePassengerChange = (index: number, field: keyof Passenger, value: unknown) => {
    const updated = [...passengers];
    updated[index] = {
      ...updated[index],
      [field]: value,
    };
    onChange(updated);
  };

  const addPassenger = () => {
    if (passengers.length >= 6) return;
    onChange([
      ...passengers,
      {
        fullName: '',
        age: 28,
        gender: 'MALE',
        berthPreference: 'NO_PREFERENCE',
      },
    ]);
  };

  const removePassenger = (index: number) => {
    if (passengers.length <= 1) return;
    const updated = passengers.filter((_, i) => i !== index);
    onChange(updated);
  };

  return (
    <div className="passenger-form-section">
      <div className="section-title-row">
        <h3>Passenger Details</h3>
        <button
          type="button"
          className="btn ghost add-passenger-btn"
          onClick={addPassenger}
          disabled={passengers.length >= 6}
        >
          <Plus size={16} /> Add Passenger ({passengers.length}/6)
        </button>
      </div>

      <div className="passengers-list">
        {passengers.map((passenger, idx) => (
          <div key={idx} className="passenger-card glass">
            <div className="passenger-header">
              <div className="passenger-num">
                <User size={16} />
                <span>Passenger {idx + 1}</span>
              </div>
              {passengers.length > 1 && (
                <button
                  type="button"
                  className="remove-passenger-btn"
                  onClick={() => removePassenger(idx)}
                >
                  <Trash2 size={15} /> Remove
                </button>
              )}
            </div>

            <div className="form-fields-grid">
              <label>
                <span>Full Name (as per Govt ID)</span>
                <input
                  type="text"
                  placeholder="e.g. Aarav Mehta"
                  value={passenger.fullName}
                  onChange={(e) => handlePassengerChange(idx, 'fullName', e.target.value)}
                  required
                />
              </label>

              <label>
                <span>Age</span>
                <input
                  type="number"
                  min={1}
                  max={120}
                  value={passenger.age}
                  onChange={(e) => handlePassengerChange(idx, 'age', parseInt(e.target.value) || 18)}
                  required
                />
              </label>

              <label>
                <span>Gender</span>
                <select
                  value={passenger.gender}
                  onChange={(e) => handlePassengerChange(idx, 'gender', e.target.value)}
                >
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </label>
            </div>

            <div className="berth-preference-block">
              <span className="berth-label">Berth / Seat Preference</span>
              <div className="berth-chips">
                {BERTH_OPTIONS.map((opt) => {
                  const isSelected = passenger.berthPreference === opt.value;
                  return (
                    <button
                      key={opt.value}
                      type="button"
                      className={`berth-chip ${isSelected ? 'active' : ''}`}
                      onClick={() => handlePassengerChange(idx, 'berthPreference', opt.value)}
                    >
                      {isSelected && <Check size={12} />}
                      <span>{opt.label}</span>
                    </button>
                  );
                })}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="contact-details-card glass">
        <h4>Contact Information for Ticket & SMS</h4>
        <div className="contact-grid">
          <label>
            <span>Email Address</span>
            <input
              type="email"
              placeholder="e.g. traveller@railsarathi.com"
              value={contactEmail}
              onChange={(e) => onEmailChange(e.target.value)}
              required
            />
          </label>

          <label>
            <span>Mobile Phone (for PNR updates)</span>
            <input
              type="tel"
              placeholder="+91 98765 43210"
              value={contactPhone}
              onChange={(e) => onPhoneChange(e.target.value)}
              required
            />
          </label>
        </div>
      </div>
    </div>
  );
};
