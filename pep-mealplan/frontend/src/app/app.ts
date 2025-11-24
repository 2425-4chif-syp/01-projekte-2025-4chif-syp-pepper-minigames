import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { SelectButtonModule } from 'primeng/selectbutton';
import { FormsModule } from '@angular/forms';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [ButtonModule, SelectButtonModule, FormsModule, Navbar, RouterOutlet],
  templateUrl: './app.html',
  standalone: true,
  styleUrl: './app.css',
})
export class App {}
