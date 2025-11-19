import { Component, inject, input, signal, WritableSignal } from '@angular/core';
import { Barco } from '../../model/barco';
import { BarcoService } from '../../shared/barco.service';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from '../../shared/auth.service';

@Component({
  selector: 'app-barco-view',
  imports: [],
  templateUrl: './barco-view.component.html',
  styleUrl: './barco-view.component.css'
})
export class BarcoViewComponent {
  barcoService = inject(BarcoService);
  auth = inject(AuthService);

  route = inject(ActivatedRoute);

  router = inject(Router);

  barco = signal<Barco>({});

  ngOnInit(): void {
    this.route.params.pipe(
      switchMap(params => this.barcoService.findById(+params['id']))
    ).subscribe({
      next: resp => this.barco.set(resp),
      error: err => console.error('Error cargando barco', err)
    });
  }

  volver() {
    this.router.navigate(['/barco/list']);
  }

  eliminar() {
    const id = this.barco()?.id;
    const nombre = this.barco()?.nombre;
    
    if (!id) return;
    
    if (confirm(`¿Estás seguro de eliminar el barco "${nombre}"?`)) {
      this.barcoService.delete(id).subscribe({
        next: () => {
          console.log('Barco eliminado');
          this.router.navigate(['/barco/list']);
        },
        error: err => {
          alert('Error al eliminar el barco');
          console.error(err);
        }
      });
    }
  }
}
