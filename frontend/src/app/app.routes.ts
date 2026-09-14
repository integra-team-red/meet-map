import {Routes} from '@angular/router';
import {HomePage} from './home/feature/home-page/home-page';
import {LoginPage} from './auth/feature/login-page/login-page';
import {SignupPage} from './auth/feature/signup-page/signup-page';
import {AdminPanel} from './admin/feature/admin-panel/admin-panel';
import {EventCreateComponent} from './features/event-create/event-create';
import {ProfilePage} from './profile/feature/profile-page/profile-page';
import {LandingPage} from './landing/feature/landing-page/landing-page';
import {authGuard} from './core/guards/auth-guard/auth-guard';
import {EventDetailsPage} from './event/feature/event-details-page/event-details-page';
import {MobileMapPage} from './home/feature/mobile-map-page/mobile-map-page';
import {guestGuard} from './core/guards/guest-guard/guest-guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [guestGuard],
    component: LandingPage,
  },
  {
    path: 'home',
    canActivate: [authGuard],
    component: HomePage,
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    component: LoginPage,
  },
  {
    path: 'signup',
    canActivate: [guestGuard],
    component: SignupPage,
  },
  {
    path: 'admin',
    component: AdminPanel
  },
  {
    path: 'events/create',
    canActivate: [authGuard],
    component: EventCreateComponent,
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    component: ProfilePage,
  },
  {
    path: 'events/:id',
    canActivate: [authGuard],
    component: EventDetailsPage,
  },
  {
    path: 'map',
    canActivate: [authGuard],
    component: MobileMapPage,
  },
];
