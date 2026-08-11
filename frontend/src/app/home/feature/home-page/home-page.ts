import {Component, inject, OnInit, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {EventCard} from '../../../shared/ui/event-card/event-card';


@Component({
  selector: 'app-home-page',
  imports: [
    EventCard,
    FormsModule,
  ],
  templateUrl: './home-page.html',
})
export class HomePage implements OnInit {
  eventService: EventControllerService = inject(EventControllerService);
  router = inject(Router);

  events = signal<EventDto[]>([])

  ngOnInit(): void {
    this.eventService.getAllEvents({
      page: 0,
      size: 20,
    }).subscribe((response: PageEventDto) => {
      this.events.set(response.content!)
    })
  }

  openEvent(eventId: number): void {
    this.router.navigate([`/events/${eventId}`])
  }
}
