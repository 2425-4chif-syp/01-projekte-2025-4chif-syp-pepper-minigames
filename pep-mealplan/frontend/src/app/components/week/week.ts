import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-week',
  imports: [FormsModule, TableModule, Button],
  templateUrl: './week.html',
  styleUrl: './week.css',
})
export class Week {
}
