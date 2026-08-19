import {
  Component,
  computed,
  input,
  OnInit,
  output
} from '@angular/core';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {AdminEventCard} from '../../../shared/ui/admin-event-card/admin-event-card';
import {Card} from 'primeng/card';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {ScrollPanel} from 'primeng/scrollpanel';
import {Paginator, PaginatorState} from 'primeng/paginator';
import {EventDto} from '@app/api/model/eventDto';

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
  page = input.required<PageEventDto|null>();
  pageRequest = output<number[]>()

  events = computed(() => this.page()?.content!)
  countPage = computed(() => 10)
  countTotal = computed(() => this.page()?.totalElements ?? 0)

  onEventClicked = output<EventDto>()

  ngOnInit() {
    this.requestPage()
  }

  protected requestPage(page: number = 0, rows: number = 10){
    this.pageRequest.emit([page, rows])
  }

  protected clickEvent(e: EventDto) {
    this.onEventClicked.emit(e)
  }

  protected changePage(event: PaginatorState) {
    this.requestPage(event.page, event.rows)
  }
}
