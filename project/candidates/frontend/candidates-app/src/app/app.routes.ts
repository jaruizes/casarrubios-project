import { Routes } from '@angular/router';
import {HomeComponent} from "./features/home/home.component";
import {PositionViewComponent} from "./features/position-view/position-view.component";

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full'},
  { path: 'home', component: HomeComponent },
  { path: 'position-detail', component: PositionViewComponent },
  { path: '**', redirectTo: "home" }
];
