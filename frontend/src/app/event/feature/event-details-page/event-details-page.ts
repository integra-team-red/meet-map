import {Component, computed, effect, inject, input, numberAttribute, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {DatePipe, TitleCasePipe} from '@angular/common';
import {EventParticipationControllerService} from '@app/api/api/eventParticipationController.service';
import {ReviewControllerService} from '@app/api/api/reviewController.service';
import {PageReviewDto} from '@app/api/model/pageReviewDto';
import {ReviewCard} from '../../../features/review-card/review-card';
import {Tag} from 'primeng/tag';
import {ParticipantsCard} from '../../../features/participants-card/participants-card';
import {Button} from 'primeng/button';
import {PageEventParticipationDto} from '@app/api/model/pageEventParticipationDto';
import {StarRating} from '../../../shared/ui/star-rating/star-rating';
import {UserDto} from '@app/api/model/userDto';
import {UserControllerService} from '@app/api/api/userController.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-event-details-page',
  imports: [DatePipe, ReviewCard, Tag, ParticipantsCard, Button, TitleCasePipe, StarRating, Message],
  templateUrl: './event-details-page.html',
})
export class EventDetailsPage {
  readonly id = input.required({transform: numberAttribute});
  event = signal<EventDto | undefined>(undefined);
  participantsPage = signal<PageEventParticipationDto | undefined>(undefined);
  participantsCount = computed(() => this.participantsPage()?.totalElements);
  participants = computed(() => (this.participantsPage()?.content ?? []));
  joinLoading = signal(false);
  joinError = signal<string | undefined>(undefined);

  currentUser = signal<UserDto | undefined>(undefined);

  currentUserId = computed (() => this.currentUser()?.id);
  isParticipating = computed(() => {
    const participants = this.participants();
    const currentUserId = this.currentUserId();
    return participants.some(p => p.userId === currentUserId);
  });

  isFull = computed(() => {
    const max = this.event()?.maxParticipants;
    const count = this.participantsCount();
    return max != null && count != null && count >= max;
  });

  canJoinStatus = computed (() => {
    const status = this.event()?.status;
    return status !== EventDto.StatusEnum.Cancelled && status != EventDto.StatusEnum.Completed;
  })

  joinButtonDisabled = computed(() =>
    this.joinLoading() ||
    !this.canJoinStatus() ||
    (!this.isParticipating() && this.isFull())
  );

  joinButtonLabel = computed(() => {
    if (this.joinLoading()) return this.isParticipating() ? 'Leaving...' : 'Joining...';
    if (this.isParticipating()) return 'Leave Event';
    if (!this.canJoinStatus()) return 'Event unavailable';
    if (this.isFull()) return 'Event is full';
    return 'Join Event';
  })

  joinButtonIcon = computed (() => this.isParticipating() ? 'pi pi-sign-out' : 'pi pi-calendar-plus');
  joinButtonSeverity = computed<'success' | 'danger'> (() => this.isParticipating() ? 'danger' : 'success');

  reviewPage = signal<PageReviewDto | undefined>(undefined);
  reviews = computed(() => this.reviewPage()?.content ?? []);
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
  private eventService = inject(EventControllerService);
  private participationService = inject(EventParticipationControllerService);
  private reviewService = inject(ReviewControllerService);
  private userService = inject(UserControllerService);

  constructor() {
    this.userService.getCurrentUser().subscribe(user => {
      this.currentUser.set(user);
    });
    effect(() => {
      const id = this.id();
      this.eventService.getEvent(this.id()).subscribe(e => this.event.set(e));
      this.participationService.getAllParticipants(id, {page: 0, size: 20})
        .subscribe(p => this.participantsPage.set(p));
      this.reviewService.getAllReviewsForEvent(id, {page: 0, size: 20})
        .subscribe(p => this.reviewPage.set(p));
    });
  }

  toggleParticipation() {
    const eventId = this.id();
    if (eventId == null || this.joinButtonDisabled()) return;

    this.joinError.set(undefined);
    this.joinLoading.set(true);

    const action$ = this.isParticipating()
      ? this.participationService.leaveEvent(eventId)
      : this.participationService.joinEvent(eventId);

    action$.subscribe( {
      next: () => {
        this.joinLoading.set(false);
        this.refreshParticipants();
      },
      error: (err: HttpErrorResponse) => {
        this.joinLoading.set(false);
        this.joinError.set(this.extractErrorMessage(err));
      },
    });
  }
  private refreshParticipants() {
    this.participationService.getAllParticipants(this.id(), {page: 0, size: 20})
      .subscribe(p => this.participantsPage.set(p));
  }
  private extractErrorMessage(err: HttpErrorResponse): string {
    return  err?.error?.message ?? 'Something went wrong. Please try again.';
  }


}
