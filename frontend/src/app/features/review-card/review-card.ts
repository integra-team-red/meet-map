import {Component, input} from '@angular/core';
import {Avatar} from 'primeng/avatar';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {Card} from 'primeng/card';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-review-card',
  imports: [Avatar, Rating, FormsModule, Card, DatePipe],
  templateUrl: './review-card.html',
})
export class ReviewCard {
  readonly rating = input.required<number>();
  readonly comment = input<string>();
  readonly date = input<string>();
  readonly authorName = input<string>();
  readonly userImagePlaceholderURL = input<string>('https://placehold.net/avatar.png');
}
