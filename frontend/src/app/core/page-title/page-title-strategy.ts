import {inject, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, TitleStrategy} from '@angular/router';
import {Meta, Title} from '@angular/platform-browser';

const APP_NAME = 'MeetMap';
const DEFAULT_DESCRIPTION = 'Find and join events happening near you on MeetMap.';

@Injectable({providedIn: 'root'})
export class PageTitleStrategy extends TitleStrategy {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  override updateTitle(snapshot: RouterStateSnapshot) {
    const routeTitle = this.buildTitle(snapshot);
    const pageTitle = routeTitle ? `${APP_NAME} – ${routeTitle}` : APP_NAME;
    const description = this.findDescription(snapshot) ?? DEFAULT_DESCRIPTION;

    this.title.setTitle(pageTitle);
    this.meta.updateTag({name: 'description', content: description});
    this.meta.updateTag({property: 'og:title', content: pageTitle});
    this.meta.updateTag({property: 'og:description', content: description});
    this.meta.updateTag({property: 'og:url', content: window.location.href});
  }
  private findDescription(snapshot: RouterStateSnapshot): string | undefined {
    let route: ActivatedRouteSnapshot | undefined = snapshot.root;
    let description: string | undefined;
    while (route) {
      description = route.data['description'] ?? description;
      route = route.firstChild ?? undefined;
    }
    return description;
  }
}
