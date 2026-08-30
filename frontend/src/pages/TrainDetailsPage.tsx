import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { ArrowLeft, Clock, MapPin, Sparkles, Shield, ArrowRight } from 'lucide-react';
import { TrainSearchResultDto, TrainScheduleDto } from '../types/train.types';
import { trainApi } from '../api/trainApi';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { LiveStatusBadge } from '../components/trains/LiveStatusBadge';
import { ScheduleTable } from '../components/trains/ScheduleTable';

export const TrainDetailsPage: React.FC = () => {
  const { trainNumber = '22301' } = useParams<{ trainNumber: string }>();
  const navigate = useNavigate();

  const [train, setTrain] = useState<TrainSearchResultDto | null>(null);
  const [schedule, setSchedule] = useState<TrainScheduleDto[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTrainData = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const [trainData, scheduleData] = await Promise.all([
          trainApi.getTrainDetails(trainNumber),
          trainApi.getTrainSchedule(trainNumber),
        ]);
        setTrain(trainData);
        setSchedule(scheduleData);
      } catch (err: unknown) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError('Failed to load train details.');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchTrainData();
  }, [trainNumber]);

  if (isLoading) {
    return (
      <div className="page-loading-wrapper glass p-12">
        <LoadingSpinner size={36} message={`Loading official schedule and TinyFish AI tracking for ${trainNumber}...`} />
      </div>
    );
  }

  if (error || !train) {
    return (
      <div className="page-error-wrapper glass p-8">
        <h2>Train Details Unavailable</h2>
        <p className="muted">{error || 'Train not found in schedule database.'}</p>
        <Link to="/search" className="btn primary mt-4">
          ← Back to Search
        </Link>
      </div>
    );
  }

  const formatCurrency = (amount: number) => `₹${amount.toLocaleString('en-IN')}`;

  return (
    <div className="train-details-page">
      <Link to="/search" className="back-link">
        <ArrowLeft size={16} /> Back to Search Results
      </Link>

      {/* Train Header Banner */}
      <div className="train-overview-header glass">
        <div className="header-meta">
          <span className="train-number-badge">{train.trainNumber}</span>
          <h1 className="train-title">{train.trainName}</h1>
          <span className="train-type-pill">{train.trainType.replace('_', ' ')}</span>
        </div>

        <div className="route-endpoints-strip">
          <div className="endpoint">
            <span className="label">ORIGIN</span>
            <b>{train.originStation.stationName} ({train.originStation.stationCode})</b>
            <span className="time">{train.departureTime}</span>
          </div>

          <div className="connector">
            <span>● ━━━━ {train.durationFormatted} ━━━━ ●</span>
            <small>{train.travelDistanceKm} Total Kilometers</small>
          </div>

          <div className="endpoint right">
            <span className="label">TERMINUS</span>
            <b>{train.terminusStation.stationName} ({train.terminusStation.stationCode})</b>
            <span className="time">{train.arrivalTime}</span>
          </div>
        </div>
      </div>

      {/* Live TinyFish AI Satellite Tracking Section */}
      <section className="live-tracking-section">
        <h3 className="section-title">
          <Sparkles size={18} className="text-cyan" />
          <span>Real-Time Web Verified Status</span>
        </h3>
        <LiveStatusBadge trainNumber={train.trainNumber} />
      </section>

      {/* Main Content 2-Column Grid */}
      <div className="train-details-grid">
        {/* Left Column: Full Intermediate Timetable */}
        <section className="timetable-section">
          <h3 className="section-title">
            <Clock size={18} />
            <span>Intermediate Route Timetable & Platforms</span>
          </h3>
          <ScheduleTable schedules={schedule} />
        </section>

        {/* Right Column: Classes, Fares & Booking */}
        <aside className="classes-fares-sidebar">
          <div className="fare-matrix-panel glass">
            <h3 className="panel-title">Available Travel Classes</h3>
            <div className="fares-list">
              {train.availableClasses.map((cls) => (
                <div key={cls.classCode} className="class-fare-row">
                  <div className="class-info">
                    <strong className="class-code">{cls.classCode}</strong>
                    <span className="class-name">{cls.className}</span>
                    <span className="seat-status">{cls.statusDescription}</span>
                  </div>

                  <div className="fare-action">
                    <b className="fare-amount">{formatCurrency(cls.fare)}</b>
                    <button
                      type="button"
                      className="btn primary small"
                      onClick={() =>
                        navigate('/booking/initiate', {
                          state: {
                            train,
                            selectedClass: cls.coachClass,
                            fare: cls.fare,
                            className: cls.className,
                            classCode: cls.classCode,
                            journeyDate: new Date().toISOString().split('T')[0],
                          },
                        })
                      }
                    >
                      Book <ArrowRight size={14} />
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="panel-security-note">
              <Shield size={14} />
              <span>Instant refund calculation & atomic seat hold upon checkout</span>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
};
