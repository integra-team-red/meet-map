import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {ButtonModule} from 'primeng/button';
import {AvatarModule} from 'primeng/avatar';
import {DialogModule} from 'primeng/dialog';
import {ChipModule} from 'primeng/chip';
import {ScrollPanel} from 'primeng/scrollpanel';
import {Paginator, PaginatorState} from 'primeng/paginator';

import {Router} from '@angular/router';
import {UserControllerService} from '@app/api/api/userController.service';
import {UserDto} from '@app/api/model/userDto';
import {TagDto} from '@app/api/model/tagDto';
import {TagControllerService} from '@app/api/api/tagController.service';
import {UpdateUserTagsDto} from '@app/api/model/updateUserTagsDto';
import {EventControllerService} from '@app/api/api/eventController.service';
import {EventDto} from '@app/api/model/eventDto';
import {EventCard} from '../../../shared/ui/event-card/event-card';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  imports: [
    ButtonModule,
    AvatarModule,
    ChipModule,
    DialogModule,
    ScrollPanel,
    EventCard,
    Paginator,
  ],
})
export class ProfilePage implements OnInit {
  private readonly userApi = inject(UserControllerService);
  private readonly tagApi = inject(TagControllerService);
  private readonly eventApi = inject(EventControllerService);
  private readonly router = inject(Router);

  // Placeholder until Matrix communication is implemented
  protected readonly matrixId = 'ianis67skibidi@matrix.meet-map.ro';

  protected readonly user = signal<UserDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly assignedTags = signal<TagDto[]>([]);
  protected readonly availableTags = signal<TagDto[]>([]);

  protected dialogVisible = signal(false);
  protected readonly draftTagIds = signal<number[]>([]);
  protected readonly saving = signal(false);

  protected readonly sortedTags = computed(() =>
    [...this.availableTags()].sort((a, b) =>
      (a.name ?? '').localeCompare(b.name ?? '')
    )
  );
  protected readonly sortedAssignedTags = computed(() =>
    [...this.assignedTags()].sort((a, b) =>
      (a.name ?? '').localeCompare(b.name ?? '')
    )
  );

  protected readonly createdEvents = signal<EventDto[]>([]);
  protected readonly createdPage = signal(0);
  protected readonly createdRows = signal(10);
  protected readonly createdTotal = signal(0);

  ngOnInit(): void {
    this.userApi.getCurrentUser().subscribe({
      next: (user) => {
        this.user.set(user);
        this.loading.set(false);
        this.getCreated(user.id!);
        this.getJoined(user.id!);
      },
      error: () => {
        this.error.set('Could not load your profile. Please try again.');
        this.loading.set(false);
      },
    });

    this.userApi.getTags().subscribe({
      next: (tags) => {
        this.assignedTags.set(tags);
      },
    });

    this.tagApi.getAllTags().subscribe({
      next: (tags) => {
        this.availableTags.set(tags);
      },
    });
  }

  protected createPressed(): void {
    this.router.navigate([`events/create`]);
  }

  protected getCreated(id: number, page = this.createdPage(), rows = this.createdRows()): void {
    this.createdPage.set(page);
    this.createdRows.set(rows);
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

  protected changeCreatedPage(event: PaginatorState): void {
    this.getCreated(this.user()!.id!, event.page, event.rows);
  }

  protected openTagDialog(): void {
    this.draftTagIds.set(
      this.assignedTags()
        .map(tag => tag.id)
        .filter((id): id is number => id !== undefined)
    );

    this.dialogVisible.set(true);
  }

  protected toggleTag(tag: TagDto): void {
    if (tag.id === undefined) {
      return;
    }

    const currentIds = this.draftTagIds();

    if (currentIds.includes(tag.id)) {
      this.draftTagIds.set(
        currentIds.filter(id => id !== tag.id)
      );
    } else {
      this.draftTagIds.set([
        ...currentIds,
        tag.id,
      ]);
    }
  }

  protected isTagSelected(tag: TagDto): boolean {
    return tag.id !== undefined &&
      this.draftTagIds().includes(tag.id);
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

  protected cancelTagDialog(): void {
    this.dialogVisible.set(false);
  }

  protected saveTags(): void {
    const dto: UpdateUserTagsDto = {
      tagIds: this.draftTagIds(),
    };

    this.saving.set(true);

    this.userApi.updateTags(dto).subscribe({
      next: (tags) => {
        this.assignedTags.set(tags);
        this.dialogVisible.set(false);
        this.saving.set(false);
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Could not update your tags. Please try again.');
      },
    });
  }
}
