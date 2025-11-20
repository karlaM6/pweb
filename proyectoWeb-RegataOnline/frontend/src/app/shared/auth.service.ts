import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const JWT_TOKEN = 'jwt-token';
const EMAIL = 'user-email';
const ROLE = 'user-role';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  http = inject(HttpClient);

  // Simple, untyped login to avoid coupling to DTOs in this shared service.
  login(loginDto: any): Observable<any> {
    return this.http.post<any>(`${environment.baseUrl}/auth/login`, loginDto).pipe(
      map((jwt: any) => {
        sessionStorage.setItem(JWT_TOKEN, jwt.token);
        sessionStorage.setItem(EMAIL, jwt.email ?? '');
        sessionStorage.setItem(ROLE, jwt.role ?? '');
        return jwt;
      })
    );
  }

  logout() {
    sessionStorage.removeItem(JWT_TOKEN);
    sessionStorage.removeItem(EMAIL);
    sessionStorage.removeItem(ROLE);
  }

  isAuthenticated(): boolean {
    return sessionStorage.getItem(JWT_TOKEN) != null;
  }

  token(): string | null {
    return sessionStorage.getItem(JWT_TOKEN);
  }

  role(): string | null {
    return sessionStorage.getItem(ROLE);
  }

  email(): string | null {
    return sessionStorage.getItem(EMAIL);
  }

  userId(): number | null {
    const token = this.token();
    if (!token) return null;
    try {
      const payloadPart = token.split('.')[1];
      if (!payloadPart) return null;
      const json = JSON.parse(atob(payloadPart));
      // Prefer jugadorId claim embedded in JWT; fall back to numeric id/userId
      const raw = json.jugadorId ?? json.id ?? json.userId;
      if (raw == null) return null;
      if (typeof raw === 'number') return raw;
      const numeric = Number(raw);
      return isNaN(numeric) ? null : numeric;
    } catch (e) {
      return null;
    }
  }

  // Return true when the stored role indicates admin privileges.
  // We handle both plain strings like 'ADMIN' and any object/string that contains 'ADMIN'.
  isAdmin(): boolean {
    const r = this.role();
    if (!r) return false;
    try {
      // If role was stored as a JSON string, try parse and inspect
      const parsed = JSON.parse(r);
      if (parsed && (parsed.authority || parsed.role || parsed.name)) {
        const val = (parsed.authority || parsed.role || parsed.name).toString().toUpperCase();
        return val.includes('ADMIN');
      }
    } catch (_) {
      // not JSON, fallthrough
    }
    return r.toString().toUpperCase().includes('ADMIN');
  }
}
