import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Navbar } from './components/common/Navbar';
import { Footer } from './components/common/Footer';
import { AuthModal } from './components/auth/AuthModal';
import { HomePage } from './pages/HomePage';
import { SearchResultsPage } from './pages/SearchResultsPage';
import { TrainDetailsPage } from './pages/TrainDetailsPage';
import { BookingPage } from './pages/BookingPage';
import { ConfirmationPage } from './pages/ConfirmationPage';
import { DashboardPage } from './pages/DashboardPage';
import { AdminPage } from './pages/AdminPage';

export const App: React.FC = () => {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="main-viewport">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchResultsPage />} />
          <Route path="/train/:trainNumber" element={<TrainDetailsPage />} />
          <Route path="/booking/initiate" element={<BookingPage />} />
          <Route path="/booking/confirmation" element={<ConfirmationPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <Footer />
      <AuthModal />
    </div>
  );
};
