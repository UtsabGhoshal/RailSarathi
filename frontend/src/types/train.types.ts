import { StationDto } from './station.types';

export type TrainType =
  | 'VANDE_BHARAT'
  | 'RAJDHANI'
  | 'SHATABDI'
  | 'DURONTO'
  | 'SUPERFAST'
  | 'EXPRESS'
  | 'MAIL'
  | 'LOCAL';

export type CoachClass =
  | 'FIRST_AC'
  | 'SECOND_AC'
  | 'THIRD_AC'
  | 'THIRD_AC_ECONOMY'
  | 'AC_CHAIR_CAR'
  | 'EXECUTIVE_CHAIR_CAR'
  | 'SLEEPER'
  | 'SECOND_SITTING';

export interface ClassAvailabilityDto {
  coachClass: CoachClass;
  classCode: string;
  className: string;
  fare: number;
  totalSeats: number;
  availableSeats: number;
  statusCode: 'AVL' | 'RAC' | 'WL' | string;
  statusDescription: string;
}

export interface TrainScheduleDto {
  stopOrder: number;
  stationCode: string;
  stationName: string;
  city: string;
  state: string;
  arrivalTime: string | null;
  departureTime: string | null;
  haltMinutes: number;
  distanceFromSourceKm: number;
  dayNumber: number;
  platform?: string;
}

export interface TrainSearchResultDto {
  trainId: number;
  trainNumber: string;
  trainName: string;
  trainType: TrainType;
  originStation: StationDto;
  terminusStation: StationDto;
  boardingStation: StationDto;
  destinationStation: StationDto;
  departureTime: string;
  arrivalTime: string;
  durationFormatted: string;
  travelDistanceKm: number;
  dayDifference: number;
  runsOnDays: string;
  availableClasses: ClassAvailabilityDto[];
  routeSchedule: TrainScheduleDto[];
}

export interface LiveTrainStatusDto {
  trainNumber: string;
  trainName: string;
  currentStation: string;
  nextStation: string;
  delayMinutes: number;
  statusMessage: string;
  expectedArrival: string;
  dataSource: string;
  checkedAt: string;
  rawInsights: string[];
}
