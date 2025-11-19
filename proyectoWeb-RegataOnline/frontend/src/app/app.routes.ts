import { Routes } from '@angular/router';
import { BarcoListaComponent } from './barco/barco-lista/barco-lista.component';
import { BarcoViewComponent } from './barco/barco-view/barco-view.component';
import { BarcoEditComponent } from './barco/barco-edit/barco-edit.component';
import { BarcoCreateComponent } from './barco/barco-create/barco-create.component';
import { JugadorListaComponent } from './jugador/jugador-lista/jugador-lista.component';
import { JugadorViewComponent } from './jugador/jugador-view/jugador-view.component';
import { JugadorEditComponent } from './jugador/jugador-edit/jugador-edit.component';
import { JugadorCreateComponent } from './jugador/jugador-create/jugador-create.component';
import { MapaListaComponent } from './mapa/mapa-lista/mapa-lista.component';
import { MapaCreateComponent } from './mapa/mapa-create/mapa-create.component';
import { MapaViewComponent } from './mapa/mapa-view/mapa-view.component';
import { ModeloListaComponent } from './modelo/modelo-lista/modelo-lista.component';
import { ModeloCreateComponent } from './modelo/modelo-create/modelo-create.component';
import { ModeloViewComponent } from './modelo/modelo-view/modelo-view.component';
import { ModeloEditComponent } from './modelo/modelo-edit/modelo-edit.component';
import { PartidaMenuComponent } from './partida/partida-menu/partida-menu.component';
import { PartidaCrearComponent } from './partida/partida-crear/partida-crear.component';
import { PartidaJuegoComponent } from './partida/partida-juego/partida-juego.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './security/login/login.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    // Barcos: list/view require authentication, create/edit require ADMIN role
    { path: 'barco/list', component: BarcoListaComponent, canActivate: [authGuard] },
    { path: 'barco/create', component: BarcoCreateComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
    { path: 'barco/view/:id', component: BarcoViewComponent, canActivate: [authGuard] },
    { path: 'barco/edit/:id', component: BarcoEditComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

    // Jugadores
    { path: 'jugador/list', component: JugadorListaComponent, canActivate: [authGuard] },
    { path: 'jugador/create', component: JugadorCreateComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
    { path: 'jugador/view/:id', component: JugadorViewComponent, canActivate: [authGuard] },
    { path: 'jugador/edit/:id', component: JugadorEditComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

    // Modelos
    { path: 'modelo/list', component: ModeloListaComponent, canActivate: [authGuard] },
    { path: 'modelo/create', component: ModeloCreateComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
    { path: 'modelo/view/:id', component: ModeloViewComponent, canActivate: [authGuard] },
    { path: 'modelo/edit/:id', component: ModeloEditComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

    // Mapas
    { path: 'mapa/list', component: MapaListaComponent, canActivate: [authGuard] },
    { path: 'mapa/create', component: MapaCreateComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
    { path: 'mapa/view/:id', component: MapaViewComponent, canActivate: [authGuard] },

    // Partidas: only USERs can jugar (admin cannot)
    { path: 'partida/menu', component: PartidaMenuComponent, canActivate: [authGuard], data: { roles: ['USER'] } },
    { path: 'partida/crear', component: PartidaCrearComponent, canActivate: [authGuard], data: { roles: ['USER'] } },
    { path: 'partida/juego/:id', component: PartidaJuegoComponent, canActivate: [authGuard], data: { roles: ['USER'] } }
];
