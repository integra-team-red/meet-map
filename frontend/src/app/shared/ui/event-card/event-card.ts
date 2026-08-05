import {Component, input} from '@angular/core';
import {Card} from 'primeng/card';
import {Tag} from 'primeng/tag';

@Component({
  selector: 'app-event-card',
  imports: [
    Card,
    Tag
  ],
  templateUrl: './event-card.html',
})
export class EventCard {
  readonly name = input.required<string>()
  readonly location = input.required<string>()
  readonly tags = input.required<number[]>()
}
