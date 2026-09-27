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
    data: {description: 'MeetMap helps you discover events nearby and meet people who share your interests.'},
  },
  {
    path: 'home',
    canActivate: [authGuard],
    component: HomePage,
    title: 'Browse Events',
    data: {description: 'Browse events happening near you and join the ones that interest you.'},
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    component: LoginPage,
    title: 'Log In',
    data: {description: 'Log in to your MeetMap account to browse and join events near you.'},
  },
  {
    path: 'signup',
    canActivate: [guestGuard],
    component: SignupPage,
    title: 'Sign Up',
    data: {description: 'Create a MeetMap account to discover events and meet people nearby.'},
  },
  {
    path: 'admin',
    component: AdminPanel,
    title: 'Admin Panel',
    data: {description: 'Manage events and reported content on MeetMap.'}
  },
  {
    path: 'events/create',
    canActivate: [authGuard],
    component: EventCreateComponent,
    title: 'Create an Event',
    data: {description: 'Create a new event on MeetMap and invite people nearby to join.'}
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    component: ProfilePage,
    title: 'Your Profile',
    data: {description: 'View your MeetMap profile, the events you host and the ones you have joined.'},
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
    title: 'Event Map',
    data: {description: 'Explore events happening near you on the MeetMap map.'},
  },
];
