import {Component, inject, OnInit, signal} from '@angular/core';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {Card} from 'primeng/card';
import {EventCard} from '../../../shared/ui/event-card/event-card';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {Select} from 'primeng/select';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home-page',
  imports: [
    IconField,
    InputIcon,
    InputText,
    Card,
    EventCard,
    Accordion,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    Select,
    FormsModule
  ],
  templateUrl: './home-page.html',
})
export class HomePage implements OnInit {
  eventService: EventControllerService = inject(EventControllerService);
  router = inject(Router);

  protected options = [
    { name: 'Newest', value: 'asc' },
    { name: 'Oldest', value: 'desc' },
  ];

  events = signal<EventDto[]>([])
  sortOrder: { name: string, value: string } = this.options[0]

  ngOnInit(): void {
    this.eventService.getAllEvents({
      page: 0,
      size: 10,
      sort: [this.sortOrder.value]
    }).subscribe((response: PageEventDto) => {
      console.log(response)
      this.events.set(response.content!)
      console.log(response.content)
    })
  }

  doSomething(eventId: number): void {
    console.log("clicked", eventId);
    this.router.navigate([`/events/${eventId}`])
  }
}
