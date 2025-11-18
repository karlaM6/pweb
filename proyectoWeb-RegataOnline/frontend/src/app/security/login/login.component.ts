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
        <button type="submit" [disabled]="loading">{{ loading ? 'Ingresando...' : 'Ingresar' }}</button>
      </div>
      <div *ngIf="error" class="error">{{ error }}</div>
    </form>
  </div>
  `,
  styles: [
    `.login-container { max-width: 360px; margin: 2rem auto; padding: 1rem; border: 1px solid #ddd; border-radius: 8px; }
     label { display:block; margin-bottom: 0.75rem }
     input { width:100%; padding:0.5rem; margin-top:0.25rem }
     .actions { margin-top:1rem }
     .error { margin-top:1rem; color: #b00020 }
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
        // Try to display a useful message
        this.error = err?.error?.message || err?.error || 'Error al iniciar sesión';
      }
    });
  }
}
