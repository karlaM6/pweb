import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ModelService } from '../../services/model';

@Component({
  selector: 'app-model-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './model-form.html',
  styleUrls: ['./model-form.css']
})
export class ModelForm {
  model = { nombre_modelo: '', color: '', resistencia: 0 };

  constructor(private modelService: ModelService) {}

  guardar(): void {
    this.modelService.crear(this.model).subscribe(() => {
      alert('Modelo guardado correctamente');
      this.model = { nombre_modelo: '', color: '', resistencia: 0 };
    });
  }
}
