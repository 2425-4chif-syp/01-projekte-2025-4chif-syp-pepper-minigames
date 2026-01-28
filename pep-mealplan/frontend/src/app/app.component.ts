import {Component, OnInit, signal} from '@angular/core';
import {Router, NavigationEnd, Event, RouterOutlet, RouterLink} from '@angular/router';
import {MegaMenuItem} from "primeng/api";
import {NgClass} from "@angular/common";
import {MegaMenuModule} from "primeng/megamenu";
import {Ripple} from "primeng/ripple";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    imports: [
        NgClass,
        RouterOutlet,
        RouterLink,
        MegaMenuModule,
        Ripple
    ],
    standalone: true
})
export class AppComponent implements OnInit{
    items: MegaMenuItem[] | undefined;
    currentRoute = signal<string>("");

    constructor(private router: Router) {}

    ngOnInit() {

        this.router.events.subscribe(event => {
            if (event instanceof NavigationEnd) {
                this.currentRoute.set(event.urlAfterRedirects);
            }
        });

        this.items = [
            { label: 'WOCHENPLAN', root: true, routerLink: '/menu-week' },
            { label: 'BESTELLUNGEN', root: true, routerLink: '/orders' },
            { label: 'BEWOHNER', root: true, routerLink: '/users' },
            { label: 'GERICHTE', root: true, routerLink: '/foods' },
            { label: 'PLANNER', root: true, routerLink: '/menu-grid' },
            { label: 'PEPPER DASHBOARD', root: true, url: 'https://vm107.htl-leonding.ac.at/', target: '_blank' }
        ];
    }

}