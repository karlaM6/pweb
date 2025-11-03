import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BoatService } from '../../services/boat';

@Component({
  selector: 'app-boat-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './boat-list.html',
  styleUrls: ['./boat-list.css']
})
export class BoatListComponent implements OnInit {
  boats: any[] = [];
  cargando: boolean = false; // 👈 Agrega esta propiedad

  constructor(private boatService: BoatService) {}

  ngOnInit(): void {
    this.cargarBoats();
  }

  cargarBoats(): void {
    this.cargando = true; // 👈 Activa el indicador
    this.boatService.listar().subscribe({
      next: (data) => {
        this.boats = data;
        this.cargando = false; // 👈 Desactiva cuando termina
      },
      error: (error) => {
        console.error('Error al cargar barcos:', error);
        this.cargando = false; // 👈 Desactiva también en caso de error
      }
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Estás seguro de eliminar este barco?')) {
      this.boatService.eliminar(id).subscribe({
        next: () => {
          alert('Barco eliminado correctamente');
          this.cargarBoats();
        },
        error: (error) => {
          console.error('Error al eliminar barco:', error);
          alert('Error al eliminar el barco');
        }
      });
    }
  }
}