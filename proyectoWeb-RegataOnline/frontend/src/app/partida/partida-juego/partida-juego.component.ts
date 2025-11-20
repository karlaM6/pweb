import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PartidaService, Partida, GameState, GameStateParticipant, BarcoResumen } from '../../shared/partida.service';
import { AuthService } from '../../shared/auth.service';
import { MapaService, Celda, Mapa } from '../../shared/mapa.service';

@Component({
  selector: 'app-partida-juego',
  imports: [CommonModule],
  templateUrl: './partida-juego.component.html',
  styleUrl: './partida-juego.component.css'
})
export class PartidaJuegoComponent implements OnInit, OnDestroy {
  partidaService = inject(PartidaService);
  mapaService = inject(MapaService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  partida = signal<Partida | null>(null);
  // Estado multijugador agregado
  estadoMultijugador = signal<GameState | null>(null);
  participantes = signal<GameStateParticipant[]>([]);
  mapa = signal<Mapa | null>(null);
  matrizCeldas = signal<Celda[][]>([]);
  celdasDestinoPosibles = signal<{x: number, y: number, aceleracionX: number, aceleracionY: number}[]>([]);
  
  cargando = signal(true);
  mensaje = signal('');
  moviendo = signal(false);
  // Flags y selección multijugador
  multijugador = signal(true); // TODO: decidir dinámicamente según tipo de partida
  jugadorId = signal<number | null>(null); // Se obtiene del JWT
  barcoParaUnirId = signal(1); // Selección inicial de barco para unirse
  barcosDisponibles = signal<BarcoResumen[]>([]);
  barcoSeleccionadoId = signal<number | null>(null);

  authService = inject(AuthService);

  ngOnInit(): void {
    const partidaId = +this.route.snapshot.params['id'];
    const uid = this.authService.userId();
    if (uid != null && !isNaN(uid)) {
      this.jugadorId.set(uid);
      console.log('[DEBUG] jugadorId detectado en ngOnInit ->', uid);
      // Cargar barcos del jugador para selección segura
      this.partidaService.listarBarcosJugador(uid).subscribe({
        next: barcos => {
          this.barcosDisponibles.set(barcos);
          console.log('[DEBUG] barcosDisponibles cargados ->', barcos);
          if (barcos.length > 0) this.barcoParaUnirId.set(barcos[0].id);
        },
        error: err => console.warn('No se pudieron cargar barcos del jugador', err)
      });
    } else {
      console.warn('No se pudo determinar jugadorId desde el JWT (uid=', uid, ')');
    }
    // Cargar partida base y estado; el usuario se unirá manualmente con el botón
    this.cargarPartida(partidaId);
    if (this.multijugador()) {
      this.cargarEstado(partidaId);
    }
    this.iniciarPolling(partidaId);
  }

  private pollingHandle: any;
  iniciarPolling(partidaId: number) {
    if (!this.multijugador()) return;
    if (this.pollingHandle) clearInterval(this.pollingHandle);
    this.pollingHandle = setInterval(() => {
      const p = this.partida();
      if (this.moviendo() || p?.estado === 'terminada') return;
      this.cargarEstado(partidaId);
    }, 5000);
  }

  ngOnDestroy(): void {
    if (this.pollingHandle) clearInterval(this.pollingHandle);
  }

  cargarPartida(id: number) {
    this.cargando.set(true);
    this.partidaService.obtenerPartida(id).subscribe({
      next: (partida) => {
        this.partida.set(partida);
        // Si la partida está pausada, reanudar automáticamente
        if (partida.estado === 'pausada' && partida.id) {
          this.partidaService.reanudarPartida(partida.id).subscribe({
            next: (reanudada) => {
              this.partida.set(reanudada);
            },
            error: (err) => {
              console.error('Error al reanudar partida', err);
              this.mensaje.set(typeof err.error === 'string' ? err.error : 'No se pudo reanudar la partida');
            }
          });
        }
        
        // Verificar si la partida ya terminó
        if (partida.estado === 'terminada') {
          if (partida.haLlegadoMeta) {
            this.mensaje.set('¡Felicidades! Has llegado a la meta 🎉');
          } else {
            this.mensaje.set('Esta partida ya ha terminado');
          }
        }
        
        // Cargar el mapa
        this.mapaService.buscarMapa(partida.mapaId).subscribe({
          next: (mapa) => {
            this.mapa.set(mapa);
            this.construirMatriz(mapa);
            this.calcularDestinosPosibles();
            this.cargando.set(false);
          },
          error: (err) => {
            console.error('Error cargando mapa', err);
            this.mensaje.set('Error al cargar el mapa');
            this.cargando.set(false);
          }
        });
      },
      error: (err) => {
        console.error('Error cargando partida', err);
        this.mensaje.set('Error al cargar la partida');
        this.cargando.set(false);
      }
    });
  }

  construirMatriz(mapa: Mapa) {
    if (!mapa.celdas) return;

    const matriz: Celda[][] = [];
    
    // i = fila (posicionY), j = columna (posicionX)
    for (let i = 0; i < mapa.filas; i++) {
      const fila: Celda[] = [];
      for (let j = 0; j < mapa.columnas; j++) {
        fila.push({
          posicionX: j,
          posicionY: i,
          tipo: ''
        });
      }
      matriz.push(fila);
    }

    // Llenar con las celdas del mapa
    mapa.celdas.forEach(celda => {
      if (celda.posicionY < mapa.filas && celda.posicionX < mapa.columnas) {
        matriz[celda.posicionY][celda.posicionX] = celda;
      }
    });

    this.matrizCeldas.set(matriz);
  }

  calcularDestinosPosibles() {
    const partida = this.partida();
    if (!partida) return;

    const velocidadX = partida.barcoVelocidadX || 0;
    const velocidadY = partida.barcoVelocidadY || 0;
    const posX = partida.barcoPosicionX || 0;
    const posY = partida.barcoPosicionY || 0;

    const destinos: {x: number, y: number, aceleracionX: number, aceleracionY: number}[] = [];

    // Calcular las 9 opciones de aceleración
    for (let ax = -1; ax <= 1; ax++) {
      for (let ay = -1; ay <= 1; ay++) {
        const nuevaVx = velocidadX + ax;
        const nuevaVy = velocidadY + ay;
        const destinoX = posX + nuevaVx;
        const destinoY = posY + nuevaVy;

        destinos.push({
          x: destinoX,
          y: destinoY,
          aceleracionX: ax,
          aceleracionY: ay
        });
      }
    }

    this.celdasDestinoPosibles.set(destinos);
  }

  moverBarco(aceleracionX: number, aceleracionY: number) {
    if (this.multijugador()) {
      const partidaId = this.partida()?.id;
      const barcoId = this.barcoSeleccionadoId();
      if (!partidaId || !barcoId) {
        this.mensaje.set('Selecciona tu barco antes de mover');
        return;
      }
      this.moviendo.set(true);
      this.partidaService.moverBarcoMultijugador(partidaId, barcoId, aceleracionX, aceleracionY).subscribe({
        next: () => {
          this.mensaje.set('Movimiento realizado');
          this.moviendo.set(false);
          this.cargarEstado(partidaId);
        },
        error: (err) => {
          let errorMsg = err.error?.message || (typeof err.error === 'string' ? err.error : 'Error al mover');
          this.mensaje.set(errorMsg);
          this.moviendo.set(false);
        }
      });
    } else {
      const partida = this.partida();
      if (!partida || !partida.id || partida.estado !== 'activa') {
        this.mensaje.set('No se puede mover el barco en esta partida');
        return;
      }
      this.moviendo.set(true);
      this.mensaje.set('');
      this.partidaService.moverBarco(partida.id, aceleracionX, aceleracionY).subscribe({
        next: (partidaActualizada) => {
          this.partida.set(partidaActualizada);
          this.calcularDestinosPosibles();
          this.moviendo.set(false);
          if (partidaActualizada.haLlegadoMeta) {
            this.mensaje.set('¡Felicidades! Has llegado a la meta en ' + partidaActualizada.movimientos + ' movimientos ');
          }
        },
        error: (err) => {
          let errorMsg = 'Error desconocido';
          if (typeof err.error === 'string') errorMsg = err.error;
          else if (err.error?.message) errorMsg = err.error.message;
          else if (err.message) errorMsg = err.message;
          this.mensaje.set(errorMsg);
          this.moviendo.set(false);
        }
      });
    }
  }

  clickCelda(x: number, y: number) {
    // Solo permitir click si es un destino posible y válido
    if (!this.esDestinoPosible(x, y) || !this.esDestinoValido(x, y)) {
      return;
    }

    const partida = this.partida();
    console.log('=== CLICK EN CELDA ===');
    console.log('Celda clickeada: (', x, ',', y, ')');
    console.log('Posición actual barco: (', partida?.barcoPosicionX, ',', partida?.barcoPosicionY, ')');
    console.log('Velocidad actual: (', partida?.barcoVelocidadX, ',', partida?.barcoVelocidadY, ')');

    // Buscar la aceleración correspondiente a este destino
    const destino = this.celdasDestinoPosibles().find(d => d.x === x && d.y === y);
    if (destino) {
      console.log('Aceleración a aplicar: (', destino.aceleracionX, ',', destino.aceleracionY, ')');
      console.log('Destino calculado: (', destino.x, ',', destino.y, ')');
      this.moverBarco(destino.aceleracionX, destino.aceleracionY);
    } else {
      console.error(' No se encontró destino para (', x, ',', y, ')');
    }
  }

  esBarco(x: number, y: number): boolean {
    if (this.multijugador()) {
      return this.participantes().some(p => p.posicionX === x && p.posicionY === y);
    }
    const partida = this.partida();
    return partida?.barcoPosicionX === x && partida?.barcoPosicionY === y;
  }

  esDestinoValido(x: number, y: number): boolean {
    const mapa = this.mapa();
    if (!mapa) return false;

    // Verificar límites
    if (x < 0 || x >= mapa.columnas || y < 0 || y >= mapa.filas) return false;

    // Verificar que no sea pared
    const matriz = this.matrizCeldas();
    const celda = matriz[y]?.[x];
    return celda?.tipo !== 'x';
  }

  esDestinoPosible(x: number, y: number): boolean {
    return this.celdasDestinoPosibles().some(d => d.x === x && d.y === y);
  }

  obtenerColorCelda(celda: Celda, x: number, y: number): string {
    // Barco
    if (this.esBarco(x, y)) return '#e74c3c'; // Rojo brillante
    
    // Destino posible
    if (this.esDestinoPosible(x, y) && this.esDestinoValido(x, y)) {
      return '#3498db80'; // Azul transparente
    }

    // Tipos de celda
    switch(celda.tipo) {
      case 'x': return '#2c3e50'; // Pared
      case 'P': return '#ff7a59'; // Partida (orange)
      case 'M': return '#f39c12'; // Meta
      default: return '#ecf0f1'; // Agua
    }
  }

  obtenerIconoCelda(celda: Celda, x: number, y: number): string {
    if (this.multijugador() && this.esBarco(x, y)) {
      const participante = this.participantes().find(p => p.posicionX === x && p.posicionY === y);
      if (participante) {
        // Ícono distinto para cada barco usando modulo
        const icons = ['⛵','🚤','🛶','🛥️','🚢'];
        return icons[participante.barcoId % icons.length];
      }
    }
    if (this.esBarco(x, y)) return '⛵';
    
    switch(celda.tipo) {
      case 'x': return '🧱';
      case 'P': return '🏁';
      case 'M': return '🎯';
      default: return '';
    }
  }

  getAccionTexto(ax: number, ay: number): string {
    if (ax === 0 && ay === 0) return 'Mantener';
    
    let texto = '';
    if (ax === -1) texto += '← ';
    if (ax === 1) texto += '→ ';
    if (ay === -1) texto += '↑ ';
    if (ay === 1) texto += '↓ ';
    
    return texto.trim() || `(${ax},${ay})`;
  }

  volverAlMenu() {
    this.router.navigate(['/partida/menu']);
  }

  pausarPartida() {
    const partida = this.partida();
    if (!partida?.id) return;

    this.partidaService.pausarPartida(partida.id).subscribe({
      next: () => {
        this.mensaje.set('Partida pausada');
        this.router.navigate(['/partida/menu']);
      },
      error: (err) => {
        console.error('Error al pausar', err);
        this.mensaje.set('Error al pausar la partida');
      }
    });
  }

  finalizarPartida() {
    const partida = this.partida();
    if (!partida?.id) return;

    if (confirm('¿Estás seguro de finalizar esta partida?')) {
      this.partidaService.finalizarPartida(partida.id).subscribe({
        next: () => {
          this.mensaje.set('Partida finalizada');
          this.router.navigate(['/partida/menu']);
        },
        error: (err) => {
          console.error('Error al finalizar', err);
          this.mensaje.set('Error al finalizar la partida');
        }
      });
    }
  }

  // --- Métodos multijugador ---
  participa(): boolean {
    return this.participantes().some(p => p.jugadorId === this.jugadorId());
  }

  joinPartida() {
    const partidaId = this.partida()?.id;
    const jugadorActual = this.jugadorId();
    const barcoId = this.barcoParaUnirId();
    if (!partidaId || jugadorActual == null || isNaN(jugadorActual)) {
      this.mensaje.set('Datos insuficientes para unirse');
      return;
    }
    if (!this.barcosDisponibles().some(b => b.id === barcoId)) {
      this.mensaje.set('Selecciona un barco válido de la lista');
      return;
    }
    if (this.participa()) {
      this.mensaje.set('Ya eres participante.');
      return;
    }
    this.mensaje.set('Uniéndose a la partida...');
    this.partidaService.unirAPartida(partidaId, jugadorActual, barcoId).subscribe({
      next: estado => {
        this.estadoMultijugador.set(estado);
        this.participantes.set(estado.participantes);
        const propio = estado.participantes.find(p => p.jugadorId === jugadorActual);
        if (propio) this.barcoSeleccionadoId.set(propio.barcoId);
        this.mensaje.set('Te has unido a la partida');
      },
      error: (err) => {
        let msg: string;
        if (typeof err.error === 'string') msg = err.error;
        else if (err.error?.mensaje) msg = err.error.mensaje;
        else if (err.error?.message) msg = err.error.message;
        else if (err.error?.error) msg = err.error.error; // ErrorDTO serialized
        else msg = 'No se pudo unir a la partida';
        if (msg.includes('BARCO_INVALIDO')) {
          msg = 'El barco seleccionado no pertenece a tu jugador. Elige uno de tu lista.';
        }
        this.mensaje.set(msg);
      }
    });
  }

  onBarcoInput(event: Event) {
    const raw = (event.target as HTMLSelectElement).value;
    const num = Number(raw);
    if (!isNaN(num)) this.barcoParaUnirId.set(num);
  }

  cargarEstado(partidaId: number) {
    this.partidaService.estadoPartida(partidaId).subscribe({
      next: estado => {
        this.estadoMultijugador.set(estado);
        this.participantes.set(estado.participantes);
        console.log('[DEBUG] estado recibido -> participantes:', estado.participantes);
        console.log('[DEBUG] jugadorId actual ->', this.jugadorId());
        if (!this.barcoSeleccionadoId()) {
          const propio = estado.participantes.find(p => p.jugadorId === this.jugadorId());
          if (propio) this.barcoSeleccionadoId.set(propio.barcoId);
        }
      },
      error: (err) => {
        // Silenciar 404 (partida aún no creada o no accesible) para evitar spam
        if (err.status === 404) return;
        // Mostrar otros errores una sola vez
        const msg = typeof err.error === 'string' ? err.error : (err.error?.message || 'Error obteniendo estado');
        this.mensaje.set(msg);
      }
    });
  }
}
