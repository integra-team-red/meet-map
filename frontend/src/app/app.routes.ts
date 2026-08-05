import {Routes} from '@angular/router';
import {HomePage} from './home/feature/home-page/home-page';
import {EventDetailsPage} from './event/feature/event-details-page/event-details-page';

export const routes: Routes = [
  {
    path: '',
    component: HomePage,
  },
  {
    path: 'events/:id',
    component: EventDetailsPage
  }
];
