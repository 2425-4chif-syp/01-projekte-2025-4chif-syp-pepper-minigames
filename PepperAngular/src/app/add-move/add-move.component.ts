import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-move',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './add-move.component.html',
  styleUrls: ['./add-move.component.scss']
})
export class AddMoveComponent {
  moves = ["winken", "nicken", "beidarmig Winken", "drehen", "tanzen", "springen", "klatschen", "stampfen", "hüpfen", "wackeln", "winken", "nicken", "beidarmig Winken", "drehen", "tanzen", "springen", "klatschen", "stampfen", "hüpfen", "wackeln"];
}
