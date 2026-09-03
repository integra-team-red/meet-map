import {Component, computed, inject, signal} from '@angular/core';
import {ScrollPanelModule} from 'primeng/scrollpanel'
import {FormsModule} from '@angular/forms';
import {AdminEventList} from '../admin-event-list/admin-event-list';
import {AdminEventDetails} from '../admin-event-details/admin-event-details';
import {EventDto} from '@app/api/model/eventDto';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {NgClass} from '@angular/common';
import {CreateEventDto} from '@app/api/model/createEventDto';
import {ToastNotificationService} from '../../../shared/ui/toast-notification-service/toast-notification-service';

@Component({
  selector: 'app-admin-panel',
  imports: [
    ScrollPanelModule,
    FormsModule,
    AdminEventList,
    AdminEventDetails,
    NgClass
  ],
  templateUrl: './admin-panel.html',
})
export class AdminPanel {
  eventService: EventControllerService = inject(EventControllerService);
  isEventSelected = computed(() => {
    console.log(!!this.detailsEvent() ? "Event #" + this.detailsEvent()!.id + " is selected." : "No event is selected");
    return !!this.detailsEvent();
  });

  constructor(private toastNotification: ToastNotificationService) {}

  listPage = signal<PageEventDto|null>(null);
  detailsEvent = signal<EventDto|null>(null);

  pageNumCached = 0;
  rowsNumCached = 10;

  protected backendGetEventPage(page: number = this.pageNumCached, rows: number = this.rowsNumCached) {
    this.pageNumCached = page
    this.rowsNumCached = rows
    this.eventService.getAllEvents({ page: page, size: rows, sort: ["desc"] })
      .subscribe((response: PageEventDto) => {
        console.log(response);
        this.listPage.set(response);
      });
  }

  protected backendGetEvent(id: number) {
    this.eventService.getEvent(id)
      .subscribe((response: EventDto)=> {
        console.log(response);
        this.replaceEvent(response)
        this.detailsEvent.set(structuredClone(response));
      })
  }

  protected backendUpdateEvent(e: EventDto) {
    this.eventService.updateEvent(e.id!, e as CreateEventDto).subscribe((response) => {
      this.replaceEvent(response);
      this.toastNotification.showSuccess("The Event has been updated.")
    })
  }

  protected backendDeleteEvent(e: EventDto){
    this.eventService.deleteEvent(e.id!)
      .subscribe(() => {
        this.detailsEvent.set(null);
        this.backendGetEventPage()
        this.toastNotification.showSuccess("The Event has been canceled.")
      });
  }

  protected replaceEvent(e: EventDto){
    this.listPage.update((page) => {
      if(!page) return page;
      for(let i=0; i<page.content!.length!; i++){
        if(page.content![i].id == e.id){
          page.content![i] = e;
          break
        }
      }
      return page;
    })
    this.detailsEvent.update((event) => (event && event!.id == e.id) ? structuredClone(e) : event)
  }

  pageRequested = (pageable: number[]) => {
    if(pageable.length == 2)
      this.backendGetEventPage(pageable[0], pageable[1]);
    this.backendGetEventPage()
  }

  eventUpdated = (e: EventDto) => {
    this.backendUpdateEvent(e);
  }

  eventDeleted = (e: EventDto) => {
    this.backendDeleteEvent(e);
  }

  eventClicked = (e: EventDto)=> {
    try {
      this.detailsEvent.set(structuredClone(e))
    } catch (e) {
      console.log(this.detailsEvent)
    }
  }

  eventDeselected = () => {
    this.detailsEvent.set(null);
  }
}
