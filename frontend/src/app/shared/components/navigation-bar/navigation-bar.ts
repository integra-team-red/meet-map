import { Component, OnInit } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import {Avatar} from 'primeng/avatar';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-navigation-bar',
  templateUrl: './navigation-bar.html',
  imports: [MenubarModule, Avatar, RouterLink]
})
export class NavigationBar implements OnInit {
  items: MenuItem[] | undefined;
  mobileItems: MenuItem[] | undefined;
  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        routerLink: '/'
      },
      {
        label: 'Create Event',
      }
    ];
    this.mobileItems = [
      {
        label: 'Home',
        routerLink: '/'
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
