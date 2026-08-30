import { ApiResponse } from '../types/api.types';

const API_BASE = '/api/v1';

export async function request<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_BASE}${endpoint}`;
  const token = localStorage.getItem('railsarathi_token');

  const headers = new Headers(options.headers || {});
  headers.set('Accept', 'application/json');

  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  try {
    const res = await fetch(url, {
      ...options,
      headers,
    });

    const json: ApiResponse<T> = await res.json().catch(() => ({
      success: false,
      message: 'Failed to parse JSON response from server.',
      data: null as unknown as T,
      timestamp: new Date().toISOString(),
    }));

    if (!res.ok || !json.success) {
      const errorMessage = json.message || `Request failed with status ${res.status}`;
      throw new Error(errorMessage);
    }

    return json.data;
  } catch (error: unknown) {
    if (error instanceof Error) {
      throw error;
    }
    throw new Error('An unexpected network error occurred.');
  }
}
