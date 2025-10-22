import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, Event } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { API_URL } from './constants';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'MenuAssistent_Website';
  showNavbar = true;
  currentUrl = '';

  constructor(private router: Router, private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.router.events
      .pipe(
        filter(
          (event: Event): event is NavigationEnd =>
            event instanceof NavigationEnd
        )
      )
      .subscribe((event: NavigationEnd) => {
        this.currentUrl = event.urlAfterRedirects;
        const hideNavbarRoutes = [
          '/select-menu/',
          '/create-order',
          '/select-weekday',
        ];
        this.showNavbar = !hideNavbarRoutes.some((route) =>
          event.urlAfterRedirects.startsWith(route)
        );
      });

    this.http.get<any>(API_URL + '/api/version-check').subscribe((res) => {
      if (res.updateAvailable) {
        this.snackBar.open(
          `ⓘ Hinweis: Eine neue Version (${res.latest}) ist verfügbar. Aktuelle Version: ${res.current}.`,
          undefined,
          {
            duration: 8000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: 'custom-orange-snackbar'
          }
        );
        console.log('Update available:', res.latest);
        console.log('Current version:', res.current);

      }else{
        console.log('No update available. Using latest version:', res.current);
      }
    });
  }
}