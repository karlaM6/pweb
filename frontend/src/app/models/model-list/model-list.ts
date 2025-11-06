import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModelService } from '../../services/model';

@Component({
  selector: 'app-model-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './model-list.html',
  styleUrls: ['./model-list.css']
})
export class ModelList implements OnInit {
  models: any[] = [];
  cargando = true;

  constructor(private modelService: ModelService) {}

  ngOnInit(): void {
    this.cargarModelos();
  }

  cargarModelos(): void {
    this.modelService.listar().subscribe({
      next: (data) => {
        this.models = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error cargando modelos', err);
        this.cargando = false;
      }
    });
  }

  eliminar(id: number): void {
    console.log('Eliminando modelo con ID:', id);
    if (confirm('¿Seguro que deseas eliminar este modelo?')) {
      this.modelService.eliminar(id).subscribe({
        next: () => {
          console.log('Modelo eliminado correctamente');
          this.cargarModelos();
        },
        error: (err) => console.error('Error eliminando modelo', err)
      });
    }
  }
}
