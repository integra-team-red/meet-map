import {Component, input} from '@angular/core';
import {Card} from 'primeng/card';

@Component({
  selector: 'app-admin-event-card',
  imports: [Card],
  templateUrl: './admin-event-card.html',
})
export class AdminEventCard {
  readonly name = input.required<string>();
  readonly description = input.required<string>();
  readonly flagCount = input.required<number>();
}
