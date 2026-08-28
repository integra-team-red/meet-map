import {Component, OnInit} from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import {Avatar} from 'primeng/avatar';
import {FormsModule} from '@angular/forms';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {ThemePicker} from '../theme-picker/theme-picker';

@Component({
  selector: 'app-navigation-bar',
  templateUrl: './navigation-bar.html',
  imports: [MenubarModule, Avatar, RouterLink, RouterLinkActive, FormsModule, ThemePicker],
})
export class NavigationBar implements OnInit {
  items: MenuItem[] | undefined;
  mobileItems: MenuItem[] | undefined;

  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        routerLink: '/home'
      },
      {
        label: 'Create Event',
        routerLink: '/events/create',
      },
      {
        label: 'Admin Panel',
        routerLink: '/admin'
      }
    ];
    this.mobileItems = [
      {
        label: 'Home',
        routerLink: '/home'
      },
/*      {
        label: 'Map',
      },*/
      {
        label: 'Create Event',
        routerLink: '/events/create'
      },
      {
        label: 'Admin Panel',
        routerLink: '/admin'
      }
    ];
    }
}
