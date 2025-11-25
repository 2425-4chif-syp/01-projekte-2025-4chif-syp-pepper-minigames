import { Component, Input, OnInit } from '@angular/core';
import { Allergen, Food, MenuAPIService } from '../menu-api.service';
import { Router } from '@angular/router';
import { NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';

@Component({
    selector: 'app-food-editor',
    templateUrl: './food-editor.component.html',
    styleUrls: ['./food-editor.component.scss'],
    standalone: true,
    imports: [
        NgClass,
        FormsModule,
        SelectModule
    ]
})
export class FoodEditorComponent implements OnInit {
    food?: Food;
    @Input() foodId?: number;
    allergens?: Allergen[];
    allergensChecked: boolean[] = [];
    img = new Image();

    // Optionen für PrimeNG Select – gleiche Werte wie vorher
    foodTypes = [
        { label: 'Suppe',        value: 'soup' },
        { label: 'Hauptgericht', value: 'main' },
        { label: 'Nachspeise',   value: 'dessert' }
    ];

    constructor(
        private router: Router,
        private menuApiService: MenuAPIService
    ) {}

    ngOnInit(): void {
        if (this.foodId == null || this.foodId < 0) {
            return;
        }

        this.menuApiService.getFoodById(this.foodId).subscribe((data) => {
            this.food = data;
            this.loadImageFromFood();

            this.menuApiService.getAllergens().subscribe((allergensData) => {
                this.allergens = allergensData;
                this.allergensChecked = new Array(allergensData.length).fill(false);

                for (let i = 0; i < allergensData.length; i++) {
                    if (this.food?.Allergens.includes(allergensData[i].Shortname)) {
                        this.allergensChecked[i] = true;
                    }
                }
            });
        });
    }

    loadImageFromFood(): void {
        if (!this.food?.Picture) return;

        this.img.src = `data:${this.food.Picture.MediaType};base64,${this.food.Picture.Base64}`;
        this.img.onload = () => this.scaleImage();
    }

    scaleImage(): void {
        const maxWidth = 500;
        if (this.img.width > maxWidth) {
            const scaleFactor = maxWidth / this.img.width;
            this.img.width *= scaleFactor;
            this.img.height *= scaleFactor;
        }
    }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (!input.files?.length) return;

        const file = input.files[0];
        const fileExtension = file.name.split('.').pop()?.toLowerCase() || '';
        const reader = new FileReader();

        if (!this.food) {
            this.food = {
                Name: '',
                Type: '',
                Allergens: [],
                Picture: { Name: '', MediaType: '', Base64: '' }
            };
        }
        if (!this.food.Picture) {
            this.food.Picture = { Name: '', MediaType: '', Base64: '' };
        }

        this.food.Picture.Name = file.name.split('.')[0];

        switch (fileExtension) {
            case 'jpg':
            case 'jpeg':
                this.food.Picture.MediaType = 'image/jpeg';
                break;
            case 'png':
                this.food.Picture.MediaType = 'image/png';
                break;
            default:
                this.food.Picture.MediaType = 'image/jpeg';
                break;
        }

        reader.readAsDataURL(file);
        reader.onload = () => {
            const result = reader.result as string;
            this.food!.Picture.Base64 = result.split(',').pop() as string;
            this.loadImageFromFood();
        };
    }

    updateFood(): void {
        if (!this.food || !this.food.Picture?.Base64) {
            alert('Gericht kann nicht ohne Bild gespeichert werden!');
            setTimeout(() => this.router.navigate(['/food-management']), 0);
            return;
        }

        this.food.Allergens = [];
        if (this.allergens) {
            for (let i = 0; i < this.allergensChecked.length; i++) {
                if (this.allergensChecked[i]) {
                    this.food.Allergens.push(this.allergens[i].Shortname);
                }
            }
        }

        this.menuApiService.updateFood(this.food).subscribe({
            next: () => {
                alert('Änderungen erfolgreich gespeichert!');
                setTimeout(() => this.router.navigate(['/food-management']), 0);
            },
            error: () => {
                alert('Fehler beim Speichern des Gerichts!');
                setTimeout(() => this.router.navigate(['/food-management']), 0);
            }
        });
    }
}
