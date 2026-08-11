import {Routes} from '@angular/router';
import {HomePage} from './home/feature/home-page/home-page';
import {LoginPage} from './auth/feature/login-page/login-page';
import {SignupPage} from './auth/feature/signup-page/signup-page';
import {AdminPanel} from './admin/feature/admin-panel/admin-panel';

export const routes: Routes = [
  {
    path: '',
    component: HomePage,
  },
  {
    path: 'login',
    component: LoginPage,
  },
  {
    path: 'signup',
    component: SignupPage,
  },
  {
    path: 'admin',
    component: AdminPanel
  }
];
