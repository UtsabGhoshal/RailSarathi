import { request } from './client';
import {
  LiveTrainStatusDto,
  TrainScheduleDto,
  TrainSearchResultDto,
} from '../types/train.types';

export const trainApi = {
  searchTrains: async (
    source: string,
    destination: string,
    journeyDate?: string
  ): Promise<TrainSearchResultDto[]> => {
    const params = new URLSearchParams();
    params.append('source', source.trim());
    params.append('destination', destination.trim());
    if (journeyDate) {
      params.append('journeyDate', journeyDate);
    }
    return request<TrainSearchResultDto[]>(`/trains/search?${params.toString()}`, {
      method: 'GET',
    });
  },

  getTrainDetails: async (trainNumber: string): Promise<TrainSearchResultDto> => {
    return request<TrainSearchResultDto>(`/trains/${encodeURIComponent(trainNumber)}`, {
      method: 'GET',
    });
  },

  getTrainSchedule: async (trainNumber: string): Promise<TrainScheduleDto[]> => {
    return request<TrainScheduleDto[]>(`/trains/${encodeURIComponent(trainNumber)}/schedule`, {
      method: 'GET',
    });
  },

  getLiveStatus: async (
    trainNumber: string,
    forceRefresh: boolean = false
  ): Promise<LiveTrainStatusDto> => {
    const params = new URLSearchParams();
    if (forceRefresh) {
      params.append('forceRefresh', 'true');
    }
    const q = params.toString() ? `?${params.toString()}` : '';
    return request<LiveTrainStatusDto>(
      `/trains/${encodeURIComponent(trainNumber)}/live-status${q}`,
      {
        method: 'GET',
      }
    );
  },
};
