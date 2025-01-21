import { Routes } from '@angular/router';
import { LoginComponent } from "./public/features/login/login.component";

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'private', loadChildren: () => import('./private/private.routes').then(m => m.PRIVATE_ROUTES) },
  { path: '**', redirectTo: "login" }
];
