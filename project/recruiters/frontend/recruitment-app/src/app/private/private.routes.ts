import { Routes } from '@angular/router';
import { HomeComponent } from "./features/home/home.component";
import { NewPositionComponent } from "./features/new-position/new-position.component";
import {PrivateComponent} from "./private.component";
import {PositionDetailComponent} from "./features/position-detail/position-detail.component";

export const PRIVATE_ROUTES: Routes = [
  { path: '', component: PrivateComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'new-position', component: NewPositionComponent },
      { path: 'position-detail', component: PositionDetailComponent }
    ]},
  { path: '**', redirectTo: "home" },
];
