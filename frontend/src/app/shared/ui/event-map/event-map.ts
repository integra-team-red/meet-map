import {AfterViewInit, Component, effect, ElementRef, inject, input, OnDestroy, viewChild} from '@angular/core';
import * as L from 'leaflet';
import {EventDto} from '@app/api/model/eventDto';
import {Router} from '@angular/router';

@Component({
  selector: 'app-event-map',
  templateUrl: './event-map.html',
})
export class EventMap implements AfterViewInit, OnDestroy {
  readonly events = input<EventDto[]>([]);
  private readonly mapContainer = viewChild.required<ElementRef<HTMLDivElement>>('mapContainer');
  private readonly router = inject(Router);

  private map?: L.Map;
  private markers?: L.LayerGroup;
  private resizeObserver?: ResizeObserver;

  constructor() {
    effect(() => {
      const events = this.events();
      if (this.markers){
        this.renderPins(events);
      }
    });
  }

  ngAfterViewInit(): void {
    const container = this.mapContainer().nativeElement;

    this.map = L.map(container, {
      center: [46.7712, 23.6236],
      zoom: 12,
      zoomControl: false,
    });
    L.control.zoom({position: 'bottomright'}).addTo(this.map);

    this.markers = L.layerGroup().addTo(this.map);
    this.renderPins(this.events());
    this.centerOnUser();

    this.resizeObserver = new ResizeObserver(()=> this.map?.invalidateSize());
    this.resizeObserver.observe(container);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.map);
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    this.map?.remove();
  }

  private renderPins(events: EventDto[]):void{
    this.markers?.clearLayers();
    for (const event of events){
      if (event.id==null || event.latitude == null || event.longitude ==null) continue;

      L.marker([event.latitude, event.longitude], {icon: this.pinIcon()})
        .bindTooltip(event.title ?? 'Event')
        .on('click', ()=> this.router.navigate(['/events', event.id]))
        .addTo(this.markers!);
    }
  }

  private pinIcon(): L.DivIcon{
    return L.divIcon({
      html: '<i class="pi pi-map-marker text-4xl! text-primary"></i>',

      className: '',
      iconSize: [32, 32],
      iconAnchor: [16, 32],
    });
  }

  private centerOnUser(): void{
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition(
      position => this.map?.setView([position.coords.latitude, position.coords.longitude], 12),
      ()=>{},
    )
  }
}
