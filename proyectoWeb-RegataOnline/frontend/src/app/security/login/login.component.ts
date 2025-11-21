import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../shared/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
  <div class="login-container">
    <h2>Iniciar sesión</h2>
    <form (ngSubmit)="submit()">
      <label>
        Email
        <input name="email" [(ngModel)]="email" required />
      </label>
      <label>
        Contraseña
        <input name="password" [(ngModel)]="password" type="password" required />
      </label>
      <div class="actions">
        <button type="submit" class="btn btn-primary" [disabled]="loading">{{ loading ? 'Ingresando...' : 'Ingresar' }}</button>
      </div>
      <div *ngIf="error" class="error">{{ error }}</div>
    </form>
  </div>
  `,
  styles: [
    `.login-container {
        max-width: 420px;
        margin: 2.5rem auto;
        padding: 1.25rem 1.5rem;
        border-radius: 12px;
        background: #fff;
        box-shadow: 0 8px 30px rgba(12, 13, 14, 0.06);
        border: 1px solid rgba(12,13,14,0.04);
      }
      .login-container h2 { margin: 0 0 0.75rem 0; font-size: 1.25rem; color: #0b6ef6 }
      label { display:block; margin-bottom: 0.9rem; color: #2c3e50; font-weight:600 }
      input { width:100%; padding:0.7rem; margin-top:0.35rem; border-radius:8px; border:1px solid #e6e9ef }
      input:focus { outline:none; box-shadow: 0 4px 14px rgba(103, 117, 255, 0.12); border-color: rgba(102,126,234,0.6) }
      .actions { margin-top:1.25rem; display:flex; justify-content:flex-end }
      .error { margin-top:1rem; color: #b00020 }
      .note { font-size:0.9rem; color:#666; margin-top:0.5rem }
    `
  ]
})
export class LoginComponent {
  auth = inject(AuthService);
  router = inject(Router);

  email = '';
  password = '';
  error = '';
  loading = false;

  submit() {
    this.error = '';
    this.loading = true;
    console.log('Login submit', { email: this.email });
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.loading = false;
        console.log('Login success');
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        this.loading = false;
        console.error('Login error', err);
        
        this.error = err?.error?.message || err?.error || 'Error al iniciar sesión';
      }
    });
  }
}
