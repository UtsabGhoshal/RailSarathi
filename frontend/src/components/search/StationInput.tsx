import React, { useState, useEffect, useRef } from 'react';
import { MapPin, Loader2 } from 'lucide-react';
import { StationDto } from '../../types/station.types';
import { stationApi } from '../../api/stationApi';

interface StationInputProps {
  label: string;
  selectedCode: string;
  onSelect: (code: string, station?: StationDto) => void;
  placeholder?: string;
}

export const StationInput: React.FC<StationInputProps> = ({
  label,
  selectedCode,
  onSelect,
  placeholder = 'Station or city...',
}) => {
  const [searchTerm, setSearchTerm] = useState(selectedCode);
  const [suggestions, setSuggestions] = useState<StationDto[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);

  // Sync internal state when prop changes
  useEffect(() => {
    setSearchTerm(selectedCode);
  }, [selectedCode]);

  // Debounced API station search
  useEffect(() => {
    if (!isOpen) return;

    const timer = setTimeout(async () => {
      setIsLoading(true);
      try {
        const results = await stationApi.searchStations(searchTerm);
        setSuggestions(results);
      } catch (err) {
        console.error('Station search error:', err);
      } finally {
        setIsLoading(false);
      }
    }, 200);

    return () => clearTimeout(timer);
  }, [searchTerm, isOpen]);

  // Close suggestions when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value.toUpperCase();
    setSearchTerm(val);
    setIsOpen(true);
  };

  const handleSelectStation = (station: StationDto) => {
    setSearchTerm(station.stationCode);
    onSelect(station.stationCode, station);
    setIsOpen(false);
  };

  return (
    <div className="station-input-wrapper" ref={wrapperRef}>
      <label className="station-label">
        <span>{label}</span>
        <div className="station-input-box">
          <MapPin size={17} className="station-icon" />
          <input
            type="text"
            className="station-input"
            value={searchTerm}
            placeholder={placeholder}
            onFocus={() => setIsOpen(true)}
            onChange={handleInputChange}
            autoComplete="off"
          />
          {isLoading && <Loader2 size={15} className="station-loading animate-spin" />}
        </div>
      </label>

      {isOpen && suggestions.length > 0 && (
        <div className="station-suggestions glass">
          {suggestions.map((station) => (
            <button
              key={station.stationCode}
              type="button"
              className="suggestion-item"
              onClick={() => handleSelectStation(station)}
            >
              <span className="station-code-badge">{station.stationCode}</span>
              <div className="station-details">
                <span className="station-name">{station.stationName}</span>
                <span className="station-subtext">
                  {station.city}, {station.state} {station.zone ? `· ${station.zone}` : ''}
                </span>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};
