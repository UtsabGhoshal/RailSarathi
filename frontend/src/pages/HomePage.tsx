import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, Shield, Clock, Zap, ArrowRight, Compass } from 'lucide-react';
import { SearchConsole } from '../components/search/SearchConsole';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();

  const handleQuickRoute = (source: string, destination: string) => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateStr = tomorrow.toISOString().split('T')[0];
    navigate(`/search?source=${source}&destination=${destination}&date=${dateStr}`);
  };

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-left-content">
          <div className="hero-badge">
            <Sparkles size={14} className="text-cyan" />
            <span>THE NEXT GENERATION OF RAILWAY TRAVEL</span>
          </div>

          <h1 className="hero-headline">
            Your journey,<br />
            <i>elevated.</i>
          </h1>

          <p className="hero-description">
            Search authentic Indian Railways schedules, track real-time train status powered by TinyFish AI, and experience frictionless seat reservations with sub-second booking speeds.
          </p>

          <div className="hero-stats-row">
            <div className="stat-card">
              <b>16+</b>
              <small>Major Railway Hubs</small>
            </div>
            <div className="stat-card">
              <b>99.8%</b>
              <small>Live Track Accuracy</small>
            </div>
            <div className="stat-card">
              <b>&lt; 1ms</b>
              <small>Cache Response Time</small>
            </div>
          </div>
        </div>

        <div className="hero-right-console">
          <SearchConsole />
        </div>
      </section>

      {/* Featured Routes Section */}
      <section className="featured-routes-section">
        <div className="section-header">
          <div>
            <span className="eyebrow">EXPLORE POPULAR ROUTES</span>
            <h2>Flagship Corridors</h2>
          </div>
          <button
            type="button"
            className="btn-text-link"
            onClick={() => handleQuickRoute('HWH', 'NJP')}
          >
            Explore all trains →
          </button>
        </div>

        <div className="routes-cards-grid">
          <button
            type="button"
            className="route-card glass"
            onClick={() => handleQuickRoute('HWH', 'NJP')}
          >
            <div className="route-endpoints">
              <div>
                <strong>HWH</strong>
                <span>Howrah</span>
              </div>
              <div className="route-arrow">━━━━ ◉ ━━━━</div>
              <div>
                <strong>NJP</strong>
                <span>New Jalpaiguri</span>
              </div>
            </div>
            <div className="route-meta">
              <span className="train-tag">22301 Vande Bharat</span>
              <span className="duration-tag">07h 30m</span>
            </div>
          </button>

          <button
            type="button"
            className="route-card glass"
            onClick={() => handleQuickRoute('HWH', 'NDLS')}
          >
            <div className="route-endpoints">
              <div>
                <strong>HWH</strong>
                <span>Howrah</span>
              </div>
              <div className="route-arrow">━━━━ ◉ ━━━━</div>
              <div>
                <strong>NDLS</strong>
                <span>New Delhi</span>
              </div>
            </div>
            <div className="route-meta">
              <span className="train-tag">12301 Rajdhani Express</span>
              <span className="duration-tag">17h 15m</span>
            </div>
          </button>

          <button
            type="button"
            className="route-card glass"
            onClick={() => handleQuickRoute('CSMT', 'NDLS')}
          >
            <div className="route-endpoints">
              <div>
                <strong>CSMT</strong>
                <span>Mumbai Central</span>
              </div>
              <div className="route-arrow">━━━━ ◉ ━━━━</div>
              <div>
                <strong>NDLS</strong>
                <span>New Delhi</span>
              </div>
            </div>
            <div className="route-meta">
              <span className="train-tag">12951 Tejas Rajdhani</span>
              <span className="duration-tag">15h 32m</span>
            </div>
          </button>
        </div>
      </section>

      {/* Value Pillars Section */}
      <section className="features-grid-section">
        <div className="feature-box glass">
          <div className="feature-icon-wrapper">
            <Zap size={24} className="text-cyan" />
          </div>
          <h3>Multi-Stop Route Engine</h3>
          <p className="muted">
            Search from any source to any intermediate junction across India with dynamic distance-based pricing and class availability.
          </p>
        </div>

        <div className="feature-box glass">
          <div className="feature-icon-wrapper">
            <Sparkles size={24} className="text-cyan" />
          </div>
          <h3>TinyFish AI Live Tracker</h3>
          <p className="muted">
            Real-time web verification and live delay monitoring backed by intelligent 3-minute caching for instantaneous updates.
          </p>
        </div>

        <div className="feature-box glass">
          <div className="feature-icon-wrapper">
            <Shield size={24} className="text-cyan" />
          </div>
          <h3>Isolated Tab Sessions</h3>
          <p className="muted">
            Independent session UUID authentication protects against multi-account collisions on shared devices and browser tabs.
          </p>
        </div>
      </section>
    </div>
  );
};
