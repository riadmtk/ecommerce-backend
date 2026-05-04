export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}