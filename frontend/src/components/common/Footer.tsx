import React from 'react';
import { TrainFront, Shield, Heart } from 'lucide-react';
import { Link } from 'react-router-dom';

export const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="footer-brand">
          <div className="brand">
            <i>
              <TrainFront size={18} />
            </i>
            Rail<span>Sarathi</span>
          </div>
          <p className="footer-tagline">
            Next-generation Indian railway journey orchestration, live web status, and intelligent seat booking.
          </p>
        </div>

        <div className="footer-links">
          <div className="link-group">
            <h4>EXPLORE</h4>
            <Link to="/search">Search Trains</Link>
            <Link to="/train/22301">Vande Bharat Express</Link>
            <Link to="/train/12301">Rajdhani Express</Link>
          </div>

          <div className="link-group">
            <h4>ACCOUNT</h4>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/dashboard">Booking Archive</Link>
            <Link to="/admin">Operations</Link>
          </div>

          <div className="link-group">
            <h4>SECURITY</h4>
            <span><Shield size={14} /> 256-Bit JWT Encryption</span>
            <span>Session Isolation Protected</span>
            <span>TinyFish AI Verified</span>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <p>© 2026 RailSarathi System · Crafted with precision for Indian Railways travellers.</p>
        <span className="live-indicator">
          <span className="pulse-dot"></span> All Systems Operational
        </span>
      </div>
    </footer>
  );
};
