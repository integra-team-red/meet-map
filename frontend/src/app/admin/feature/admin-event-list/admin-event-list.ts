import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {AdminEventCard} from '../../../shared/ui/admin-event-card/admin-event-card';
import {Card} from 'primeng/card';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {ScrollPanel} from 'primeng/scrollpanel';
import {Paginator, PaginatorState} from 'primeng/paginator';
import {FlagControllerService} from '@app/api/api/flagController.service';

@Component({
  selector: 'app-admin-event-list',
  imports: [
    AdminEventCard,
    Card,
    IconField,
    InputIcon,
    InputText,
    ScrollPanel,
    Paginator
  ],
  templateUrl: './admin-event-list.html',
})
export class AdminEventList implements OnInit {
  eventService: EventControllerService = inject(EventControllerService);
  flagService: FlagControllerService = inject(FlagControllerService);
  page = signal<PageEventDto|undefined>(undefined);
  events = computed(() => this.page()?.content!)

  countPage = computed(() => 10)
  countTotal = computed(() => this.page()?.totalElements ?? 0)

  ngOnInit() {
    this.getEventPage(0, 10);
  }

  protected getEventPage(page: number | undefined, rows: number | undefined) {
    this.eventService.getAllEvents({ page: page ?? 0, size: rows ?? 10, sort: ["desc"] })
      .subscribe((response: PageEventDto) => {
        console.log(response);
        this.page.set(response);
      });
  }

  protected eventClicked(id: number) {
    console.log("clicked event " + id);
  }

  protected onPageChange(event: PaginatorState) {
    this.getEventPage(event.page, event.rows)
  }
}
