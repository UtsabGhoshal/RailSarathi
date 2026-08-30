import React, { useState, useEffect } from 'react';
import { RefreshCw, Radio, Sparkles, CheckCircle2, Clock } from 'lucide-react';
import { LiveTrainStatusDto } from '../../types/train.types';
import { trainApi } from '../../api/trainApi';

interface LiveStatusBadgeProps {
  trainNumber: string;
  initialData?: LiveTrainStatusDto;
}

export const LiveStatusBadge: React.FC<LiveStatusBadgeProps> = ({
  trainNumber,
  initialData,
}) => {
  const [statusData, setStatusData] = useState<LiveTrainStatusDto | null>(initialData || null);
  const [isLoading, setIsLoading] = useState<boolean>(!initialData);
  const [isRefreshing, setIsRefreshing] = useState<boolean>(false);

  const fetchLiveStatus = async (force: boolean = false) => {
    if (force) {
      setIsRefreshing(true);
    } else {
      setIsLoading(true);
    }

    try {
      const data = await trainApi.getLiveStatus(trainNumber, force);
      setStatusData(data);
    } catch (err) {
      console.error('Failed to load live train status:', err);
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  useEffect(() => {
    if (!initialData) {
      fetchLiveStatus(false);
    }
  }, [trainNumber]);

  if (isLoading) {
    return (
      <div className="live-status-widget loading glass">
        <Radio size={15} className="animate-pulse text-cyan" />
        <span>Connecting to TinyFish AI live satellite feed...</span>
      </div>
    );
  }

  if (!statusData) return null;

  const isDelayed = statusData.delayMinutes > 0;

  return (
    <div className="live-status-widget glass">
      <div className="status-main-row">
        <div className="status-badge-indicator">
          <span className={`pulse-dot ${isDelayed ? 'amber' : 'green'}`}></span>
          <span className={`status-pill ${isDelayed ? 'delayed' : 'on-time'}`}>
            {statusData.statusMessage}
          </span>
        </div>

        <button
          type="button"
          className={`refresh-btn ${isRefreshing ? 'spinning' : ''}`}
          title="Force refresh live web tracking"
          onClick={() => fetchLiveStatus(true)}
          disabled={isRefreshing}
        >
          <RefreshCw size={14} />
          <span>{isRefreshing ? 'Refreshing...' : 'Live Sync'}</span>
        </button>
      </div>

      <div className="status-route-summary">
        <div>
          <span className="label">LAST VERIFIED STATION</span>
          <b>{statusData.currentStation}</b>
        </div>
        <div className="arrow">→</div>
        <div>
          <span className="label">APPROACHING</span>
          <b>{statusData.nextStation}</b>
        </div>
      </div>

      <div className="status-meta">
        <span className="source-tag">
          <Sparkles size={12} /> {statusData.dataSource}
        </span>
        <span className="time-tag">
          <Clock size={12} /> Expected: {statusData.expectedArrival}
        </span>
      </div>
    </div>
  );
};
