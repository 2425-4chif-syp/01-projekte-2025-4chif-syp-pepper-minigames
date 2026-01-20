import { Component, Input, OnInit } from '@angular/core';
import { MenuAPIService } from '../services/menu-api.service';
import { Allergen } from '../models/allergen.model';
import { Food } from '../models/food.model';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';

@Component({
    selector: 'app-food-editor',
    templateUrl: './food-editor.component.html',
    styleUrls: ['./food-editor.component.scss'],
    standalone: true,
    imports: [
        FormsModule,
        SelectModule,
        CardModule,
        ButtonModule,
        InputTextModule,
        CheckboxModule
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
                    if (this.food?.allergens.includes(allergensData[i].shortname)) {
                        this.allergensChecked[i] = true;
                    }
                }
            });
        });
    }

    loadImageFromFood(): void {
        if (!this.food?.picture) return;

        this.img.src = `data:${this.food.picture.mediaType};base64,${this.food.picture.base64}`;
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
                name: '',
                type: '',
                allergens: [],
                picture: { name: '', mediaType: '', base64: '' }
            };
        }
        if (!this.food.picture) {
            this.food.picture = { name: '', mediaType: '', base64: '' };
        }

        this.food.picture.name = file.name.split('.')[0];

        switch (fileExtension) {
            case 'jpg':
            case 'jpeg':
                this.food.picture.mediaType = 'image/jpeg';
                break;
            case 'png':
                this.food.picture.mediaType = 'image/png';
                break;
            default:
                this.food.picture.mediaType = 'image/jpeg';
                break;
        }

        reader.readAsDataURL(file);
        reader.onload = () => {
            const result = reader.result as string;
            this.food!.picture!.base64 = result.split(',').pop() as string;
            this.loadImageFromFood();
        };
    }

    updateFood(): void {
        if (!this.food || !this.food.picture?.base64) {
            alert('Gericht kann nicht ohne Bild gespeichert werden!');
            setTimeout(() => this.router.navigate(['/foods']), 0);
            return;
        }

        this.food.allergens = [];
        if (this.allergens) {
            for (let i = 0; i < this.allergensChecked.length; i++) {
                if (this.allergensChecked[i]) {
                    this.food.allergens.push(this.allergens[i].shortname);
                }
            }
        }

        alert('Änderungen bestehender Gerichte werden vom Backend derzeit nicht unterstützt.');
        setTimeout(() => this.router.navigate(['/foods']), 0);
    }
}
