import {Component, OnInit, signal} from '@angular/core';
import {Router, NavigationEnd, Event, RouterOutlet, RouterLink} from '@angular/router';
import {MegaMenuItem} from "primeng/api";
import {NgClass, NgIf} from "@angular/common";
import {MegaMenuModule} from "primeng/megamenu";
import {Ripple} from "primeng/ripple";
import {AuthGuard} from "./guards/auth.guard";
import {RoleService} from "./services/role.service";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    imports: [
        NgClass,
        NgIf,
        RouterOutlet,
        RouterLink,
        MegaMenuModule,
        Ripple,
        ButtonModule,
        TooltipModule
    ],
    standalone: true
})
export class AppComponent implements OnInit{
    items: MegaMenuItem[] | undefined;
    currentRoute = signal<string>("");

    constructor(
        private router: Router,
        private authGuard: AuthGuard,
        private roleService: RoleService
    ) {}

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
            { label: 'PEPPER DASHBOARD', root: true, url: 'https://vm107.htl-leonding.ac.at/'}
        ];
    }

    get currentUser(): string {
        return this.roleService.getUsername();
    }

    get isLoggedIn(): boolean {
        return !!localStorage.getItem('kc_token');
    }

    logout(): void {
        this.authGuard.logout();
    }
}