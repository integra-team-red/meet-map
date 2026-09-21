import {Component, inject, input, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {Dialog} from 'primeng/dialog';
import {Textarea} from 'primeng/textarea';
import {Message} from 'primeng/message';
import {HttpErrorResponse} from '@angular/common/http';
import {FlagControllerService} from '@app/api/api/flagController.service';

@Component({
  selector: 'app-report-event',
  imports: [FormsModule, Button, Dialog, Textarea, Message],
  templateUrl: './report-event.html',
})
export class ReportEvent {
  readonly eventId = input.required<number>();
  protected readonly reasonMaxLength = 255;
  protected dialogVisible = signal(false);
  protected reason = signal('');
  protected submitting = signal(false);
  protected error = signal<string | undefined>(undefined);
  protected reported = signal(false);
  private flagService = inject(FlagControllerService);

  protected openDialog(): void {
    this.reason.set('');
    this.error.set(undefined);
    this.dialogVisible.set(true);
  }

  protected submitReport(): void {
    const reason = this.reason().trim();
    if (this.submitting() || !reason) return;

    this.error.set(undefined);
    this.submitting.set(true);

    this.flagService.createFlag({eventId: this.eventId(), reason}).subscribe({
      next: () => {
        this.submitting.set(false);
        this.dialogVisible.set(false);
        this.reported.set(true);
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.error.set(err.error?.message ?? 'Something went wrong. Please try again.');
      },
    });
  }
}
