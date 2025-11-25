import { Component, OnInit } from '@angular/core';
import { Allergen, Food, MenuAPIService, Picture } from '../menu-api.service';
import { Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { tap } from 'rxjs/operators';
import { API_URL } from '../constants';

@Component({
    selector: 'app-food-management',
    templateUrl: './food-management.component.html',
    styleUrls: ['./food-management.component.scss'],
    standalone: false
})
export class FoodManagementComponent implements OnInit {
  nameFilter = new FormControl('');
  foods: Food[] = [];
  newFood: Food = { 
    Name: "", 
    Allergens: [""], 
    Type: "", 
    Picture: { Base64: "", Name: "", MediaType: "" } 
  } as Food;
  newPicture: Picture = {} as Picture;
  filteredFood: Food[] = [];
  newFoodAllergens: string = "";
  allergens: Allergen[] = [];
  allergensChecked: boolean[] = [];
  img = new Image();
  public apiUrl = API_URL;
  searchLoading = false;

  constructor(private menuApiService: MenuAPIService, private router: Router) {
    this.menuApiService.getFood().subscribe((foods) => {
      if(foods == null) {
        foods = [];
      }
      this.foods = foods;
      this.filteredFood = foods;
    });
  }

  ngOnInit() {
    this.loadFoods();

    // Set default food type to the last selected type
    const lastSelectedType = localStorage.getItem('lastFoodType');
    if (!this.newFood.Type && lastSelectedType) {
      this.newFood.Type = lastSelectedType;
    }

    this.nameFilter.valueChanges
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        tap(() => (this.searchLoading = true))
      )
      .subscribe((nameFilter: string | null) => {
        this.filteredFood = this.foods.filter((food) =>
          food.Name.toLowerCase().includes(nameFilter ? nameFilter.toLowerCase() : '')
        );
        this.searchLoading = false;
      });
    this.menuApiService.getAllergens().subscribe((data) => {
      if(data == null) {
        data = [];
      }
      this.allergens = data;
      this.allergensChecked = new Array(this.allergens.length).fill(false);
    });
  }

  onFoodTypeChange(type: string): void {
    localStorage.setItem('lastFoodType', type);
  }

  loadFoods() {
    this.menuApiService.getFood().subscribe((data) => {
      if(data == null) {
        data = [];
      }
      this.foods = data ? data : [];
      this.filteredFood = this.foods.sort((a, b) => a.Name.localeCompare(b.Name));
    });
  }

  // Updated file selection method to set both lowercase and uppercase keys
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    const reader = new FileReader();
    // Set picture name based on file name (without extension)
    this.newFood.Picture.Name = file.name.split('.')[0];
    const fileExtension = file.name.split('.').pop()?.toLowerCase() || '';

    // Determine media type based on file extension
    switch (fileExtension) {
      case "jpg":
      case "jpeg":
        this.newFood.Picture.MediaType = "image/jpeg";
        break;
      case "png":
        this.newFood.Picture.MediaType = "image/png";
        break;
      default:
        this.newFood.Picture.MediaType = "image/jpeg";
        break;
    }

    reader.readAsDataURL(file);
    reader.onload = () => {
      const img = new Image();
      img.src = reader.result as string;

      // Log original size (approximate)
      const originalSize = (reader.result as string).length * 3 / 4;
      console.log(`Originalgröße: ${originalSize} Bytes`); //DEV

      img.onload = () => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        const maxWidth = 500;
        const scale = maxWidth / img.width;
        canvas.width = maxWidth;
        canvas.height = img.height * scale;

        ctx!.drawImage(img, 0, 0, canvas.width, canvas.height);
        const base64Data = canvas.toDataURL(this.newFood.Picture.MediaType, 0.7).split(',').pop() as string;
        this.newFood.Picture.Base64 = base64Data;

        // Log compressed size (approximate)
        const compressedSize = base64Data.length * 3 / 4;
        console.log(`Komprimierte Größe: ${compressedSize} Bytes`); //DEV
        console.log(`Reduktion: ${((originalSize - compressedSize) / originalSize * 100).toFixed(2)}%`); //DEV
        this.img.src = 'data:' + this.newFood.Picture.MediaType + ';base64,' + base64Data;
      };
    };
  }

  addFood() {
    // Collect selected allergens
    for (let i = 0; i < this.allergensChecked.length; i++) {
      if (this.allergensChecked[i]) {
        this.newFood.Allergens.push(this.allergens[i].Shortname);
      }
    }
    this.newFood.Allergens = this.newFood.Allergens.filter(a => a !== "");

    if (this.newFood.ID) {
      this.resetForm();
      this.menuApiService.updateFood(this.newFood).subscribe(() => {
        this.loadFoods();
      });
    } else {
      this.menuApiService.addFood(this.newFood).subscribe({
        next: () => {
          this.loadFoods();
        },
        error: () => alert("Ein Fehler ist beim Speichern aufgetreten.")
      });
    }

    this.resetForm();
  }

  deleteFood(id: number, name: string) {
    const confirmation = confirm(`Möchten Sie das Gericht "${name}" wirklich löschen?`);
    if (confirmation) {
      this.menuApiService.deleteFood(id).subscribe({
        next: () => {
          alert(`Gericht "${name}" wurde gelöscht!`);
          this.loadFoods();
          this.resetForm();
        },
        error: () => alert("Gericht kann nicht gelöscht werden!")
      });
    }
  }

  loadFood(food: Food) {
    this.newFood = { ...food };
    this.allergensChecked = this.allergens.map(a => (this.newFood.Allergens || []).includes(a.Shortname));
    console.log(this.newFood); // DEV
  
    if (food.Picture && (food.Picture.Base64 || (food.Picture as any).Bytes)) {
      const base64Bytes = food.Picture.Base64 || (food.Picture as any).Bytes;
      const mediaType = food.Picture.MediaType || (food.Picture as any).mediaType;
      const name = food.Picture.Name || (food.Picture as any).name;
  
      // automatic check for double encoding
      let imageData = base64Bytes;
      try {
        const decoded = atob(base64Bytes);
        if (/^[A-Za-z0-9+/=]+$/.test(decoded)) {
          imageData = decoded;
        }
      } catch (e) {
        // ignore error, it means it's not double encoded
      }
  
      this.img.src = `data:${mediaType};base64,${imageData}`;
      console.log("Image loaded:", this.img.src); // DEV
    } else {
      this.img = new Image();
    }
  }  

  resetForm() {
    this.newFood = { 
      Name: "", 
      Allergens: [""], 
      Type: "", 
      Picture: { 
        base64Bytes: "", 
        name: "", 
        mediaType: "", 
        Base64: "", 
        Name: "", 
        MediaType: ""
      } 
    } as Food;

    // Set default food type to the last selected type
    const lastSelectedType = localStorage.getItem('lastFoodType');
    if (!this.newFood.Type && lastSelectedType) {
      this.newFood.Type = lastSelectedType;
    }
    
    this.allergensChecked = new Array(this.allergens.length).fill(false);
    this.img = new Image();
  }
}