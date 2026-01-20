import {Component, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    FormControl,
    FormsModule,
    ReactiveFormsModule
} from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { tap } from 'rxjs/operators';

import { MenuAPIService } from '../services/menu-api.service';
import { Allergen } from '../models/allergen.model';
import { Food } from '../models/food.model';
import { Picture } from '../models/picture.model';
import { API_URL } from '../constants';

import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SelectModule } from 'primeng/select';
import { ListboxModule } from 'primeng/listbox';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';

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
        ProgressSpinnerModule,
        ListboxModule,
        CardModule,
        TagModule
    ]
})
export class FoodManagementComponent implements OnInit {
    foodTypes = [
        { label: 'Suppe', value: 'soup' },
        { label: 'Hauptgericht', value: 'main' },
        { label: 'Nachspeise', value: 'dessert' }
    ];

    foods = signal<Food[]>([]);
    selectedFood = signal<Food|null>(null);

    newFood: Food = this.createEmptyFood();
    allergens: Allergen[] = [];
    allergensChecked: boolean[] = [];

    img = new Image();
    readonly apiUrl = API_URL;

    constructor(private readonly menuApiService: MenuAPIService) {}

    ngOnInit(): void {
        this.loadFoods();
        this.loadAllergens();
        this.restoreLastFoodType();
    }

    private createEmptyFood(): Food {
        return {
            name: '',
            allergens: [],
            type: '',
            picture: {
                base64: '',
                name: '',
                mediaType: ''
            } as Picture
        } as Food;
    }

    private restoreLastFoodType(): void {
        const lastSelectedType = localStorage.getItem('lastFoodType');
        if (!this.newFood.type && lastSelectedType) {
            this.newFood.type = lastSelectedType;
        }
    }

    // -----------------------------
    // Data Loading
    // -----------------------------

    loadFoods(): void {
        this.menuApiService.getFood().subscribe(data => {
            const foods = data ?? [];
            this.foods.set(foods);
        });
    }

    onFoodSelected(food: Food | null): void {
        if (!food) {
            return;
        }

        this.selectedFood.set(food);
        this.loadFood(food);
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

        this.newFood.picture!.name = file.name.split('.')[0];
        const fileExtension = file.name.split('.').pop()?.toLowerCase() ?? '';

        switch (fileExtension) {
            case 'jpg':
            case 'jpeg':
                this.newFood.picture!.mediaType = 'image/jpeg';
                break;
            case 'png':
                this.newFood.picture!.mediaType = 'image/png';
                break;
            default:
                this.newFood.picture!.mediaType = 'image/jpeg';
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
                    .toDataURL(this.newFood.picture!.mediaType, 0.7)
                    .split(',')
                    .pop() as string;

                this.newFood.picture!.base64 = base64Data;

                const compressedSize = base64Data.length * 3 / 4;
                console.log(`Komprimierte Größe: ${compressedSize} Bytes`);
                console.log(
                    `Reduktion: ${(
                        ((originalSize - compressedSize) / originalSize) *
                        100
                    ).toFixed(2)}%`
                );

                this.img.src =
                    'data:' + this.newFood.picture!.mediaType + ';base64,' + base64Data;
            };
        };
    }

    // -----------------------------
    // CRUD Actions
    // -----------------------------

    addFood(): void {
        this.newFood.allergens = this.allergens
            .filter((_, index) => this.allergensChecked[index])
            .map(a => a.shortname);

        if (this.newFood.id) {
            alert('Änderungen bestehender Gerichte werden vom Backend derzeit nicht unterstützt.');
            return;
        }

        this.menuApiService.addFood(this.newFood).subscribe({
            next: () => {
                this.loadFoods();
                this.resetForm();
            },
            error: () => alert('Ein Fehler ist beim Speichern aufgetreten.')
        });
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
        if (!this.newFood.picture) {
            this.newFood.picture = { base64: '', name: '', mediaType: '' };
        }

        this.allergensChecked = this.allergens.map(a =>
            (this.newFood.allergens || []).includes(a.shortname)
        );

        if (food.picture && (food.picture.base64 || (food.picture as any).Bytes)) {
            const base64Bytes =
                food.picture.base64 || (food.picture as any).Bytes;
            const mediaType =
                food.picture.mediaType || (food.picture as any).mediaType;

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

    getFoodTypeLabel(type: string): string {
        const found = this.foodTypes.find(t => t.value === type);
        return found?.label ?? type;
    }

    getFoodTypeSeverity(type: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
        switch (type) {
            case 'soup': return 'warn';
            case 'main': return 'success';
            case 'dessert': return 'info';
            default: return 'secondary';
        }
    }
}
