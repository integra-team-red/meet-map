import {Component, computed, input} from '@angular/core';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-star-rating', imports: [Rating, FormsModule], templateUrl: './star-rating.html',
})
export class StarRating {
  readonly averageRating = input<number | undefined>();
  readonly reviewsCount = input<number | undefined>();
  starRating = computed(() => Math.round(this.averageRating() ?? 0));
}
