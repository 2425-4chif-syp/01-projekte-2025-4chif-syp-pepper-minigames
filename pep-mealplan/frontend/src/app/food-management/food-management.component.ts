import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    FormControl,
    FormsModule,
    ReactiveFormsModule
} from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { tap } from 'rxjs/operators';

import { Allergen, Food, MenuAPIService, Picture } from '../menu-api.service';
import { API_URL } from '../constants';

import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SelectModule } from 'primeng/select';

@Component({
    selector: 'app-food-management',
    templateUrl: './food-management.component.html',
    styleUrls: ['./food-management.component.scss'],
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        InputTextModule,
        SelectModule,
        ButtonModule,
        ProgressSpinnerModule
    ]
})
export class FoodManagementComponent implements OnInit {
    foodTypes = [
        { label: 'Suppe', value: 'soup' },
        { label: 'Hauptgericht', value: 'main' },
        { label: 'Nachspeise', value: 'dessert' }
    ];

    nameFilter = new FormControl<string | null>('');
    foods: Food[] = [];
    filteredFood: Food[] = [];

    newFood: Food = this.createEmptyFood();
    allergens: Allergen[] = [];
    allergensChecked: boolean[] = [];

    img = new Image();
    readonly apiUrl = API_URL;
    searchLoading = false;

    constructor(private readonly menuApiService: MenuAPIService) {}

    ngOnInit(): void {
        this.initSearchFilter();
        this.loadFoods();
        this.loadAllergens();
        this.restoreLastFoodType();
    }

    // -----------------------------
    // Init / Helper
    // -----------------------------

    private createEmptyFood(): Food {
        return {
            Name: '',
            Allergens: [],
            Type: '',
            Picture: {
                Base64: '',
                Name: '',
                MediaType: ''
            } as Picture
        } as Food;
    }

    private restoreLastFoodType(): void {
        const lastSelectedType = localStorage.getItem('lastFoodType');
        if (!this.newFood.Type && lastSelectedType) {
            this.newFood.Type = lastSelectedType;
        }
    }

    private initSearchFilter(): void {
        this.nameFilter.valueChanges
            .pipe(
                debounceTime(200),
                distinctUntilChanged(),
                tap(() => (this.searchLoading = true))
            )
            .subscribe((filterValue: string | null) => {
                const term = (filterValue ?? '').toLowerCase();
                this.filteredFood = this.foods.filter(food =>
                    food.Name.toLowerCase().includes(term)
                );
                this.searchLoading = false;
            });
    }

    // -----------------------------
    // Data Loading
    // -----------------------------

    loadFoods(): void {
        this.menuApiService.getFood().subscribe(data => {
            const foods = data ?? [];
            this.foods = foods;
            this.filteredFood = [...foods].sort((a, b) =>
                a.Name.localeCompare(b.Name)
            );
        });
    }

    private loadAllergens(): void {
        this.menuApiService.getAllergens().subscribe(data => {
            this.allergens = data ?? [];
            this.allergensChecked = new Array(this.allergens.length).fill(false);
        });
    }

    // -----------------------------
    // Food Type Handling
    // -----------------------------

    onFoodTypeChange(type: string): void {
        if (type) {
            localStorage.setItem('lastFoodType', type);
        }
    }

    // -----------------------------
    // Image / File Handling
    // -----------------------------

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];
        if (!file) {
            return;
        }

        const reader = new FileReader();

        this.newFood.Picture.Name = file.name.split('.')[0];
        const fileExtension = file.name.split('.').pop()?.toLowerCase() ?? '';

        switch (fileExtension) {
            case 'jpg':
            case 'jpeg':
                this.newFood.Picture.MediaType = 'image/jpeg';
                break;
            case 'png':
                this.newFood.Picture.MediaType = 'image/png';
                break;
            default:
                this.newFood.Picture.MediaType = 'image/jpeg';
                break;
        }

        reader.readAsDataURL(file);
        reader.onload = () => {
            const img = new Image();
            img.src = reader.result as string;

            const originalSize = (reader.result as string).length * 3 / 4;
            console.log(`Originalgröße: ${originalSize} Bytes`);

            img.onload = () => {
                const canvas = document.createElement('canvas');
                const ctx = canvas.getContext('2d');
                const maxWidth = 500;
                const scale = maxWidth / img.width;

                canvas.width = maxWidth;
                canvas.height = img.height * scale;

                ctx?.drawImage(img, 0, 0, canvas.width, canvas.height);

                const base64Data = canvas
                    .toDataURL(this.newFood.Picture.MediaType, 0.7)
                    .split(',')
                    .pop() as string;

                this.newFood.Picture.Base64 = base64Data;

                const compressedSize = base64Data.length * 3 / 4;
                console.log(`Komprimierte Größe: ${compressedSize} Bytes`);
                console.log(
                    `Reduktion: ${(
                        ((originalSize - compressedSize) / originalSize) *
                        100
                    ).toFixed(2)}%`
                );

                this.img.src =
                    'data:' + this.newFood.Picture.MediaType + ';base64,' + base64Data;
            };
        };
    }

    // -----------------------------
    // CRUD Actions
    // -----------------------------

    addFood(): void {
        this.newFood.Allergens = this.allergens
            .filter((_, index) => this.allergensChecked[index])
            .map(a => a.Shortname);

        if (this.newFood.ID) {
            this.menuApiService.updateFood(this.newFood).subscribe({
                next: () => {
                    this.loadFoods();
                    this.resetForm();
                }
            });
        } else {
            this.menuApiService.addFood(this.newFood).subscribe({
                next: () => {
                    this.loadFoods();
                    this.resetForm();
                },
                error: () => alert('Ein Fehler ist beim Speichern aufgetreten.')
            });
        }
    }

    deleteFood(id: number, name: string): void {
        const confirmation = confirm(
            `Möchten Sie das Gericht "${name}" wirklich löschen?`
        );
        if (!confirmation) {
            return;
        }

        this.menuApiService.deleteFood(id).subscribe({
            next: () => {
                alert(`Gericht "${name}" wurde gelöscht!`);
                this.loadFoods();
                this.resetForm();
            },
            error: () => alert('Gericht kann nicht gelöscht werden!')
        });
    }

    loadFood(food: Food): void {
        this.newFood = { ...food };

        this.allergensChecked = this.allergens.map(a =>
            (this.newFood.Allergens || []).includes(a.Shortname)
        );

        if (food.Picture && (food.Picture.Base64 || (food.Picture as any).Bytes)) {
            const base64Bytes =
                food.Picture.Base64 || (food.Picture as any).Bytes;
            const mediaType =
                food.Picture.MediaType || (food.Picture as any).mediaType;

            let imageData = base64Bytes;
            try {
                const decoded = atob(base64Bytes);
                if (/^[A-Za-z0-9+/=]+$/.test(decoded)) {
                    imageData = decoded;
                }
            } catch {
                // not double-encoded, ignore
            }

            this.img.src = `data:${mediaType};base64,${imageData}`;
        } else {
            this.img = new Image();
        }
    }

    resetForm(): void {
        this.newFood = this.createEmptyFood();
        this.restoreLastFoodType();
        this.allergensChecked = new Array(this.allergens.length).fill(false);
        this.img = new Image();
    }
}
