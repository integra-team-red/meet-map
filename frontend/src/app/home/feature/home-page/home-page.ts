import {Component, inject, OnInit, signal} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {EventCard} from '../../../shared/ui/event-card/event-card';
import {Card} from 'primeng/card';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {Select} from 'primeng/select';
import {Slider} from 'primeng/slider';
import {DatePicker} from 'primeng/datepicker';
import {TagDto} from '@app/api/model/tagDto';
import {MultiSelect} from 'primeng/multiselect';
import {Button} from 'primeng/button';
import {TagControllerService} from '@app/api/api/tagController.service';


@Component({
  selector: 'app-home-page',
  imports: [
    EventCard,
    FormsModule,
    IconField,
    InputIcon,
    InputText,
    Card,
    Accordion,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    Select,
    Slider,
    DatePicker,
    MultiSelect,
    Button,
    ReactiveFormsModule,
  ],
  templateUrl: './home-page.html',
})
export class HomePage implements OnInit {
  eventService: EventControllerService = inject(EventControllerService);
  tagService: TagControllerService = inject(TagControllerService);
  router = inject(Router);

  protected filters = new FormGroup({
    city: new FormControl<string | null>(null),
    tags: new FormControl<number[]>([]),
    age: new FormControl<number[]>([0, 100]),
    dateRange: new FormControl<Date[] | null>(null),
  });

  accordionValue = signal<string | null>('0'); //so I can hide the calendar properly

  protected options = [
    {name: 'Newest', value: 'createdAt,desc'},
    {name: 'Oldest', value: 'createdAt,asc'},
  ];
  sortOrder: { name: string, value: string } = this.options[0]

  events = signal<EventDto[]>([])
  cities = signal<string[]>([])
  tags = signal<TagDto[]>([])

  ngOnInit(): void {
    this.searchEvents();
    this.getCities();
    this.getTags();
  }

  protected applyFilters() {
    this.searchEvents();
  }

  protected clearFilters() {
    this.filters.reset({city: null, tags: [], age: [0, 100], dateRange: null});
    this.searchEvents()
  }

  protected getCities() {
    this.eventService.getCities().subscribe((response: string[]) => {
      this.cities.set(response)
    })
  }

  protected getTags() {
    this.tagService.getAllTags().subscribe((response: TagDto[]) => {
      this.tags.set(response)
    })
  }

  protected searchEvents() {
    const f = this.filters.value;

    this.eventService.getAllEvents(
      {page: 0, size: 20, sort: [this.sortOrder.value]},
      this.searchQuery() || undefined,
      f.city ?? undefined,
      f.tags?.length ? f.tags : undefined,
      f.age?.[0],
      f.age?.[1],
      f.dateRange?.[0] ? this.toLocalDate(f.dateRange[0]) : undefined,
      f.dateRange?.[1] ? this.toLocalDate(f.dateRange[1]) : undefined,
    ).subscribe((response: PageEventDto) => {
      this.events.set(response.content!);
    });
  }

  searchQuery = signal<string>('');

  onSearchUpdated(sq: string) {
    this.searchQuery.set(sq);
    this.searchEvents();
  }

  openEvent(eventId: number): void {
    this.router.navigate([`/events/${eventId}`])
  }

  private toLocalDate(d: Date): string {
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  }

}
