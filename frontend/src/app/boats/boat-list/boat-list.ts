import { Component, OnInit } from '@angular/core';
import { BoatService } from '../../services/boat';
import { CommonModule } from '@angular/common'; 

@Component({
  selector: 'app-boat-list',
  standalone: true, 
  imports: [CommonModule], 
  templateUrl: './boat-list.html',
  styleUrls: ['./boat-list.css']
})
export class BoatList implements OnInit {

  boats: any[] = [];
  cargando = true;

  constructor(private boatService: BoatService) {}

  ngOnInit(): void {
    this.cargarBarcos();
  }

  cargarBarcos(): void {
    this.boatService.listar().subscribe(data => {
      this.boats = data;
      this.cargando = false;
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Seguro que deseas eliminar este barco?')) {
      this.boatService.eliminar(id).subscribe(() => this.cargarBarcos());
    }
  }
}
