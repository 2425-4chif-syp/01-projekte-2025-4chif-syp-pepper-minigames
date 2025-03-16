import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterModule } from '@angular/router';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'PepperAngular';
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
}
