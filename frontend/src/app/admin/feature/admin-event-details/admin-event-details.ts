import {Component, input, linkedSignal, output} from '@angular/core';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {FloatLabel} from 'primeng/floatlabel';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Slider} from 'primeng/slider';
import {Textarea} from 'primeng/textarea';
import {EventDto} from '@app/api/model/eventDto';
import {InputText} from 'primeng/inputtext';
import {InputNumber} from 'primeng/inputnumber';
import {DatePicker} from 'primeng/datepicker';
import {ScrollPanel} from 'primeng/scrollpanel';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {ConfirmationService} from 'primeng/api';

@Component({
  selector: 'app-admin-event-details',
  imports: [
    Button,
    Card,
    FloatLabel,
    ReactiveFormsModule,
    Slider,
    Textarea,
    FormsModule,
    InputText,
    InputNumber,
    DatePicker,
    ScrollPanel,
    ConfirmDialogModule,
  ],
  providers: [ConfirmationService],
  templateUrl: './admin-event-details.html',
})
export class AdminEventDetails{
  inputEvent = input.required<EventDto|null>();

  onEventDelete = output<EventDto>();
  onEventUpdate = output<EventDto>();
  onEventDeselect = output<void>();

  constructor(private confirmationService: ConfirmationService) {}

  event = linkedSignal<EventDto|null>(() => this.inputEvent()!);
  eventAgeRange = linkedSignal(() => [
    this.event()!.minAge!,
    this.event()!.maxAge!
  ])
  eventDate = linkedSignal(() => new Date(this.event()!.dateTime!))

  setEventAgeRange(range: number[]) {
    this.event.update((e) => {
      if(e) {
        e.minAge = range[0];
        e.maxAge = range[1];
      }
      return e;
    })
  }

  setEventDate(date: Date) {
    this.event.update((e) => {
      if(e) e.dateTime = date.toISOString()
      return e;
    })
  }

  confirmCancel() {
    this.confirmationService.confirm({
      message: "Are you sure you want to cancel this event?",
      header: "Canceling confirmation",
      icon: 'pi pi-exclamation-triangle',
      rejectButtonProps: {
        label: "Go back",
        severity: 'primary',
        outlined: true
      },
      acceptButtonProps: {
        label: "Cancel",
        severity: 'danger',
        outlined: true
      },
      accept: () => {
        this.deleteEvent();
      },
      reject: () => {
      }
    });
  }

  protected updateEvent() {
    this.onEventUpdate.emit(this.event()!);
  }

  protected deleteEvent() {
    this.onEventDelete.emit(this.event()!);
  }

  protected deselectEvent() {
    this.onEventDeselect.emit();
  }
}
