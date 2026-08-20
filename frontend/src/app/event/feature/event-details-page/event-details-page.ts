import {Component, computed, effect, inject, input, numberAttribute, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {DatePipe} from '@angular/common';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {EventParticipationControllerService} from '@app/api/api/eventParticipationController.service';
import {ReviewControllerService} from '@app/api/api/reviewController.service';
import {PageReviewDto} from '@app/api/model/pageReviewDto';

@Component({
  selector: 'app-event-details-page',
  imports: [DatePipe, Rating, FormsModule],
  templateUrl: './event-details-page.html',
})
export class EventDetailsPage {
  private eventService = inject(EventControllerService);
  private participationService = inject(EventParticipationControllerService);
  private reviewService = inject(ReviewControllerService);

  readonly id = input.required({transform: numberAttribute});

  event = signal<EventDto | undefined>(undefined);
  participantsCount = signal<number | undefined>(undefined);
  reviewPage = signal<PageReviewDto | undefined>(undefined);
  reviewCount = computed(() => this.reviewPage()?.totalElements);
  averageRating = signal<number | undefined>(undefined);
  starRating = computed(() => Math.round((this.averageRating() ?? 0)));

  constructor() {
    effect(() => {
      const id = this.id();
      this.eventService.getEvent(this.id()).subscribe(e => this.event.set(e));
      this.participationService.getAllParticipants(id, {page: 0, size: 1})
        .subscribe(p => this.participantsCount.set(p.totalElements));
      this.reviewService.getAllReviewsForEvent(id, {page: 0, size: 20})
        .subscribe(p => this.reviewPage.set(p));
      this.reviewService.getAverageRating(id)
        .subscribe(r => this.averageRating.set(r));
    });
  }
}
