import { request } from './client';
import { StationDto } from '../types/station.types';

export const stationApi = {
  getAllStations: async (): Promise<StationDto[]> => {
    return request<StationDto[]>('/stations', {
      method: 'GET',
    });
  },

  searchStations: async (query: string): Promise<StationDto[]> => {
    const params = new URLSearchParams();
    if (query && query.trim()) {
      params.append('query', query.trim());
    }
    const q = params.toString() ? `?${params.toString()}` : '';
    return request<StationDto[]>(`/stations/search${q}`, {
      method: 'GET',
    });
  },

  getStationByCode: async (code: string): Promise<StationDto> => {
    return request<StationDto>(`/stations/${encodeURIComponent(code)}`, {
      method: 'GET',
    });
  },
};
