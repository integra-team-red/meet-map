import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {TextareaModule} from 'primeng/textarea';
import {DatePickerModule} from 'primeng/datepicker';
import {InputNumberModule} from 'primeng/inputnumber';
import {MultiSelectModule} from 'primeng/multiselect';
import {CreateEventDto, EventControllerService, TagControllerService, TagDto} from '../../../../typescript-client';
import {ToastNotificationService} from '../../shared/ui/toast-notification-service/toast-notification-service';

@Component({
  selector: 'app-event-create',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    TextareaModule,
    DatePickerModule,
    InputNumberModule,
    MultiSelectModule,
  ],
  templateUrl: './event-create.html',
})
export class EventCreateComponent implements OnInit {
  readonly today = new Date();
  readonly serverErrors = signal<string[]>([]);
  readonly tags = signal<TagDto[]>([]);
  readonly submitting = signal(false);
  private fb = inject(FormBuilder);
  eventForm = this.fb.group(
    {
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      dateTime: [null as Date | null, Validators.required],
      maxParticipants: [null as number | null, Validators.min(1)],
      minAge: [null as number | null, Validators.min(0)],
      maxAge: [null as number | null, Validators.min(0)],
      tagIds: [[] as number[]],
    },
  );
  private eventApi = inject(EventControllerService);
  private tagApi = inject(TagControllerService);

  constructor(private toastNotification: ToastNotificationService) {}

  ngOnInit() {
    this.tagApi.getAllTags().subscribe((tags) => this.tags.set(tags));
  }

  onSubmit() {
    this.serverErrors.set([]);
    if (this.eventForm.invalid) {
      this.eventForm.markAllAsTouched();
      return;
    }

    const form = this.eventForm.getRawValue();
    const tagIds = form.tagIds ?? [];

    const payload: CreateEventDto = {
      title: form.title!,
      description: form.description!,
      address: form.address!,
      city: form.city!,
      dateTime: form.dateTime!.toISOString(),
      maxParticipants: form.maxParticipants ?? undefined,
      minAge: form.minAge ?? undefined,
      maxAge: form.maxAge ?? undefined,
      // TODO: put the actual user's id who creates the event
      creatorId: 1,
      tagIds: (tagIds.length ? tagIds : undefined) as unknown as Set<number> | undefined,
    };

    this.submitting.set(true);
    this.eventApi.createEvent(payload).subscribe({
      next: (created) => {
        console.log('NEXT fired', created);
        this.submitting.set(false);
        console.log('Created event', created);
        this.eventForm.reset({tagIds: []});
        this.toastNotification.showSuccess("The Event has been successfully created.")
      },
      error: (err) => {
        console.log('ERROR fired', err);
        this.submitting.set(false);
        this.applyServerErrors(err);
        this.toastNotification.showError("An error occurred, please try again.")
      },
    });
  }

  private applyServerErrors(err: unknown) {
    const body = (err as { error?:
        { fieldErrors?: { field?: string; defaultMessage?: string }[] } })?.error;
    const fieldErrors = body?.fieldErrors;

    if (!fieldErrors?.length) {
      this.serverErrors.set(['Could not create the event. Please try again.']);
      return;
    }

    const formErrors: string[] = [];
    for (const fe of fieldErrors) {
      const message = fe.defaultMessage ?? 'Invalid value.';
      const control = fe.field ? this.eventForm.get(fe.field) : null;
      if (control) {
        control.setErrors({...(control.errors ?? {}), server: message});
        control.markAsTouched();
      } else {
        formErrors.push(message);
      }
    }
    this.serverErrors.set(formErrors);
  }
}
