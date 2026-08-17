import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly tokenKey = 'token';

  setToken(token: string, remember = true): void {
    this.clearToken();
    if (remember) {
      localStorage.setItem('token', token);
    } else {
      sessionStorage.setItem('token', token);
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token') ??
      sessionStorage.getItem('token');
  }

  isAuthenticated() {
    return !!this.getToken();
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}
