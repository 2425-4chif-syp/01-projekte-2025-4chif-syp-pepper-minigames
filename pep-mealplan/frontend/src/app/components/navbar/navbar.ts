import { Component, OnInit } from '@angular/core';
import { MegaMenuItem } from 'primeng/api';
import { MegaMenu } from 'primeng/megamenu';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { AvatarModule } from 'primeng/avatar';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [MegaMenu, ButtonModule, CommonModule, AvatarModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  standalone: true,
})
export class Navbar implements OnInit {
  items: MegaMenuItem[] | undefined;
  ngOnInit() {
    this.items = [
      {
        label: 'Woche',
        root: true,
        routerLink: '/week',
      },
      {
        label: 'Bestellungen',
        root: true,
        routerLink: '/orders',
      },
      {
        label: 'Bewohner',
        root: true,
        routerLink: '/residents',
      },
      {
        label: 'Gerichte',
        root: true,
        routerLink: '/dishes',
      },
      {
        label: 'Planer',
        root: true,
        routerLink: '/planner',
      },
    ];
  }
}
