import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-landing-page',
  imports: [],
  templateUrl: './landing-page.html',
})

export class LandingPage{
  constructor(private router: Router) {}

  onSignUp() {
    this.router.navigate(['/signup'])
  }
  onLogIn() {
    this.router.navigate(['/login'])
  }
}
