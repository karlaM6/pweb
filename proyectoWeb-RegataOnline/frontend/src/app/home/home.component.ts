import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../shared/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  auth = inject(AuthService);
  router = inject(Router);

  isAuthenticated(): boolean {
    return this.auth.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.auth.role() === 'ADMIN' || this.auth.role() === 'ROLE_ADMIN';
  }

  getEmail(): string | null {
    return this.auth.email();
  }

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/');
  }
}
