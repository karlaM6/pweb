import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BarcoViewComponent } from './barco/barco-view/barco-view.component';
import { BarcoListaComponent } from './barco/barco-lista/barco-lista.component';
import { AuthService } from './shared/auth.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, BarcoViewComponent, BarcoListaComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  // Expose auth service for template role-based navigation filtering
  auth = inject(AuthService);
}
