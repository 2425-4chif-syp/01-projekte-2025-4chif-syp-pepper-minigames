import { Component } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { RoleService } from '../role.service';


@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {
  
  constructor(private roleService: RoleService) {}

  isAdmin(): boolean {
    return this.roleService.hasRole('admin');
  }

}
