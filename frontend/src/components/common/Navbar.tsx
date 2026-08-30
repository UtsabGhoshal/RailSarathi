import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { TrainFront, LogOut, User as UserIcon, Shield, Search, Sparkles } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

import { NotificationBell } from '../notifications/NotificationBell';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout, openAuthModal } = useAuth();

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .substring(0, 2)
      .toUpperCase();
  };

  return (
    <header className="navbar">
      <Link to="/" className="brand">
        <i>
          <TrainFront size={20} />
        </i>
        Rail<span>Sarathi</span>
      </Link>

      <nav className="nav-links">
        <NavLink to="/search" className={({ isActive }) => (isActive ? 'active' : '')}>
          <Search size={15} /> Find Trains
        </NavLink>
        <NavLink to="/train/22301" className={({ isActive }) => (isActive ? 'active' : '')}>
          <Sparkles size={15} /> Live Track
        </NavLink>
        <NavLink to="/dashboard" className={({ isActive }) => (isActive ? 'active' : '')}>
          <UserIcon size={15} /> My Journeys
        </NavLink>
        {user?.role === 'ROLE_ADMIN' && (
          <NavLink to="/admin" className={({ isActive }) => (isActive ? 'active' : '')}>
            <Shield size={15} /> Admin Control
          </NavLink>
        )}
      </nav>

      <div className="nav-actions">
        {isAuthenticated && <NotificationBell />}

        {isAuthenticated && user ? (
          <div className="user-profile">
            <span className="avatar-badge">{getInitials(user.fullName || user.username)}</span>
            <div className="user-info">
              <span className="user-name">{user.fullName}</span>
              <span className="user-role">
                {user.role === 'ROLE_ADMIN' ? 'Administrator' : 'Passenger'}
              </span>
            </div>
            <button
              type="button"
              className="btn-icon"
              title="Sign Out"
              onClick={logout}
            >
              <LogOut size={16} />
            </button>
          </div>
        ) : (
          <div className="auth-buttons">
            <button
              type="button"
              className="btn ghost"
              onClick={() => openAuthModal('login')}
            >
              Log in
            </button>
            <button
              type="button"
              className="btn primary"
              onClick={() => openAuthModal('register')}
            >
              Get started
            </button>
          </div>
        )}
      </div>
    </header>
  );
};
