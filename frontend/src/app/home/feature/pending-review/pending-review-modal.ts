import {Component, computed, inject, signal} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {FormsModule} from '@angular/forms';
import {Dialog} from 'primeng/dialog';
import {Rating} from 'primeng/rating';
import {Textarea} from 'primeng/textarea';
import {Button} from 'primeng/button';
import {UserControllerService} from '@app/api/api/userController.service';
import {ReviewControllerService} from '@app/api/api/reviewController.service';

@Component({
  selector: 'app-pending-review-modal',
  imports: [FormsModule, Dialog, Rating, Textarea, Button],
  templateUrl: './pending-review-modal.html',
})
export class PendingReviewModal {
  protected readonly visible = computed(() => !!this.pendingReviewSource.value());
  protected rating = signal<number>(0);
  protected comment = signal<string>('');
  protected submitting = signal(false);
  private readonly userService = inject(UserControllerService);
  protected readonly pendingReviewSource = rxResource({
    stream: () => this.userService.getPendingReviews(),
  });
  private readonly reviewService = inject(ReviewControllerService);

  protected submit(): void {
    const pending = this.pendingReviewSource.value();

    if (!pending || this.rating() === 0) {
      return;
    }

    this.submitting.set(true);

    this.reviewService.createReview({
      eventId: pending.event!.id!,
      rating: this.rating(),
      comment: this.comment(),
    } as any).subscribe({
      next: () => {
        this.submitting.set(false);
        this.pendingReviewSource.set(undefined);
      },
      error: () => this.submitting.set(false),
    });
  }

  protected dismiss(): void {
    const pending = this.pendingReviewSource.value();

    if (!pending) {
      return;
    }

    this.userService.dismissPendingReview(pending.event!.id!).subscribe(() => {
      this.pendingReviewSource.set(undefined);
    });
  }
}
