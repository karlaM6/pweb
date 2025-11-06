import { Routes } from '@angular/router';
import { BoatList } from './boats/boat-list/boat-list';
import { BoatForm } from './boats/boat-form/boat-form';
import { ModelList } from './models/model-list/model-list';
import { ModelForm } from './models/model-form/model-form';
import { MapList } from './maps/map-list/map-list';

export const routes: Routes = [
  { path: '', redirectTo: '/boats', pathMatch: 'full' },
  { path: 'boats', component: BoatList },
  { path: 'boats/nuevo', component: BoatForm },
  { path: 'models', component: ModelList },
  { path: 'models/nuevo', component: ModelForm },
  { path: 'maps', component: MapList },
];
