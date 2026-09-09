import {Component, effect, inject, input, output, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {Textarea} from 'primeng/textarea';
import {HttpErrorResponse} from '@angular/common/http';
import {ReviewControllerService} from '@app/api/api/reviewController.service';

@Component({
  selector: 'app-submit-review',
  imports: [Button, Card, Rating, Textarea, FormsModule],
  templateUrl: './submit-review.html',
})
export class SubmitReview {
  readonly eventId = input.required<number>();
  readonly submitted = output<void>();
  protected readonly commentMaxLength = 100;
  protected reviewRating = signal(0);
  protected reviewComment = signal('');
  protected reviewSubmitting = signal(false);
  protected reviewError = signal<string | undefined>(undefined);
  private reviewService = inject(ReviewControllerService);

  constructor() {
    effect(() => {
      this.eventId();
      this.reviewError.set(undefined);
      this.reviewRating.set(0);
      this.reviewComment.set('');
    });
  }

  submitReview() {
    if (this.reviewSubmitting() || this.reviewRating() === 0) return;

    const comment = this.reviewComment().trim();
    this.reviewError.set(undefined);
    this.reviewSubmitting.set(true);

    this.reviewService.createReview({
      eventId: this.eventId(),
      rating: this.reviewRating(),
      comment: comment || undefined
    }).subscribe({
      next: () => {
        this.reviewSubmitting.set(false);
        this.submitted.emit();
        this.reviewRating.set(0);
        this.reviewComment.set('');
      }, error: (err: HttpErrorResponse) => {
        this.reviewSubmitting.set(false);
        if (err.status === 409) {
          this.submitted.emit();
        }
        this.reviewError.set(err.error.message);
      },
    });
  }
}
