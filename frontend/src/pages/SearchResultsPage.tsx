import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { SlidersHorizontal, ArrowLeftRight, TrainFront, Filter, CalendarDays, RefreshCw } from 'lucide-react';
import { TrainSearchResultDto, CoachClass, TrainType } from '../types/train.types';
import { trainApi } from '../api/trainApi';
import { TrainCard } from '../components/trains/TrainCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { SearchConsole } from '../components/search/SearchConsole';

export const SearchResultsPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const source = searchParams.get('source') || 'HWH';
  const destination = searchParams.get('destination') || 'NDLS';
  const date = searchParams.get('date') || new Date().toISOString().split('T')[0];

  const [trains, setTrains] = useState<TrainSearchResultDto[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showModifySearch, setShowModifySearch] = useState<boolean>(false);

  // Filters state
  const [selectedClasses, setSelectedClasses] = useState<string[]>([]);
  const [selectedTrainTypes, setSelectedTrainTypes] = useState<string[]>([]);
  const [sortBy, setSortBy] = useState<'DEPARTURE' | 'DURATION' | 'PRICE'>('DEPARTURE');

  const fetchTrains = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await trainApi.searchTrains(source, destination, date);
      setTrains(data);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Failed to search trains. Please verify station codes.');
      }
      setTrains([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchTrains();
  }, [source, destination, date]);

  // Generate 5 days strip around selected date
  const generateDateStrip = () => {
    const dates = [];
    const base = new Date(date);
    for (let i = -2; i <= 2; i++) {
      const d = new Date(base);
      d.setDate(base.getDate() + i);
      dates.push(d);
    }
    return dates;
  };

  const handleDateSelect = (selectedDate: Date) => {
    const dateStr = selectedDate.toISOString().split('T')[0];
    const newParams = new URLSearchParams(searchParams);
    newParams.set('date', dateStr);
    setSearchParams(newParams);
  };

  const toggleClassFilter = (code: string) => {
    if (selectedClasses.includes(code)) {
      setSelectedClasses(selectedClasses.filter((c) => c !== code));
    } else {
      setSelectedClasses([...selectedClasses, code]);
    }
  };

  // Filter & sort trains
  const filteredTrains = trains
    .filter((train) => {
      // Filter by classes
      if (selectedClasses.length > 0) {
        const hasClass = train.availableClasses.some((c) =>
          selectedClasses.includes(c.classCode)
        );
        if (!hasClass) return false;
      }
      // Filter by train types
      if (selectedTrainTypes.length > 0) {
        if (!selectedTrainTypes.includes(train.trainType)) return false;
      }
      return true;
    })
    .sort((a, b) => {
      if (sortBy === 'PRICE') {
        const minFareA = Math.min(...a.availableClasses.map((c) => c.fare), 99999);
        const minFareB = Math.min(...b.availableClasses.map((c) => c.fare), 99999);
        return minFareA - minFareB;
      }
      if (sortBy === 'DURATION') {
        return a.travelDistanceKm - b.travelDistanceKm;
      }
      // Default: sort by departure time
      return a.departureTime.localeCompare(b.departureTime);
    });

  return (
    <div className="search-results-page">
      {/* Top Banner Header */}
      <div className="results-header-banner">
        <div className="search-summary-text">
          <span className="eyebrow">LIVE RAIL ROUTING ENGINE</span>
          <h1>
            Trains from <span>{source}</span> to <span>{destination}</span>
          </h1>
          <p className="muted">
            {filteredTrains.length} train{filteredTrains.length === 1 ? '' : 's'} available on {date} · Verified Real Schedules
          </p>
        </div>

        <button
          type="button"
          className="btn ghost modify-search-btn"
          onClick={() => setShowModifySearch(!showModifySearch)}
        >
          <CalendarDays size={16} />
          <span>{showModifySearch ? 'Hide Search Box' : 'Change Search'}</span>
        </button>
      </div>

      {showModifySearch && (
        <div className="modify-console-wrapper mb-6">
          <SearchConsole
            initialSource={source}
            initialDestination={destination}
            initialDate={date}
            compact
            onSearch={(s, d, dt) => {
              const newParams = new URLSearchParams();
              newParams.set('source', s);
              newParams.set('destination', d);
              newParams.set('date', dt);
              setSearchParams(newParams);
              setShowModifySearch(false);
            }}
          />
        </div>
      )}

      {/* Date Strip Bar */}
      <div className="date-strip-bar glass">
        {generateDateStrip().map((d) => {
          const dateStr = d.toISOString().split('T')[0];
          const isSelected = dateStr === date;
          const dayName = d.toLocaleDateString('en-US', { weekday: 'short' }).toUpperCase();
          const monthDay = d.toLocaleDateString('en-US', { day: 'numeric', month: 'short' });

          return (
            <button
              key={dateStr}
              type="button"
              className={`date-strip-tab ${isSelected ? 'active' : ''}`}
              onClick={() => handleDateSelect(d)}
            >
              <small>{dayName}</small>
              <b>{monthDay}</b>
            </button>
          );
        })}
      </div>

      {/* Results Layout Grid */}
      <div className="results-layout-grid">
        {/* Left Filter Sidebar */}
        <aside className="filters-sidebar glass">
          <div className="sidebar-title-row">
            <span className="sidebar-title">
              <SlidersHorizontal size={16} /> Filters
            </span>
            {(selectedClasses.length > 0 || selectedTrainTypes.length > 0) && (
              <button
                type="button"
                className="btn-text-small"
                onClick={() => {
                  setSelectedClasses([]);
                  setSelectedTrainTypes([]);
                }}
              >
                Reset
              </button>
            )}
          </div>

          <div className="filter-group">
            <span className="group-label">TRAVEL CLASSES</span>
            {['1A', '2A', '3A', '3E', 'CC', 'EC', 'SL'].map((c) => (
              <label key={c} className="checkbox-label">
                <input
                  type="checkbox"
                  checked={selectedClasses.includes(c)}
                  onChange={() => toggleClassFilter(c)}
                />
                <span>{c} Class</span>
              </label>
            ))}
          </div>

          <div className="divider"></div>

          <div className="filter-group">
            <span className="group-label">TRAIN TYPES</span>
            {['VANDE_BHARAT', 'RAJDHANI', 'SHATABDI', 'DURONTO', 'SUPERFAST'].map((t) => (
              <label key={t} className="checkbox-label">
                <input
                  type="checkbox"
                  checked={selectedTrainTypes.includes(t)}
                  onChange={() => {
                    if (selectedTrainTypes.includes(t)) {
                      setSelectedTrainTypes(selectedTrainTypes.filter((x) => x !== t));
                    } else {
                      setSelectedTrainTypes([...selectedTrainTypes, t]);
                    }
                  }}
                />
                <span>{t.replace('_', ' ')}</span>
              </label>
            ))}
          </div>
        </aside>

        {/* Right Train Cards List */}
        <main className="trains-list-area">
          <div className="list-toolbar glass">
            <span className="result-count">
              Showing <b>{filteredTrains.length}</b> verified trains
            </span>

            <div className="sort-controls">
              <span>Sort by:</span>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as 'DEPARTURE' | 'DURATION' | 'PRICE')}
                className="sort-dropdown"
              >
                <option value="DEPARTURE">Departure Time</option>
                <option value="DURATION">Fastest Duration</option>
                <option value="PRICE">Lowest Fare</option>
              </select>
            </div>
          </div>

          {isLoading ? (
            <div className="loading-state glass p-10">
              <LoadingSpinner size={36} message="Searching authentic Indian Railways schedules and live seat availability..." />
            </div>
          ) : error ? (
            <div className="error-state glass p-8">
              <h3>No direct or intermediate route found</h3>
              <p className="muted">{error}</p>
              <button type="button" className="btn primary mt-4" onClick={() => fetchTrains()}>
                <RefreshCw size={16} /> Retry Search
              </button>
            </div>
          ) : filteredTrains.length === 0 ? (
            <div className="empty-state glass p-10 text-center">
              <TrainFront size={48} className="text-cyan mx-auto mb-4" />
              <h3>No trains match your selected filters</h3>
              <p className="muted">
                Try clearing active class/type filters or search another date for {source} → {destination}.
              </p>
              <button
                type="button"
                className="btn ghost mt-4"
                onClick={() => {
                  setSelectedClasses([]);
                  setSelectedTrainTypes([]);
                }}
              >
                Clear all filters
              </button>
            </div>
          ) : (
            <div className="train-cards-stack">
              {filteredTrains.map((train) => (
                <TrainCard key={train.trainNumber} train={train} journeyDate={date} />
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};
