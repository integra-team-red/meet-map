import {Component, OnInit, signal} from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import {Avatar} from 'primeng/avatar';
import {storedThemeName, THEME_STORAGE_KEY, ThemeName, THEMES} from '../../../theme/theme';
import {usePreset} from '@primeuix/themes';
import {Select} from 'primeng/select';
import {FormsModule} from '@angular/forms';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-navigation-bar',
  templateUrl: './navigation-bar.html',
  imports: [MenubarModule, Avatar, RouterLink, RouterLinkActive, Select, FormsModule],
})
export class NavigationBar implements OnInit {
  items: MenuItem[] | undefined;
  mobileItems: MenuItem[] | undefined;

  protected readonly theme = signal<ThemeName>(storedThemeName());
  protected readonly themeOptions = Object.entries(THEMES).map(([value, t]) => ({label: t.label, value: value as ThemeName}));

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
  setTheme(name: ThemeName): void {
    usePreset(THEMES[name].preset);
    localStorage.setItem(THEME_STORAGE_KEY, name);
    this.theme.set(name);
  }

}
