import {Routes} from '@angular/router';
import {HomePage} from './home/feature/home-page/home-page';
import {LoginPage} from './auth/feature/login-page/login-page';
import {SignupPage} from './auth/feature/signup-page/signup-page';
import {AdminPanel} from './admin/feature/admin-panel/admin-panel';
import {EventCreateComponent} from './features/event-create/event-create';
import {ProfilePage} from './profile/feature/profile-page/profile-page';
import {authGuard} from './core/guards/auth-guard/auth-guard';

export const routes: Routes = [
  {
    path: '',
    component: HomePage,
  },
  {
    path: 'login',
    canActivate: [authGuard],
    component: LoginPage,
  },
  {
    path: 'signup',
    canActivate: [authGuard],
    component: SignupPage,
  },
  {
    path: 'admin',
    component: AdminPanel
  },
  {
    path: 'events/create',
    component: EventCreateComponent,
  },
  {
    path: 'profile',
    component: ProfilePage,
  },
];
