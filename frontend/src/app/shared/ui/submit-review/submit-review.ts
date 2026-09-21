import {Component, computed, effect, inject, input, output, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {Textarea} from 'primeng/textarea';
import {HttpErrorResponse} from '@angular/common/http';
import {ReviewControllerService} from '@app/api/api/reviewController.service';
import {ToastNotificationService} from '../toast-notification-service/toast-notification-service';

@Component({
  selector: 'app-submit-review',
  imports: [Button, Card, Rating, Textarea, FormsModule],
  templateUrl: './submit-review.html',
})
export class SubmitReview {
  readonly eventId = input.required<number>();
  readonly reviewId = input<number | undefined>(undefined);
  readonly submitted = output<void>();
  protected readonly commentMaxLength = 100;
  protected reviewRating = signal(0);
  protected reviewComment = signal('');
  protected reviewSubmitting = signal(false);
  protected reviewError = signal<string | undefined>(undefined);
  private reviewService = inject(ReviewControllerService);
  readonly initialRating = input(0);
  readonly initialComment = input('');
  readonly cancelled = output<void>();

  protected readonly isEdit = computed(() => this.reviewId() != null);


  constructor(private toastNotificationService: ToastNotificationService) {
    effect(() => {
      this.eventId();
      this.reviewId();
      const rating = this.initialRating();
      const comment = this.initialComment();
      this.reviewRating.set(rating);
      this.reviewComment.set(comment);
      this.reviewError.set(undefined);
    });
  }

  submitReview() {
    if (this.reviewSubmitting() || this.reviewRating() === 0) return;

    const reviewId = this.reviewId();
    const comment = this.reviewComment().trim();
    const body = {
      rating: this.reviewRating(),
      comment: comment || undefined
    };

    this.reviewError.set(undefined);
    this.reviewSubmitting.set(true);

    const request$ = reviewId == null
      ? this.reviewService.createReview(this.eventId(), body)
      : this.reviewService.updateReview(this.eventId(), reviewId, body);

    request$.subscribe({
      next: () => {
        this.reviewSubmitting.set(false);
        this.submitted.emit();
        if(reviewId == null) {
          this.reviewRating.set(0);
          this.reviewComment.set('');
        }
        this.reviewRating.set(0);
        this.reviewComment.set('');
        this.toastNotificationService.showSuccess("Your review has been posted.");
      }, error: (err: HttpErrorResponse) => {
        this.reviewSubmitting.set(false);
        if ( reviewId == null && err.status === 409) {
          this.submitted.emit();
        }
        this.reviewError.set(err.error.message);
        this.toastNotificationService.showError("An error occurred. Please try again.");
      },
    });
  }
}
