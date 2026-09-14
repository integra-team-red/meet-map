import {Component, inject, input, numberAttribute, OnInit, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {Button} from 'primeng/button';
import {Message} from 'primeng/message';
import {HttpErrorResponse} from '@angular/common/http';
import {MatrixControllerService} from '@app/api/api/matrixController.service';
import {MatrixMessage} from '@app/api/model/matrixMessage';

interface ChatMessage {
  id: string;
  sender: string;
  content: string;
}

@Component({
  selector: 'app-event-chat',
  imports: [FormsModule, Button, Message, RouterLink],
  templateUrl: './event-chat.html',
})
export class EventChat implements OnInit {
  readonly id = input.required({transform: numberAttribute});

  private matrixService = inject(MatrixControllerService);

  messages = signal<ChatMessage[]>([]);
  messageText = signal('');
  loading = signal(true);
  sending = signal(false);
  error = signal<string | undefined>(undefined);

  ngOnInit() {
    this.loadMessages();
  }

  refresh() {
    this.loadMessages();
  }

  private loadMessages() {
    this.loading.set(true);

    this.matrixService.getMessages(this.id()).subscribe({
      next: (messages: MatrixMessage[]) => {
        this.messages.set(
          messages
            .filter(m => m.content?.msgtype === 'm.text')
            .map(m => ({
              id: m.event_id ?? crypto.randomUUID(),
              sender: m.sender ?? 'Unknown',
              content: m.content?.body ?? '',
            }))
            .reverse()
        );
        this.error.set(undefined);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.error.set(this.extractErrorMessage(err));
        this.loading.set(false);
      },
    });
  }

  sendMessage() {
    const text = this.messageText().trim();

    if (!text || this.sending()) {
      return;
    }

    this.sending.set(true);

    this.matrixService.sendMessage(this.id(), { message: text }).subscribe({
      next: () => {
        this.messageText.set('');
        this.sending.set(false);
        this.loadMessages();
      },
      error: (err: HttpErrorResponse) => {
        this.error.set(this.extractErrorMessage(err));
        this.sending.set(false);
      },
    });
  }

  private extractErrorMessage(err: HttpErrorResponse): string {
    return err?.error?.message ?? 'Could not connect to the event chat.';
  }
}
