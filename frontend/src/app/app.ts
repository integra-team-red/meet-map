import {Component, inject, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavigationBar} from './shared/components/navigation-bar/navigation-bar';
import {AuthService} from './core/auth/auth-service';
import {Toast} from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationBar, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
  readonly auth = inject(AuthService);
}
