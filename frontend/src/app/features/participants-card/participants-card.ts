import {Component, computed, input} from '@angular/core';
import {Avatar} from 'primeng/avatar';
import {Card} from 'primeng/card';
import {ScrollPanel} from 'primeng/scrollpanel';
import {EventParticipationDto} from '@app/api/model/eventParticipationDto';

@Component({
  selector: 'app-participants-card',
  imports: [Avatar, Card, ScrollPanel],
  templateUrl: './participants-card.html',
})
export class ParticipantsCard {
  readonly participants = input<EventParticipationDto[]>([]);
  readonly totalParticipants = input<number>();
  readonly maxParticipants = input<number>();
  readonly userImagePlaceholderURL = input<string>('https://placehold.net/avatar.png');
  readonly count = computed(() => this.totalParticipants() ?? this.participants().length);

  participantName(participant: EventParticipationDto): string {
    if (!participant.firstName && !participant.lastName) return 'Anonymous Participant';
    return `${participant.firstName ?? ''} ${participant.lastName ?? ''}`;
  }
}
