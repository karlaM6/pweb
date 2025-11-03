import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BoatListComponent } from './boats/boat-list/boat-list';
import { BoatFormComponent } from './boats/boat-form/boat-form';

const routes: Routes = [
  { path: 'boats', component: BoatListComponent },
  { path: 'boats/nuevo', component: BoatFormComponent },
  { path: '', redirectTo: '/boats', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
