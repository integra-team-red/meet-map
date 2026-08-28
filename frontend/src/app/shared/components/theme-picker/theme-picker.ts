import {Component, signal} from '@angular/core';
import {storedThemeName, THEME_STORAGE_KEY, ThemeName, THEMES} from '../../../theme/theme';
import {usePreset} from '@primeuix/themes';
import {Select} from 'primeng/select';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-theme-picker',
  templateUrl: './theme-picker.html',
  imports: [
    Select,
    FormsModule
  ],
})
export class ThemePicker {

  protected readonly theme = signal<ThemeName>(storedThemeName());
  protected readonly themeOptions = Object.entries(THEMES).map(([value, t]) => ({label: t.label, value: value as ThemeName}));

  setTheme(name: ThemeName): void {
    usePreset(THEMES[name].preset);
    localStorage.setItem(THEME_STORAGE_KEY, name);
    this.theme.set(name);
  }
}
