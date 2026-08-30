import { CoachClass, TrainSearchResultDto } from './train.types';

export type BerthPreference = 'LOWER' | 'MIDDLE' | 'UPPER' | 'SIDE_LOWER' | 'SIDE_UPPER' | 'WINDOW' | 'AISLE' | 'NO_PREFERENCE';

export interface Passenger {
  fullName: string;
  age: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  berthPreference: BerthPreference;
}

export interface BookingDetails {
  train: TrainSearchResultDto;
  selectedClass: CoachClass;
  journeyDate: string;
  passengers: Passenger[];
  contactEmail: string;
  contactPhone: string;
}

export interface TicketConfirmation {
  pnrNumber: string;
  bookingId: string;
  trainNumber: string;
  trainName: string;
  fromStationCode: string;
  fromStationName: string;
  toStationCode: string;
  toStationName: string;
  departureTime: string;
  arrivalTime: string;
  journeyDate: string;
  coachClass: string;
  coachNumber: string;
  seatNumbers: string[];
  passengers: Passenger[];
  totalFare: number;
  status: 'CONFIRMED' | 'RAC' | 'WAITLIST';
  bookedAt: string;
}
