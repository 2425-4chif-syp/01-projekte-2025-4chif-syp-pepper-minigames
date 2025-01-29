import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-memory',
  standalone: true,
  imports: [CommonModule], // Import CommonModule to use ngFor
  templateUrl: './memory.component.html',
  styleUrls: ['./memory.component.css'] // Ensure correct plural 'styleUrls'
})
export class MemoryComponent {
  public filteredStories = [
    { name: 'Renate', isEnabled: 'true' },
    { name: 'Obermayer', isEnabled: 'false' },
    { name: 'Graf', isEnabled: 'true' },
    { name: 'Schweitzer', isEnabled: 'false' }
  ];
}
