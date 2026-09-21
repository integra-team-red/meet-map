import {AfterViewInit, Component, effect, ElementRef, input, OnDestroy, viewChild} from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-location-preview-map',
  imports: [],
  templateUrl: './location-preview-map.html',
})
export class LocationPreviewMap implements AfterViewInit, OnDestroy {
  readonly latitude = input<number | null>(null);
  readonly longitude = input<number | null>(null);
  private readonly mapContainer = viewChild.required<ElementRef<HTMLDivElement>>('mapContainer');

  private map?: L.Map;
  private marker?: L.Marker;
  private resizeObserver?: ResizeObserver;

  constructor() {
    effect(() => {
      const lat = this.latitude();
      const lon = this.longitude();
      if (this.map) {
        this.renderPin(lat, lon);
      }
    });
  }

  ngAfterViewInit(): void {
    const container = this.mapContainer().nativeElement;

    this.map = L.map(container, {center: [46.7712, 23.6236], zoom: 12});

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.map);

    this.resizeObserver = new ResizeObserver(() => this.map?.invalidateSize());
    this.resizeObserver.observe(container);

    this.renderPin(this.latitude(), this.longitude());

  }

  ngOnDestroy() {
    this.resizeObserver?.disconnect();
    this.map?.remove();
  }

  private renderPin(lat: number | null, lon: number | null): void {
    if (lat == null || lon == null) {
      this.marker?.remove();
      this.marker = undefined;
      return;
    }
    const pos: L.LatLngExpression = [lat, lon];
    if (this.marker) {
      this.marker.setLatLng(pos);
    } else {
      this.marker = L.marker(pos, {icon: this.pinIcon()}).addTo(this.map!);
    }
    this.map!.setView(pos, 16);
  }

  private pinIcon(): L.DivIcon {
    return L.divIcon({
      html: '<i class="pi pi-map-marker text-4xl! text-primary"></i>',
      className: '',
      iconSize: [32, 32],
      iconAnchor: [16, 32],
    })
  }
}
