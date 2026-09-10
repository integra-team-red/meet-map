import {Component, inject, OnInit, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {EventMap} from '../../../shared/ui/event-map/event-map';
@Component({
  selector: 'app-mobile-map-page',
  imports: [
    EventMap
  ],
  templateUrl: './mobile-map-page.html',
})
export class MobileMapPage implements OnInit{
  private eventService = inject(EventControllerService);
  protected events = signal<EventDto[]>([]);

  ngOnInit(): void {
    this.eventService.getAllEvents(
      {page: 0, size:200, sort: ['dateTime,asc']},
      undefined, undefined, undefined, undefined,
      undefined, undefined, undefined, undefined,
      'ACTIVE'
    ).subscribe((r: PageEventDto) => this.events.set(r.content?? []));
  }
}
