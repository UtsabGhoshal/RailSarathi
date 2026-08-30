import React, { useState } from 'react';
import { X, LogIn, UserPlus, Shield, AlertCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { LoadingSpinner } from '../common/LoadingSpinner';

export const AuthModal: React.FC = () => {
  const {
    isAuthModalOpen,
    closeAuthModal,
    authMode,
    setAuthMode,
    login,
    register,
  } = useAuth();

  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');

  // Register fields
  const [fullName, setFullName] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');

  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isAuthModalOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      if (authMode === 'login') {
        if (!usernameOrEmail || !password) {
          throw new Error('Please enter your email/username and password.');
        }
        await login({ usernameOrEmail, password });
      } else {
        if (!fullName || !username || !email || !password) {
          throw new Error('Please fill in all required fields.');
        }
        if (password.length < 8) {
          throw new Error('Password must be at least 8 characters long.');
        }
        const cleanPhone = phone.trim().replace(/[\s-()]/g, '');
        await register({
          fullName: fullName.trim(),
          username: username.trim(),
          email: email.trim().toLowerCase(),
          password,
          phone: cleanPhone || '9876543210',
          phoneNumber: cleanPhone || '9876543210',
          role: 'ROLE_PASSENGER',
        });
      }
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Authentication failed. Please check your details and try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const fillDemoAdmin = () => {
    setUsernameOrEmail('admin@railsarathi.com');
    setPassword('admin@2026');
    setAuthMode('login');
  };

  const fillDemoPassenger = () => {
    setUsernameOrEmail('aarav@railsarathi.com');
    setPassword('password123');
    setAuthMode('login');
  };

  return (
    <div className="backdrop" onClick={closeAuthModal}>
      <div className="modal glass" onClick={(e) => e.stopPropagation()}>
        <button type="button" className="close-btn" onClick={closeAuthModal}>
          <X size={18} />
        </button>

        <div className="modal-header">
          <span className="eyebrow">SECURE SESSION ENCRYPTED</span>
          <h2>{authMode === 'login' ? 'Welcome back' : 'Create an account'}</h2>
          <p className="muted">
            {authMode === 'login'
              ? 'Sign in to access your bookings, tickets, and live status.'
              : 'Join RailSarathi for intelligent journey booking and instant refunds.'}
          </p>
        </div>

        <div className="auth-tabs">
          <button
            type="button"
            className={`tab-btn ${authMode === 'login' ? 'active' : ''}`}
            onClick={() => {
              setAuthMode('login');
              setError(null);
            }}
          >
            <LogIn size={15} /> Sign In
          </button>
          <button
            type="button"
            className={`tab-btn ${authMode === 'register' ? 'active' : ''}`}
            onClick={() => {
              setAuthMode('register');
              setError(null);
            }}
          >
            <UserPlus size={15} /> Register
          </button>
        </div>

        {error && (
          <div className="auth-error">
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          {authMode === 'register' && (
            <>
              <label>
                <span>Full Name</span>
                <input
                  type="text"
                  placeholder="e.g. Aarav Mehta"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  required
                />
              </label>
              <label>
                <span>Username</span>
                <input
                  type="text"
                  placeholder="e.g. aaravmehta"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </label>
              <label>
                <span>Email Address</span>
                <input
                  type="email"
                  placeholder="e.g. aarav@railsarathi.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </label>
              <label>
                <span>Phone Number (Optional)</span>
                <input
                  type="tel"
                  placeholder="+91 98765 43210"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </label>
            </>
          )}

          {authMode === 'login' && (
            <label>
              <span>Email or Username</span>
              <input
                type="text"
                placeholder="Enter email or username"
                value={usernameOrEmail}
                onChange={(e) => setUsernameOrEmail(e.target.value)}
                required
              />
            </label>
          )}

          <label>
            <span>Password (min. 8 characters)</span>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>

          <button
            type="submit"
            className="btn primary wide"
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <LoadingSpinner inline size={16} message="Authenticating..." />
            ) : authMode === 'login' ? (
              <>
                <LogIn size={16} /> Sign In
              </>
            ) : (
              <>
                <UserPlus size={16} /> Create Account
              </>
            )}
          </button>
        </form>

        <div className="demo-credentials">
          <span>Quick Demo Access:</span>
          <div className="demo-buttons">
            <button type="button" className="btn-text" onClick={fillDemoPassenger}>
              Passenger Account
            </button>
            <button type="button" className="btn-text" onClick={fillDemoAdmin}>
              <Shield size={12} /> Admin Workspace
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
