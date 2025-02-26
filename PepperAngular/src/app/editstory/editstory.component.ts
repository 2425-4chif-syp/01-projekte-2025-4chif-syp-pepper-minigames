import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-editstory',
    imports: [RouterModule, CommonModule, FormsModule],
    templateUrl: './editstory.component.html',
    styleUrl: './editstory.component.css'
})
export class EditstoryComponent {

}
