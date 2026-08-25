import {Component, computed, effect, inject, input, numberAttribute, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {DatePipe, TitleCasePipe} from '@angular/common';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {EventParticipationControllerService} from '@app/api/api/eventParticipationController.service';
import {ReviewControllerService} from '@app/api/api/reviewController.service';
import {PageReviewDto} from '@app/api/model/pageReviewDto';
import {ReviewCard} from '../../../features/review-card/review-card';
import {Tag} from 'primeng/tag';
import {ParticipantsCard} from '../../../features/participants-card/participants-card';
import {Button} from 'primeng/button';
import {PageEventParticipationDto} from '@app/api/model/pageEventParticipationDto';

@Component({
  selector: 'app-event-details-page',
  imports: [DatePipe, Rating, FormsModule, ReviewCard, Tag, ParticipantsCard, Button, DatePipe, TitleCasePipe],
  templateUrl: './event-details-page.html',
})
export class EventDetailsPage {
  readonly id = input.required({transform: numberAttribute});
  event = signal<EventDto | undefined>(undefined);
  participantsPage = signal<PageEventParticipationDto | undefined>(undefined);
  participantsCount = computed(() => this.participantsPage()?.totalElements);
  participants = computed(() =>
    (this.participantsPage()?.content ?? []));
  reviewPage = signal<PageReviewDto | undefined>(undefined);
  reviewCount = computed(() => this.reviewPage()?.totalElements);
  averageRating = signal<number | undefined>(undefined);
  starRating = computed(() => Math.round((this.averageRating() ?? 0)));
  reviews = computed(() => this.reviewPage()?.content ?? []);
  private eventService = inject(EventControllerService);
  private participationService = inject(EventParticipationControllerService);
  private reviewService = inject(ReviewControllerService);

  constructor() {
    effect(() => {
      const id = this.id();
      this.eventService.getEvent(this.id()).subscribe(e => this.event.set(e));
      this.participationService.getAllParticipants(id, {page: 0, size: 20})
        .subscribe(p => this.participantsPage.set(p));
      this.reviewService.getAllReviewsForEvent(id, {page: 0, size: 20})
        .subscribe(p => this.reviewPage.set(p));
      this.reviewService.getAverageRating(id)
        .subscribe(r => this.averageRating.set(r));
    });
  }

  ageRestriction = computed(() => {
    const {minAge, maxAge} = this.event() ?? {};
    if (minAge && maxAge) return `${minAge} - ${maxAge}`;
    if (minAge) return `${minAge}+`;
    if (maxAge) return `${maxAge} and below`;
    return 'None';
  });

  statusSeverity = computed<'success' | 'danger' | 'secondary'>(() => {
    switch (this.event()?.status) {
      case EventDto.StatusEnum.Cancelled:
        return 'danger';
      case EventDto.StatusEnum.Completed:
        return 'secondary';
      default:
        return 'success';
    }
  });

}
