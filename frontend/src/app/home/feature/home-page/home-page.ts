import {AfterViewInit, Component, ElementRef, inject, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
import {EventDto} from '@app/api/model/eventDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {PageEventDto} from '@app/api/model/pageEventDto';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
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
import {PendingReviewModal} from '../pending-review/pending-review-modal';
import {Skeleton} from 'primeng/skeleton';

const PAGE_SIZE = 20;

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
    PendingReviewModal,
    Skeleton,
  ],
  templateUrl: './home-page.html',
})
export class HomePage implements OnInit, AfterViewInit, OnDestroy {
  eventService: EventControllerService = inject(EventControllerService);
  tagService: TagControllerService = inject(TagControllerService);
  router = inject(Router);
  protected fromLogin = signal(false);

  @ViewChild('sentinel') sentinelRef!: ElementRef<HTMLDivElement>;
  @ViewChild('scrollContainer') scrollContainerRef!: ElementRef<HTMLDivElement>;
  isLoading = signal(false);
  isLastPage = signal(false);
  private observer?: IntersectionObserver;
  private currentPage = signal(0);
  private route = inject(ActivatedRoute);


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
    this.searchEvents(true);
    this.getCities();
    this.getTags();
    this.fromLogin.set(this.route.snapshot.queryParamMap.has('from-login'));
  }

  ngAfterViewInit(): void {
    this.observer = new IntersectionObserver(
      (entries) => {
        if (entries.some(e => e.isIntersecting)) {
          this.loadNextPage();
        }
      },
      {root: this.scrollContainerRef.nativeElement, rootMargin: '200px'}
    );
    this.observer.observe(this.sentinelRef.nativeElement);
  }

  ngOnDestroy() {
    this.observer?.disconnect();
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

  protected searchEvents(reset: boolean = true) {
    if (reset) {
      this.currentPage.set(0);
      this.isLastPage.set(false);
      this.events.set([]);
    }
    this.fetchPage(this.currentPage());
  }

  protected loadNextPage() {
    if (this.isLoading() || this.isLastPage()) return;
    this.fetchPage(this.currentPage() + 1);
  }

  private fetchPage(page: number) {
    if (this.isLoading()) return;
    this.isLoading.set(true);

    const f = this.filters.value;

    this.eventService.getAllEvents(
      {page, size: PAGE_SIZE, sort: [this.sortOrder.value]},
      this.searchQuery() || undefined,
      f.city ?? undefined,
      f.tags?.length ? f.tags : undefined,
      f.age?.[0],
      f.age?.[1],
      f.dateRange?.[0] ? this.toLocalDate(f.dateRange[0]) : undefined,
      f.dateRange?.[1] ? this.toLocalDate(f.dateRange[1]) : undefined,
      undefined,
      'ACTIVE'
    ).subscribe({
      next: (response: PageEventDto) => {
        const newContent = response.content ?? [];
        if (page === 0) {
          this.events.set(newContent);
        } else {
          const existingIds = new Set(this.events().map(e => e.id));
          this.events.set([...this.events(), ...newContent.filter(e => !existingIds.has(e.id))]);
        }
        this.currentPage.set(page);
        this.isLastPage.set(response.last ?? newContent.length < PAGE_SIZE);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
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
