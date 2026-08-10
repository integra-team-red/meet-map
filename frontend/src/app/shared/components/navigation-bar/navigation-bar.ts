import { Component, OnInit } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import {Avatar} from 'primeng/avatar';

@Component({
  selector: 'app-navigation-bar',
  templateUrl: './navigation-bar.html',
  imports: [MenubarModule, Avatar]
})
export class NavigationBar implements OnInit {
  items: MenuItem[] | undefined;
  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        routerLink: '/'
      },
      {
        label: 'Events',
      },
      {
        label: 'Map',
      },
      {
        label: 'Create Event',
      }
    ];
  }
}
