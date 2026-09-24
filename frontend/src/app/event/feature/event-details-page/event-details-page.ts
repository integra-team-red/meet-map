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
import {SubmitReview} from '../../../shared/ui/submit-review/submit-review';
import {ReviewDto} from '@app/api/model/reviewDto';
import {ConfirmationService} from 'primeng/api';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {ToastNotificationService} from '../../../shared/ui/toast-notification-service/toast-notification-service';

@Component({
  selector: 'app-event-details-page',
  imports: [DatePipe, ReviewCard, Tag, ParticipantsCard, Button, TitleCasePipe, StarRating, Message, SubmitReview, ConfirmDialog],
  providers: [ConfirmationService],
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
  currentUserId = computed(() => this.currentUser()?.id);

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

  canJoinStatus = computed(() => {
    const status = this.event()?.status;
    return status !== EventDto.StatusEnum.Cancelled && status != EventDto.StatusEnum.Completed;
  })

  joinButtonDisabled = computed(() => this.joinLoading() || !this.canJoinStatus() || (!this.isParticipating() && this.isFull()));

  joinButtonLabel = computed(() => {
    if (this.joinLoading()) return this.isParticipating() ? 'Leaving...' : 'Joining...';
    if (this.isParticipating()) return 'Leave Event';
    if (!this.canJoinStatus()) return 'Event unavailable';
    if (this.isFull()) return 'Event is full';
    return 'Join Event';
  })

  joinButtonSeverity = computed<'success' | 'danger'>(() => this.isParticipating() ? 'danger' : 'success');

  alreadyReviewed = signal(false);

  protected editingReviewId = signal<number | undefined>(undefined);
  protected reviewActionError = signal<string | undefined>(undefined);
  protected readonly ownReviewId = computed(() => {
    const currentUserId = this.currentUserId();
    if (currentUserId == null) return undefined;
    return this.reviews().find(r => r.userId === currentUserId)?.id;
  });

  isCompleted = computed(() => this.event()?.status === EventDto.StatusEnum.Completed);
  canReview = computed(() => this.isCompleted() && this.isParticipating() && !this.hasReviewed());
  reviewPage = signal<PageReviewDto | undefined>(undefined);
  reviews = computed(() => this.reviewPage()?.content ?? []);
  hasReviewed = computed(() => {
    if (this.alreadyReviewed()) return true;
    const currentUserId = this.currentUserId();
    return currentUserId != null && this.reviews().some(r => r.userId === currentUserId);
  });
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
  private confirmationService = inject(ConfirmationService);

  constructor(private toastNotificationService: ToastNotificationService) {
    this.userService.getCurrentUser().subscribe(user => {
      this.currentUser.set(user);
    });
    effect(() => {
      const id = this.id();
      this.alreadyReviewed.set(false);
      this.eventService.getEvent(this.id()).subscribe(e => this.event.set(e));
      this.participationService.getAllParticipants(id, {page: 0, size: 20})
        .subscribe(p => this.participantsPage.set(p));
      this.reviewService.getAllReviewsForEvent(id, {page: 0, size: 20})
        .subscribe(p => this.reviewPage.set(p));
      this.editingReviewId.set(undefined);
      this.reviewActionError.set(undefined);
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

    action$.subscribe({
      next: () => {
        this.joinLoading.set(false);
        this.refreshParticipants();

        if(this.isParticipating()) {
          this.toastNotificationService.showSuccess("You have left the event successfully.");
        } else {
          this.toastNotificationService.showSuccess("You have joined the event successfully.");
        }
      }, error: (err: HttpErrorResponse) => {
        this.joinLoading.set(false);
        this.joinError.set(this.extractErrorMessage(err));
        this.toastNotificationService.showError(this.extractErrorMessage(err));
      },
    });
  }

  protected onReviewSubmitted(): void {
    this.alreadyReviewed.set(true);
    this.refreshReviews();
  }

  private refreshReviews() {
    this.reviewService.getAllReviewsForEvent(this.id(), {page: 0, size: 20})
      .subscribe(p => this.reviewPage.set(p));
    this.eventService.getEvent(this.id()).subscribe(e => this.event.set(e));
  }

  private refreshParticipants() {
    this.participationService.getAllParticipants(this.id(), {page: 0, size: 20})
      .subscribe(p => this.participantsPage.set(p));
  }

  private extractErrorMessage(err: HttpErrorResponse): string {
    return err?.error?.message ?? 'Something went wrong. Please try again.';
  }

  protected startEdit(review: ReviewDto) {
    this.reviewActionError.set(undefined);
    this.editingReviewId.set(review.id);
  }

  protected cancelEdit() {
    this.editingReviewId.set(undefined);
  }

  protected onReviewUpdated() {
    this.editingReviewId.set(undefined);
    this.refreshReviews();
  }

  protected confirmDelete(review: ReviewDto) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete your review?',
      header: 'Delete review',
      icon: 'pi pi-exclamation-triangle',
      rejectButtonProps: {label: 'Go back', severity: 'primary', outlined: true},
      acceptButtonProps: {label: 'Delete', severity: 'danger', outlined: true},
      accept: () => this.deleteReview(review),
    });
  }

  private deleteReview(review: ReviewDto) {
    const reviewId = review.id;
    if (reviewId == null) return;

    this.reviewActionError.set(undefined);
    this.reviewService.deleteReview(this.id(), reviewId).subscribe({
      next: () => {
        this.editingReviewId.set(undefined);
        this.alreadyReviewed.set(false);
        this.refreshReviews();
      },
      error: (err: HttpErrorResponse) => this.reviewActionError.set(this.extractErrorMessage(err)),
    });
  }


}
