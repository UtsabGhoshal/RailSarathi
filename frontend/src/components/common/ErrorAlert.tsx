import React from 'react';
import { AlertCircle, X } from 'lucide-react';

interface ErrorAlertProps {
  message: string;
  onClose?: () => void;
}

export const ErrorAlert: React.FC<ErrorAlertProps> = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div className="error-banner">
      <AlertCircle size={18} className="error-icon" />
      <span className="error-text">{message}</span>
      {onClose && (
        <button type="button" className="error-close" onClick={onClose}>
          <X size={16} />
        </button>
      )}
    </div>
  );
};
