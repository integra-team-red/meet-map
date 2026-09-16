import {Component, inject, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {EventMap} from '../../../shared/ui/event-map/event-map';
import * as L from 'leaflet';

@Component({
  selector: 'app-mobile-map-page',
  imports: [
    EventMap
  ],
  templateUrl: './mobile-map-page.html',
})
export class MobileMapPage {
  private eventService = inject(EventControllerService);
  protected events = signal<EventDto[]>([]);
  private latestRequest = 0;

  protected onBoundsChanged(b: L.LatLngBounds): void {
    const id = ++this.latestRequest;

    this.eventService.getAllEvents(
      {page: 0, size: 200},
      undefined, undefined, undefined, undefined,
      undefined, undefined, undefined, undefined,
      'ACTIVE',
      b.getCenter().lng, b.getCenter().lat,
      b.getSouth(), b.getNorth(), b.getWest(), b.getEast(),
    ).subscribe({
      next: (r: PageEventDto) => {
        if (id !== this.latestRequest) return;
        this.events.set(r.content ?? []);
      },
      error: () => {
      }
    });
  }
}
