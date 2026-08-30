import React from 'react';
import { Loader2 } from 'lucide-react';

interface LoadingSpinnerProps {
  message?: string;
  size?: number;
  inline?: boolean;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  message = 'Loading live data...',
  size = 24,
  inline = false,
}) => {
  if (inline) {
    return (
      <span className="inline-spinner">
        <Loader2 className="animate-spin" size={size} />
        {message && <span>{message}</span>}
      </span>
    );
  }

  return (
    <div className="spinner-container">
      <Loader2 className="animate-spin text-cyan" size={size} />
      {message && <p className="spinner-message">{message}</p>}
    </div>
  );
};
