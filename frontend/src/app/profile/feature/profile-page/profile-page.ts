import {Component, inject, OnInit, signal} from '@angular/core';
import {ButtonModule} from 'primeng/button';
import {AvatarModule} from 'primeng/avatar';

import {Router} from '@angular/router';
import {UserControllerService} from '@app/api/api/userController.service';
import {UserDto} from '@app/api/model/userDto';
import {ScrollPanel} from 'primeng/scrollpanel';
import {EventCard} from '../../../shared/ui/event-card/event-card';
import {EventControllerService} from '@app/api/api/eventController.service';
import {EventDto} from '@app/api/model/eventDto';
import {Paginator, PaginatorState} from 'primeng/paginator';


@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  imports: [ButtonModule, AvatarModule, ScrollPanel, EventCard, Paginator],
})
export class ProfilePage implements OnInit {
  private readonly userApi = inject(UserControllerService);
  private readonly eventApi = inject(EventControllerService);
  private readonly router = inject(Router);

  //Placeholder until Matrix communication is implemented
  protected readonly matrixId = 'ianis67skibidi@matrix.meet-map.ro';

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly user = signal<UserDto | null>(null);

  ngOnInit(): void {
    this.userApi.getCurrentUser().subscribe({
      next: (user) => {
        this.user.set(user);
        this.loading.set(false);
        this.getCreated(user.id!);
      },
      error: (err) => {
        this.error.set('Could not load your profile. Please try again.');
        this.loading.set(false);
      },
    })
  }

  protected createPressed() {
    this.router.navigate([`events/create`]);
  }

  protected readonly createdEvents = signal<EventDto[]>([]);
  createdPage = signal(0)
  createdRows = signal(10)
  createdTotal = signal(0)

  protected getCreated(id: number, page = this.createdPage(), rows = this.createdRows()) {
    this.createdPage.set(page)
    this.createdRows.set(rows)
    this.eventApi.getAllEvents(
      {page: page, size: rows},
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      id)
      .subscribe((page) => {
        this.createdEvents.set(page.content!);
        this.createdTotal.set(page.totalElements!);
      });
  }

  protected changeCreatedPage(event: PaginatorState) {
    this.getCreated(this.user()!.id!, event.page, event.rows)
  }
}

