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
import { ImageApiService } from '../services/image-api.service';
import { Allergen } from '../models/allergen.model';
import { Food } from '../models/food.model';
import { Picture, ImageDto } from '../models/picture.model';
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

    // Image upload state
    uploading = false;
    previewUrl: string | null = null;
    selectedImageId: number | null = null;
    imageDescription: string = '';

    constructor(
        private readonly menuApiService: MenuAPIService,
        private readonly imageApiService: ImageApiService
    ) {}

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

        this.uploading = true;
        const reader = new FileReader();

        reader.readAsDataURL(file);
        reader.onload = () => {
            const imgElement = new Image();
            imgElement.src = reader.result as string;

            imgElement.onload = () => {
                // Resize image
                const canvas = document.createElement('canvas');
                const ctx = canvas.getContext('2d');
                const maxWidth = 800;
                const scale = Math.min(1, maxWidth / imgElement.width);

                canvas.width = imgElement.width * scale;
                canvas.height = imgElement.height * scale;

                ctx?.drawImage(imgElement, 0, 0, canvas.width, canvas.height);

                const dataUrl = canvas.toDataURL('image/png', 0.8);
                const base64Data = dataUrl.split(',')[1];

                // Show preview immediately using data URL
                this.previewUrl = dataUrl;
                this.img.src = dataUrl;

                // Upload to backend
                this.imageApiService.upload({
                    base64Image: base64Data,
                    description: this.imageDescription || undefined
                }).subscribe({
                    next: (uploaded) => {
                        this.selectedImageId = uploaded.id;

                        // Set picture reference for food
                        this.newFood.picture = {
                            id: uploaded.id,
                            description: uploaded.description,
                            base64: base64Data
                        };

                        this.uploading = false;
                    },
                    error: (err) => {
                        console.error('Error uploading image:', err);
                        alert('Fehler beim Hochladen des Bildes');
                        this.uploading = false;
                        this.previewUrl = null;
                        this.img = new Image();
                    }
                });
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

        // Handle image - check if we have an ID (new backend) or base64 (legacy)
        if (food.picture?.id) {
            this.selectedImageId = food.picture.id;
            this.previewUrl = this.imageApiService.getImageUrl(food.picture.id);
            this.img.src = this.previewUrl;
            this.imageDescription = food.picture.description || '';
        } else if (food.picture && (food.picture.base64 || (food.picture as any).Bytes)) {
            const base64Bytes =
                food.picture.base64 || (food.picture as any).Bytes;
            const mediaType =
                food.picture.mediaType || (food.picture as any).mediaType || 'image/png';

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
            this.previewUrl = this.img.src;
        } else {
            this.img = new Image();
            this.previewUrl = null;
            this.selectedImageId = null;
        }
    }

    resetForm(): void {
        this.newFood = this.createEmptyFood();
        this.restoreLastFoodType();
        this.allergensChecked = new Array(this.allergens.length).fill(false);
        this.img = new Image();
        this.previewUrl = null;
        this.selectedImageId = null;
        this.imageDescription = '';
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
