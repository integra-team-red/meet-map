import {Component, inject, OnInit, signal} from '@angular/core';
import {ButtonModule} from 'primeng/button';
import {AvatarModule} from 'primeng/avatar';

import {Router} from '@angular/router';
import {UserControllerService} from '@app/api/api/userController.service';
import {UserDto} from '@app/api/model/userDto';


@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  imports: [ButtonModule, AvatarModule],
})
export class ProfilePage implements OnInit {
  private readonly api = inject(UserControllerService);
  private readonly router = inject(Router);

  //Placeholder until Matrix communication is implemented
  protected readonly matrixId = 'ianis67skibidi@matrix.meet-map.ro';

  protected readonly user = signal<UserDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.getCurrentUser().subscribe({
      next: (user) => {
        this.user.set(user);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Could not load your profile. Please try again.');
        this.loading.set(false);
      },
    });
  }
}

