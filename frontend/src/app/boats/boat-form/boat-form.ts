import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BoatService } from '../../services/boat';
import { CommonModule } from '@angular/common'; 

@Component({
  selector: 'app-boat-form',
  standalone: true, 
  imports: [CommonModule, FormsModule], 
  templateUrl: './boat-form.html',
  styleUrls: ['./boat-form.css']
})
export class BoatForm {

  boat = { nombre: '', color: '', resistencia: 0 };

  constructor(private boatService: BoatService) {}

  guardar(): void {
    this.boatService.crear(this.boat).subscribe(() => {
      alert('Barco guardado correctamente');
      this.boat = { nombre: '', color: '', resistencia: 0 };
    });
  }
}
