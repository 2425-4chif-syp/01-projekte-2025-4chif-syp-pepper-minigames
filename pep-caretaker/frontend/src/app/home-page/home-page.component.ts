import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { RoleService } from '../role.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  
  constructor(private roleService: RoleService, private router: Router) {}

  ngOnInit() {
    console.log('HomePage ngOnInit - User roles:', this.roleService.getRoles());
    console.log('isAdmin:', this.isAdmin(), 'isCaretaker:', this.isCaretaker(), 'isResident:', this.isResident());
    
    // ⚠️ WICHTIG: Residents sollten nie hier ankommen (durch AuthGuard geblockt)
    // Aber als Sicherheitsmaßnahme: Falls doch, sofort umleiten
    if (this.isResident()) {
      console.log('⚠️ SECURITY: Resident accessed HomePage - redirecting to /my-pictures');
      this.router.navigate(['/my-pictures']);
      return; // Keine weitere Initialisierung
    }
  }

  isAdmin(): boolean {
    return this.roleService.hasRole('admin');
  }

  isCaretaker(): boolean {
    return this.roleService.hasRole('caretaker');
  }

  isResident(): boolean {
    return this.roleService.isResident(); // Nutze die richtige isResident() Methode
  }

}
