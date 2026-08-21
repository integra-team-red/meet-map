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
        this.getJoined(user.id!);
      },
      error: (err) => {
        this.error.set('Could not load your profile. Please try again.');
        this.loading.set(false);
      },
    });
  }

  protected readonly joinedEvents = signal<EventDto[]>([]);
  joinedPage = signal(0)
  joinedRows = signal(10)
  joinedTotal = signal(0)

  protected getJoined(id: number, page = this.joinedPage(), rows = this.joinedRows()) {
    this.joinedPage.set(page)
    this.joinedRows.set(rows)
    this.userApi.getJoinedEvents({page:page, size:rows}, id)
      .subscribe((page) => {
        this.joinedEvents.set(page.content!);
        this.joinedTotal.set(page.totalElements!);
      });
  }

  protected changeJoinedPage(event: PaginatorState) {
    this.getJoined(this.user()!.id!, event.page, event.rows)
  }
}

