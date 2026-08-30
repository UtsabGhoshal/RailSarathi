import React from 'react';
import { TrainScheduleDto } from '../../types/train.types';
import { Clock, MapPin } from 'lucide-react';

interface ScheduleTableProps {
  schedules: TrainScheduleDto[];
}

export const ScheduleTable: React.FC<ScheduleTableProps> = ({ schedules }) => {
  if (!schedules || schedules.length === 0) {
    return <div className="muted p-4">No schedule details available.</div>;
  }

  return (
    <div className="schedule-table-container glass">
      <table className="schedule-table">
        <thead>
          <tr>
            <th>#</th>
            <th>STATION</th>
            <th>ARRIVES</th>
            <th>DEPARTS</th>
            <th>HALT</th>
            <th>DISTANCE</th>
            <th>DAY</th>
            <th>PLATFORM</th>
          </tr>
        </thead>
        <tbody>
          {schedules.map((stop) => (
            <tr key={stop.stopOrder}>
              <td className="stop-index">{stop.stopOrder}</td>
              <td className="station-cell">
                <strong>{stop.stationName}</strong>
                <span className="station-code-sub">{stop.stationCode} · {stop.city}</span>
              </td>
              <td className="time-cell">{stop.arrivalTime || 'Origin'}</td>
              <td className="time-cell">{stop.departureTime || 'Terminus'}</td>
              <td className="halt-cell">
                {stop.haltMinutes > 0 ? `${stop.haltMinutes} mins` : '—'}
              </td>
              <td>{stop.distanceFromSourceKm} km</td>
              <td>Day {stop.dayNumber}</td>
              <td className="platform-cell">{stop.platform || 'PF 1'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
