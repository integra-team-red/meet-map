import {Component, effect, model, signal} from '@angular/core';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-image-picker',
  imports: [
    Button
  ],
  templateUrl: './image-picker.html',
})
export class ImagePicker {
  readonly MAX_IMAGE_SIZE = 20 * 1024 * 1024;
  readonly image = model<File | null>(null);
  protected readonly previewUrl = signal<string | null>(null);
  protected readonly error = signal<string | null>(null);

  constructor() {
    effect((onCleanup) => {
      const file = this.image();
      if (!file) {
        this.previewUrl.set(null);
        return;
      }
      const url = URL.createObjectURL(file);
      this.previewUrl.set(url);
      onCleanup(() => URL.revokeObjectURL(url));
    });
  }

  protected onFileSelected(event: Event){
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file) return;

    if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
      this.error.set('Only .jpeg and .png files are allowed.');
    }

    if (file.size > this.MAX_IMAGE_SIZE){
      this.error.set('Pictures must be less than 20MB in size.');
    }

    this.error.set(null);
    this.image.set(file);
  }

  protected removeImage() {
    this.image.set(null);
  }
}
