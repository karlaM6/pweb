import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BoatService } from '../../services/boat';

@Component({
  selector: 'app-boat-form',
  standalone: true, 
  imports: [FormsModule], 
  templateUrl: './boat-form.html',
  styleUrls: ['./boat-form.css']
})
export class BoatFormComponent {
  boat = { nombre: '', color: '', resistencia: 0 };
  
  constructor(private boatService: BoatService) {}
  
  guardar(): void {
    this.boatService.crear(this.boat).subscribe(() => {
      alert('Barco guardado correctamente');
      this.boat = { nombre: '', color: '', resistencia: 0 };
    });
  }
}