import {Routes} from '@angular/router';
import {HomeComponent} from "./features/home/home.component";
import {PrivateComponent} from "./private.component";
import {PositionDetailComponent} from "./features/position-detail/position-detail.component";
import {PositionViewComponent} from "./features/position-view/position-view.component";
import {ApplicationsComponent} from "./features/applications/applications.component";
import {ApplicationDetailComponent} from "./features/application-detail/application-detail.component";

export const PRIVATE_ROUTES: Routes = [
  { path: '', component: PrivateComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'position-detail', component: PositionDetailComponent },
      { path: 'position-view', component: PositionViewComponent },
      { path: 'applications-list', component: ApplicationsComponent },
      { path: 'application-detail', component: ApplicationDetailComponent }
    ]},
  { path: '**', redirectTo: "home" },
];
