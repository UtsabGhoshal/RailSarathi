export interface StationDto {
  id: number;
  stationCode: string;
  stationName: string;
  city: string;
  state: string;
  zone?: string;
  totalPlatforms?: number;
}
