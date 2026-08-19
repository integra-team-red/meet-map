import {Component, input} from '@angular/core';
import {Card} from 'primeng/card';
import {EventDto} from '@app/api/model/eventDto';
import StatusEnum = EventDto.StatusEnum;

@Component({
  selector: 'app-admin-event-card',
  imports: [Card],
  templateUrl: './admin-event-card.html',
})
export class AdminEventCard {
  readonly name = input.required<string>();
  readonly description = input.required<string>();
  readonly flagCount = input.required<number>();
  readonly status = input.required<StatusEnum>();
}
