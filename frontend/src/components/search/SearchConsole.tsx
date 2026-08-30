import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeftRight, CalendarDays, Search, Sparkles, Filter } from 'lucide-react';
import { StationInput } from './StationInput';

interface SearchConsoleProps {
  initialSource?: string;
  initialDestination?: string;
  initialDate?: string;
  onSearch?: (source: string, destination: string, date: string) => void;
  compact?: boolean;
}

export const SearchConsole: React.FC<SearchConsoleProps> = ({
  initialSource = 'HWH',
  initialDestination = 'NDLS',
  initialDate,
  onSearch,
  compact = false,
}) => {
  const navigate = useNavigate();

  // Get tomorrow's date formatted as YYYY-MM-DD by default
  const getDefaultDate = () => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  };

  const [source, setSource] = useState(initialSource);
  const [destination, setDestination] = useState(initialDestination);
  const [journeyDate, setJourneyDate] = useState(initialDate || getDefaultDate());
  const [quota, setQuota] = useState('GENERAL');
  const [journeyType, setJourneyType] = useState<'ONE_WAY' | 'ROUND_TRIP'>('ONE_WAY');

  const handleSwap = () => {
    setSource(destination);
    setDestination(source);
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!source || !destination) return;

    if (onSearch) {
      onSearch(source, destination, journeyDate);
    } else {
      const params = new URLSearchParams({
        source,
        destination,
        date: journeyDate,
      });
      navigate(`/search?${params.toString()}`);
    }
  };

  return (
    <form onSubmit={handleSearchSubmit} className={`search-console glass ${compact ? 'compact' : ''}`}>
      {!compact && (
        <div className="console-top-bar">
          <div className="journey-pills">
            <button
              type="button"
              className={`pill-btn ${journeyType === 'ONE_WAY' ? 'active' : ''}`}
              onClick={() => setJourneyType('ONE_WAY')}
            >
              One Way
            </button>
            <button
              type="button"
              className={`pill-btn ${journeyType === 'ROUND_TRIP' ? 'active' : ''}`}
              onClick={() => setJourneyType('ROUND_TRIP')}
            >
              Round Trip
            </button>
          </div>

          <div className="quota-select-wrapper">
            <select
              value={quota}
              onChange={(e) => setQuota(e.target.value)}
              className="quota-select"
            >
              <option value="GENERAL">General Quota</option>
              <option value="TATKAL">Tatkal Quota</option>
              <option value="LADIES">Ladies Quota</option>
              <option value="SENIOR">Senior Citizen / Divyang</option>
            </select>
          </div>
        </div>
      )}

      <div className="search-inputs-grid">
        <StationInput
          label="FROM"
          selectedCode={source}
          onSelect={(code) => setSource(code)}
          placeholder="Origin station (e.g. HWH)"
        />

        <button
          type="button"
          className="swap-stations-btn"
          title="Swap stations"
          onClick={handleSwap}
        >
          <ArrowLeftRight size={18} />
        </button>

        <StationInput
          label="TO"
          selectedCode={destination}
          onSelect={(code) => setDestination(code)}
          placeholder="Destination station (e.g. NDLS)"
        />

        <label className="date-input-label">
          <span>JOURNEY DATE</span>
          <div className="date-input-box">
            <CalendarDays size={18} className="date-icon" />
            <input
              type="date"
              className="date-input"
              value={journeyDate}
              min={new Date().toISOString().split('T')[0]}
              onChange={(e) => setJourneyDate(e.target.value)}
              required
            />
          </div>
        </label>
      </div>

      <div className="console-bottom-bar">
        <div className="class-preference">
          <Filter size={14} />
          <span>CLASS PREFERENCE: <b>All AC & Sleeper Classes</b></span>
        </div>

        <button type="submit" className="btn primary search-submit-btn">
          <Search size={18} /> Search Trains
        </button>
      </div>
    </form>
  );
};
