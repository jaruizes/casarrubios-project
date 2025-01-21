import { Routes } from '@angular/router';
import { HomeComponent } from "./features/home/home.component";
import { NewPositionComponent } from "./features/new-position/new-position.component";
import {PrivateComponent} from "./private.component";

export const PRIVATE_ROUTES: Routes = [
  { path: '', component: PrivateComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'new-position', component: NewPositionComponent }
    ]},
  { path: '**', redirectTo: "home" },
];
