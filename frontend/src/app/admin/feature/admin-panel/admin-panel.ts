import {Component} from '@angular/core';
import {ScrollPanelModule} from 'primeng/scrollpanel'
import {FormsModule} from '@angular/forms';
import {AdminEventList} from '../admin-event-list/admin-event-list';

@Component({
  selector: 'app-admin-panel',
  imports: [
    ScrollPanelModule,
    FormsModule,
    AdminEventList
  ],
  templateUrl: './admin-panel.html',
})
export class AdminPanel {
}
