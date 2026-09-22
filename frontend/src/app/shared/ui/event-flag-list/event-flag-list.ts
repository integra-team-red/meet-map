import {Component, input} from '@angular/core';
import {FlagDto} from '@app/api/model/flagDto';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';

@Component({
  selector: 'app-event-flag-list',
  imports: [
    AccordionPanel,
    AccordionHeader,
    Accordion,
    AccordionContent
  ],
  templateUrl: './event-flag-list.html',
})
export class EventFlagList {
  readonly flags = input<FlagDto[]>([]);
}
