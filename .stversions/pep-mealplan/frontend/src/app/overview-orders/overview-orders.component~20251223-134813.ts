import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { API_URL } from '../constants';
import {ToggleSwitch} from "primeng/toggleswitch";

@Component({
    selector: 'app-overview-orders',
    templateUrl: './overview-orders.component.html',
    styleUrls: ['./overview-orders.component.scss'],
    imports: [CommonModule, FormsModule, MatSlideToggleModule, ToggleSwitch]
})
export class OverviewOrdersComponent implements OnInit {
  // Standardmäßig "Personal" aktiv
  viewOption: string = 'personal';

  orders: any[] = [];
  filteredOrders: any[] = [];
  selectedDate: string = this.getTodayDate();

  // Menü für den gewählten Tag
  menu: any = null;

  // Bestellstatistiken
  orderStats = {
    Soup: 0,
    Lunch1: 0,
    Lunch2: 0,
    LunchDessert: 0,
    Dinner1: 0,
    Dinner2: 0,
  };

  // Bewohner-Statistiken
  residentCount: number = 0;
  orderedCount: number = 0;
  missingCount: number = 0;

  constructor(private http: HttpClient) {}

  async ngOnInit(): Promise<void> {
    await this.loadResidentCount();
    await this.loadOrders();
    await this.loadMenu();
    this.filterOrders();
  }

  onToggleView(checked: boolean): void {
    this.viewOption = checked ? 'kueche' : 'personal';
  }

  formatDate(date: string): string {
    const [year, month, day] = date.split('-');
    return `${day}.${month}.${year}`;
  }

  formatOrderedAt(orderedAt: string): string {
    const [date, time] = orderedAt.split('_');
    const [year, month, day] = date.split('-');
    return `${day}.${month}.${year} ${time}`;
  }

  getTodayDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  filterOrders(): void {
    const formattedSelectedDate = this.formatDate(this.selectedDate);
    this.filteredOrders = this.orders.filter(
      (order) => order.Date === formattedSelectedDate
    );

    this.orderedCount = this.filteredOrders.length;
    this.missingCount = this.residentCount - this.orderedCount;

    this.orderStats = {
      Soup: 0,
      Lunch1: 0,
      Lunch2: 0,
      LunchDessert: 0,
      Dinner1: 0,
      Dinner2: 0,
    };
    this.loadMenu();
  }

  calculateOrderStats(): void {
    if (!this.menu) {
      this.orderStats = {
        Soup: 0,
        Lunch1: 0,
        Lunch2: 0,
        LunchDessert: 0,
        Dinner1: 0,
        Dinner2: 0,
      };
      return;
    }

    this.orderStats = {
      Soup: 0,
      Lunch1: 0,
      Lunch2: 0,
      LunchDessert: 0,
      Dinner1: 0,
      Dinner2: 0,
    };
    // Durchlaufe alle Bestellungen mit einer for-Schleife
    for (const order of this.filteredOrders) {
      console.log(this.menu.Soup);
      if (order.Soup.Name === this.menu.Soup) {
        this.orderStats.Soup++;
      }
      if (order.SelectedLunch.Name == this.menu.Lunch1) {
        this.orderStats.Lunch1++;
      }
      if (
        order.DessertSelected &&
        order.LunchDessert.Name == this.menu.LunchDessert
      ) {
        this.orderStats.LunchDessert++;
      }
      if (order.SelectedDinner.Name === this.menu.Dinner1) {
        this.orderStats.Dinner1++;
      }
    }

    // Berechne Lunch2 und Dinner2 aus der Gesamtanzahl der Bestellungen
    this.orderStats.Lunch2 =
      this.filteredOrders.length - this.orderStats.Lunch1;
    this.orderStats.Dinner2 =
      this.filteredOrders.length - this.orderStats.Dinner1;
  }

  async loadOrders(): Promise<void> {
    try {
      const data: any[] =
        (await this.http.get<any[]>(`${API_URL}/api/orders`).toPromise()) || [];
      data.forEach((order) => {
        order.Date = this.formatDate(order.Date);
        order.OrderedAt = this.formatOrderedAt(order.OrderedAt);
      });
      this.orders = data;
      this.filterOrders();
    } catch (error) {
      console.error('Fehler beim Laden der Bestellungen:', error);
      this.orders = [];
    }
  }

  async loadMenu(): Promise<void> {
    try {
      const selectedDateISO = new Date(this.selectedDate)
        .toISOString()
        .split('T')[0];
      const response = await fetch(
        `${API_URL}/api/orders/date/${selectedDateISO}`
      );
      const menuData = await response.json();

      if (menuData.length > 0) {
        const firstOrder = menuData[0];
        this.menu = {
          OrderID: firstOrder.Menu.ID,
          WeekNumber: firstOrder.Menu.WeekNumber,
          Weekday: firstOrder.Menu.Weekday,
          Soup: firstOrder.Menu.SoupID,
          Lunch1: firstOrder.Menu.M1ID,
          Lunch2: firstOrder.Menu.M2ID,
          LunchDessert: firstOrder.Menu.LunchDessertID,
          Dinner1: firstOrder.Menu.A1ID,
          Dinner2: firstOrder.Menu.A2ID,
        };

        // Hole die Menü-Namen anhand der IDs
        this.menu.Soup = await this.getFoodNameByID(this.menu.Soup);
        this.menu.Lunch1 = await this.getFoodNameByID(this.menu.Lunch1);
        this.menu.Lunch2 = await this.getFoodNameByID(this.menu.Lunch2);
        this.menu.LunchDessert = await this.getFoodNameByID(
          this.menu.LunchDessert
        );
        this.menu.Dinner1 = await this.getFoodNameByID(this.menu.Dinner1);
        this.menu.Dinner2 = await this.getFoodNameByID(this.menu.Dinner2);
        this.calculateOrderStats();
      } else {
        console.error('No menu data found for selected date.');
        this.menu = null;
      }
    } catch (error) {
      console.error('Fehler beim Laden des Menüs:', error);
      this.menu = null;
    }
  }

  async loadResidentCount(): Promise<void> {
    try {
      const data: any = await this.http
        .get(`${API_URL}/api/residents/count`)
        .toPromise();
      this.residentCount = data.count;
    } catch (error) {
      console.error('Fehler beim Laden der Bewohneranzahl:', error);
      this.residentCount = 0;
    }
  }

  async getFoodNameByID(id: number): Promise<string> {
    try {
      let response = await fetch(`${API_URL}/api/foods/id/${id}`);
      let data = await response.json();
      return data.Name;
    } catch (error) {
      console.error('Error fetching food name:', error);
      return '';
    }
  }
}