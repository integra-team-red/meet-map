import {Component, input} from '@angular/core';
import {Card} from 'primeng/card';
import {DatePipe} from '@angular/common';
import {TagDto} from '@app/api/model/tagDto';
import {Tag} from 'primeng/tag';
import {RouterLink} from '@angular/router';
import {EventDto} from '@app/api/model/eventDto';
import {StarRating} from '../star-rating/star-rating';

@Component({
  selector: 'app-event-card',
  imports: [
    Card,
    DatePipe,
    Tag,
    RouterLink,
    StarRating
  ],
  templateUrl: './event-card.html',
})
export class EventCard {
  readonly name = input.required<string>()
  readonly location = input.required<string>()
  readonly date = input.required<string>()
  readonly tags = input<TagDto[]>([]);
  readonly maxParticipants = input<number>();
  readonly id = input<number>()
  readonly status = input<EventDto.StatusEnum>()
  protected readonly EventDto = EventDto;
  readonly averageRating = input<number | undefined>();
  readonly reviewsCount = input<number | undefined>();
}
