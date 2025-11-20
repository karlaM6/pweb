import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";

export interface Partida {
  id?: number;
  jugadorId: number;
  jugadorNombre?: string;
  mapaId: number;
  mapaFilas?: number;
  mapaColumnas?: number;
  barcoId: number;
  barcoNombre?: string;
  barcoVelocidadX?: number;  // Velocidad actual en X
  barcoVelocidadY?: number;  // Velocidad actual en Y
  barcoPosicionX?: number;
  barcoPosicionY?: number;
  estado: string; // "activa", "pausada", "terminada"
  fechaInicio?: string;
  fechaUltimaJugada?: string;
  movimientos?: number;
  haLlegadoMeta?: boolean;
}

// Estado multijugador
export interface GameStateParticipant {
  jugadorId: number;
  jugadorEmail: string;
  barcoId: number;
  barcoNombre: string;
  posicionX: number;
  posicionY: number;
  velocidadX: number;
  velocidadY: number;
  estado: string;
}

export interface GameState {
  partidaId: number;
  estadoPartida: string;
  haLlegadoMeta: boolean;
  participantes: GameStateParticipant[];
}

export interface CrearPartidaRequest {
  jugadorId: number;
  mapaId: number;
  barcoId: number;
}

export interface MoverBarcoRequest {
  aceleracionX: number;  // -1, 0, o +1
  aceleracionY: number;  // -1, 0, o +1
}

@Injectable({
  providedIn: 'root'
})
export class PartidaService {
  http = inject(HttpClient);
  url = environment.baseUrl + '/partida';
  barcosJugadorBaseUrl = environment.baseUrl + '/jugador/barcos';

  crearPartida(request: CrearPartidaRequest): Observable<Partida> {
    return this.http.post<Partida>(`${this.url}/crear`, request);
  }

  obtenerPartidaActiva(jugadorId: number): Observable<Partida> {
    return this.http.get<Partida>(`${this.url}/activa/${jugadorId}`);
  }

  obtenerPartida(id: number): Observable<Partida> {
    return this.http.get<Partida>(`${this.url}/${id}`);
  }

  listarPartidasJugador(jugadorId: number): Observable<Partida[]> {
    return this.http.get<Partida[]>(`${this.url}/jugador/${jugadorId}`);
  }

  pausarPartida(id: number): Observable<Partida> {
    return this.http.put<Partida>(`${this.url}/${id}/pausar`, {});
  }

  finalizarPartida(id: number): Observable<Partida> {
    return this.http.put<Partida>(`${this.url}/${id}/finalizar`, {});
  }

  reanudarPartida(id: number): Observable<Partida> {
    return this.http.put<Partida>(`${this.url}/${id}/reanudar`, {});
  }

  moverBarco(partidaId: number, aceleracionX: number, aceleracionY: number): Observable<Partida> {
    return this.http.put<Partida>(
      `${this.url}/${partidaId}/mover?aceleracionX=${aceleracionX}&aceleracionY=${aceleracionY}`, 
      {}
    );
  }

  moverBarcoMultijugador(partidaId: number, barcoId: number, aceleracionX: number, aceleracionY: number): Observable<Partida> {
    return this.http.put<Partida>(
      `${this.url}/${partidaId}/mover?barcoId=${barcoId}&aceleracionX=${aceleracionX}&aceleracionY=${aceleracionY}`,
      {}
    );
  }

  unirAPartida(partidaId: number, jugadorId: number, barcoId: number): Observable<GameState> {
    return this.http.post<GameState>(`${this.url}/${partidaId}/join?jugadorId=${jugadorId}&barcoId=${barcoId}`, {});
  }

  estadoPartida(partidaId: number): Observable<GameState> {
    return this.http.get<GameState>(`${this.url}/${partidaId}/estado`);
  }

  listarBarcosJugador(jugadorId: number): Observable<BarcoResumen[]> {
    return this.http.get<BarcoResumen[]>(`${this.barcosJugadorBaseUrl}/${jugadorId}/list`);
  }
}

export interface BarcoResumen {
  id: number;
  nombre: string;
  posicionX?: number;
  posicionY?: number;
}
