import {Component, OnInit, signal} from '@angular/core';
import { DayPlan, WeekPlan, MenuAPIService } from '../menu-api.service';
import {UserAPIService} from "../residents-api.service";
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { FileSaverService } from 'ngx-filesaver';
import {Resident} from "../residents-api.service";
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {MultiSelectModule} from "primeng/multiselect";
import {ButtonModule} from "primeng/button";

@Component({
    selector: 'app-week-plan-management',
    templateUrl: './week-plan-management.component.html',
    styleUrls: ['./week-plan-management.component.scss'],
    imports: [ProgressSpinnerModule, ReactiveFormsModule, FormsModule, MultiSelectModule, ButtonModule],
    standalone: true
})
export class WeekPlanManagementComponent implements OnInit {
    WEEK_DAYS = ['MO', 'DI', 'MI', 'DO', 'FR', 'SA', 'SO'];

    selectedDate: string;
    currentWeekPlan: WeekPlan = {} as WeekPlan;
    currentWeekPlanCopy: WeekPlan = {} as WeekPlan;
    specialDayPlans: DayPlan[] = [];

    weekPlanLoading = true;

    residents = signal<Resident[]>([]);
    isloading = signal<boolean>(true);
    selectedResident = signal<Resident|null>(null);

    personList = [
        {name: 'New York', code: 'NY'},
        {name: 'Rome', code: 'RM'},
        {name: 'London', code: 'LDN'},
        {name: 'Istanbul', code: 'IST'},
        {name: 'Paris', code: 'PRS'}
    ];



    constructor(
        private menuApiService: MenuAPIService,
        private residentApiService:UserAPIService,
        private fileSaverService: FileSaverService
    ) {
        const thisWeeksMonday = this.getThisWeeksMonday();
        this.selectedDate = thisWeeksMonday.toISOString().split('T')[0];
    }

    ngOnInit(): void {
        this.residentApiService.getResidents().subscribe({
            next: (res:Resident[]) => {
                this.residents.set(res);
                this.isloading.set(false);
            },
            error: (err) => {
                alert(err);
            }
        })

        this.loadWeekPlan(new Date(this.selectedDate));
    }

    exportAsWord(): void {
        this.menuApiService.getExportedWord(this.selectedDate).subscribe({
            next: (response) => {
                const contentDisposition = response.headers.get('content-disposition');
                let filename = 'Speiseplan.docx';

                if (contentDisposition) {
                    const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(
                        contentDisposition
                    );
                    if (matches?.[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }
                }

                this.fileSaverService.save(response.body, filename);
            },
            error: (error) => {
                console.error('Fehler beim Word-Export:', error);
                alert('Fehler beim Herunterladen des Word-Dokuments!');
            }
        });
    }

    dateChange(): void {
        this.specialDayPlans = [];
        const date = this.getThisWeeksMonday(new Date(this.selectedDate));
        this.selectedDate = date.toISOString().split('T')[0];
        this.loadWeekPlan(date);
    }

    dateCleaned(index: number): string {
        const dateForDay = new Date(this.selectedDate);
        dateForDay.setDate(dateForDay.getDate() + index);

        const day = dateForDay.getDate();
        const month = dateForDay.getMonth() + 1;

        const dayString = day < 10 ? '0' + day : day.toString();
        const monthString = month < 10 ? '0' + month : month.toString();

        return `${dayString}. ${monthString}`;
    }

    changeDate(amount: number): void {
        const newDate = new Date(this.selectedDate);
        newDate.setDate(newDate.getDate() + amount);
        this.selectedDate = newDate.toISOString().split('T')[0];
        this.dateChange();
    }

    resetDate(): void {
        const thisWeeksMonday = this.getThisWeeksMonday();
        this.selectedDate = thisWeeksMonday.toISOString().split('T')[0];
        this.dateChange();
    }


    private getThisWeeksMonday(base: Date = new Date()): Date {
        const d = new Date(base);
        const day = d.getDay();
        const diff = d.getDate() - day + (day === 0 ? -6 : 1);
        d.setDate(diff);
        d.setHours(12, 0, 0, 0);
        return d;
    }


    private loadWeekPlan(date: Date): void {
        this.weekPlanLoading = true;
        this.menuApiService.getWeekPlan(date).subscribe((data) => {
            this.currentWeekPlan = data;
            this.currentWeekPlanCopy = JSON.parse(
                JSON.stringify(this.currentWeekPlan)
            );
            this.weekPlanLoading = false;
        });
    }
}
