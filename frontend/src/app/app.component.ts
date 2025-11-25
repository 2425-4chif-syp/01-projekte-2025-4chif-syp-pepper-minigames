import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterModule } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { RoleService } from './role.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, FormsModule],
  providers: [AuthGuard, RoleService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'PepperAngular';

  constructor(private authGuard: AuthGuard, private roleService: RoleService) {}

  private isDarkmode:boolean = false;
  
  public onDarkmode():void{
    this.isDarkmode = !this.isDarkmode;
    const theme = this.isDarkmode ? 'dark' : 'light';
    document.getElementById("appComp")?.setAttribute('data-theme', theme);
  }
  
  public closeDrawer(): void{
    const drawer: any = document.getElementById("my-drawer");
    if(drawer){
      drawer.checked =  false;
    }
  }
// ✅ Logout-Funktion die sicherstellt, dass User zum Login zurückkehrt
  public logout(): void {
    console.log('Logging out...');
    
    // Tokens löschen
    localStorage.removeItem('kc_token');
    localStorage.removeItem('kc_refresh_token');
    
    // Keycloak Logout (führt zur Login-Seite)
    this.authGuard.logout();
  }

  // ✅ User-Info für Anzeige
  public currentUser(): string {
    const userInfo = this.roleService.getUserInfo();
    return userInfo?.preferred_username || userInfo?.name || 'User';
  }

}