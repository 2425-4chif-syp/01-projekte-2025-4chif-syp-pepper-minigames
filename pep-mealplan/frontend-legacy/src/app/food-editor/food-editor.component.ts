import { Component, Input, OnInit } from '@angular/core';
import { Allergen, Food, MenuAPIService } from '../menu-api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-food-editor',
  templateUrl: './food-editor.component.html',
  styleUrls: ['./food-editor.component.scss']
})
export class FoodEditorComponent implements OnInit {
  food?: Food;
  @Input() foodId?: number;
  allergens?: Allergen[];
  allergensChecked: boolean[] = [];
  img = new Image();

  constructor(private router: Router, private menuApiService: MenuAPIService) {}

  ngOnInit(): void {
    // Check if foodId is valid
    if (this.foodId !== undefined && this.foodId >= 0) {
      // Get food by id
      this.menuApiService.getFoodById(this.foodId).subscribe((data) => {
        this.food = data;
        console.log(this.food); // DEV log

        // If a picture exists, load it and scale if necessary
        if (this.food?.Picture) {
          this.img.src = 'data:' + this.food.Picture.MediaType + ';base64,' + this.food.Picture.Base64;
          this.img.onload = () => {
            while (this.img.width > 500) {
              this.img.width /= 2;
              this.img.height /= 2;
            }
          };
        }

        // Get allergens list and mark selected allergens based on food data
        this.menuApiService.getAllergens().subscribe((allergensData) => {
          this.allergens = allergensData;
          this.allergensChecked = new Array(this.allergens.length).fill(false);
          for (let i = 0; i < this.allergens.length; i++) {
            if (this.food?.Allergens.includes(this.allergens[i].Shortname)) {
              this.allergensChecked[i] = true;
            }
          }
        });
      });
    }
  }

  // Loads the image from the food object and scales it
  loadImageFromFood(): void {
    if (this.food && this.food.Picture) {
      this.img.src = 'data:' + this.food.Picture.MediaType + ';base64,' + this.food.Picture.Base64;
      this.img.onload = () => {
        this.scaleImage();
      };
    }
  }

  // Scales the image if it's wider than the maximum allowed width
  scaleImage(): void {
    const maxWidth = 500;
    if (this.img.width > maxWidth) {
      const scaleFactor = maxWidth / this.img.width;
      this.img.width *= scaleFactor;
      this.img.height *= scaleFactor;
    }
  }

  // Handles file input change event to load and update the image in food.Picture
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file: File = input.files[0];
      const fileExtension = file.name.split('.').pop()?.toLowerCase() || '';
      const reader = new FileReader();

      // Ensure food.Picture exists
      if (!this.food) {
        this.food = { Name: '', Type: '', Allergens: [], Picture: { Name: '', MediaType: '', Base64: '' } };
      }
      if (!this.food.Picture) {
        this.food.Picture = { Name: '', MediaType: '', Base64: '' };
      }

      // Set the file name without extension
      this.food.Picture.Name = file.name.split('.')[0];

      // Determine media type based on file extension
      switch (fileExtension) {
        case "jpg":
        case "jpeg":
          this.food.Picture.MediaType = "image/jpeg";
          break;
        case "png":
          this.food.Picture.MediaType = "image/png";
          break;
        default:
          this.food.Picture.MediaType = "image/jpeg";
          break;
      }

      // Read file as data URL
      reader.readAsDataURL(file);
      reader.onload = () => {
        const result = reader.result as string;
        // Extract base64 data from data URL
        this.food!.Picture.Base64 = result.split(',').pop() as string;
        this.loadImageFromFood();
      };
    }
  }

  // Updates the food entry, including allergens and picture, via the API
  updateFood(): void {
    // Check if a picture is available
    if (!this.food || !this.food.Picture || !this.food.Picture.Base64) {
      alert("Gericht kann nicht ohne Bild gespeichert werden!");
      setTimeout(() => this.router.navigate(['/food-management']), 0);
      return;
    }

    // Reset allergens list
    this.food.Allergens = [];
    // Add selected allergens to food.Allergens
    if (this.allergens) {
      for (let i = 0; i < this.allergensChecked.length; i++) {
        if (this.allergensChecked[i]) {
          this.food.Allergens.push(this.allergens[i].Shortname);
        }
      }
    }

    // Update the food using the MenuAPIService
    this.menuApiService.updateFood(this.food).subscribe({
      next: () => {
        alert("Änderungen erfolgreich gespeichert!");
        setTimeout(() => this.router.navigate(['/food-management']), 0);
      },
      error: () => {
        alert("Fehler beim Speichern des Gerichts!");
        setTimeout(() => this.router.navigate(['/food-management']), 0);
      }
    });
  }
}