import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PartidaService, Partida } from '../../shared/partida.service';
import { JugadorService } from '../../shared/jugador.service';
import { AuthService } from '../../shared/auth.service';
import { Jugador } from '../../model/jugador';

@Component({
  selector: 'app-partida-menu',
  imports: [CommonModule, FormsModule],
  templateUrl: './partida-menu.component.html',
  styleUrl: './partida-menu.component.css'
})
export class PartidaMenuComponent {
  partidaService = inject(PartidaService);
  jugadorService = inject(JugadorService);
  router = inject(Router);
  auth = inject(AuthService);

  jugadores = signal<Jugador[]>([]);
  jugadorSeleccionadoId = signal<number | null>(null);
  partidaActiva = signal<Partida | null>(null);
  cargando = signal<boolean>(false);
  mensaje = signal<string>('');

  ngOnInit(): void {
    this.setLoggedInPlayer();
  }

  setLoggedInPlayer(): void {
    const loggedInPlayerId = this.auth.email(); // Assuming email uniquely identifies the player
    if (loggedInPlayerId) {
      this.jugadorService.findByEmail(loggedInPlayerId).subscribe({
        next: (jugador: Jugador) => {
          if (jugador.id !== undefined) {
            this.jugadorSeleccionadoId.set(jugador.id);
            this.buscarPartidaActiva(jugador.id);
          } else {
            console.error('Jugador ID is undefined');
          }
        },
        error: (err: any) => console.error('Error fetching logged-in player', err)
      });
    }
  }

  buscarPartidaActiva(jugadorId: number): void {
    this.cargando.set(true);
    this.mensaje.set('');
    
    this.partidaService.obtenerPartidaActiva(jugadorId).subscribe({
      next: (partida: Partida) => {
        this.partidaActiva.set(partida);
        this.mensaje.set('');
        this.cargando.set(false);
      },
      error: () => {
        this.partidaActiva.set(null);
        this.mensaje.set('');
        this.cargando.set(false);
      }
    });
  }

  iniciarNuevaPartida(): void {
    if (!this.jugadorSeleccionadoId()) {
      alert('No se encontró un jugador asociado a esta cuenta.');
      return;
    }
    
    this.router.navigate(['/partida/crear'], {
      queryParams: { jugadorId: this.jugadorSeleccionadoId() }
    });
  }

  continuarPartida(): void {
    const partida = this.partidaActiva();
    if (!partida || !partida.id) {
      alert('No hay partida para continuar');
      return;
    }
    
    this.router.navigate(['/partida/juego', partida.id]);
  }

  verDetallesPartida(): void {
    const partida = this.partidaActiva();
    if (!partida) return;

    alert(`
📊 Detalles de la Partida:
━━━━━━━━━━━━━━━━━━━━━
🎮 Estado: ${partida.estado}
🗺️ Mapa: ${partida.mapaFilas}x${partida.mapaColumnas}
🚢 Barco: ${partida.barcoNombre}
📍 Posición: (${partida.barcoPosicionX}, ${partida.barcoPosicionY})
🔢 Movimientos: ${partida.movimientos}
📅 Última jugada: ${partida.fechaUltimaJugada ? new Date(partida.fechaUltimaJugada).toLocaleString() : 'N/A'}
    `);
  }

  finalizarPartida(): void {
    const partida = this.partidaActiva();
    if (!partida || !partida.id) return;

    if (!confirm('¿Estás seguro de finalizar esta partida? Esta acción no se puede deshacer.')) {
      return;
    }

    this.partidaService.finalizarPartida(partida.id).subscribe({
      next: () => {
        alert('Partida finalizada exitosamente');
        this.partidaActiva.set(null);
      },
      error: (err: any) => {
        console.error('Error al finalizar partida', err);
        alert('Error al finalizar la partida');
      }
    });
  }
}
