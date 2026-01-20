import { Component, OnInit, signal } from '@angular/core';
import { DayPlan, WeekPlan, MenuAPIService } from '../menu-api.service';
import { UserAPIService, Resident } from '../residents-api.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FileSaverService } from 'ngx-filesaver';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MultiSelectModule } from 'primeng/multiselect';
import { ButtonModule } from 'primeng/button';
import { ListboxModule } from 'primeng/listbox';
import { NgClass } from '@angular/common';

interface PersonOption {
    name: string;
    code: string;
    id: number;
    hasSelection?: boolean;
    missingCount?: number;
}

@Component({
    selector: 'app-week-plan-management',
    templateUrl: './week-plan-management.component.html',
    styleUrls: ['./week-plan-management.component.scss'],
    imports: [
        ProgressSpinnerModule,
        ReactiveFormsModule,
        FormsModule,
        MultiSelectModule,
        ButtonModule,
        ListboxModule,
        NgClass
    ],
    standalone: true
})
export class WeekPlanManagementComponent implements OnInit {
    private readonly STORAGE_KEY = 'pepper_resident_week_selections';

    WEEK_DAYS = ['MO', 'DI', 'MI', 'DO', 'FR', 'SA', 'SO'];

    activeWeekIndex = 0; // 0..3
    private baseMonday: Date;

    weekStart: string;

    currentWeekPlan: WeekPlan = {} as WeekPlan;
    currentWeekPlanCopy: WeekPlan = {} as WeekPlan;

    weekPlanLoading = true;

    residents = signal<Resident[]>([]);
    isloading = signal<boolean>(true);

    personSelectorList: PersonOption[] = [];
    selectedPersonOption: PersonOption | null = null;

    constructor(
        private menuApiService: MenuAPIService,
        private residentApiService: UserAPIService,
        private fileSaverService: FileSaverService
    ) {
        this.baseMonday = this.getThisWeeksMonday();
        this.weekStart = this.toIsoDate(this.baseMonday);
    }

    ngOnInit(): void {
        this.residentApiService.getResidents().subscribe({
            next: (res: Resident[]) => {
                this.residents.set(res);

                this.personSelectorList = this.residents().map((r) => ({
                    name: `${r.Firstname} ${r.Lastname}`,
                    code: r.id!.toString(),
                    id: r.id!,
                    hasSelection: false,
                    missingCount: 0
                }));

                this.isloading.set(false);
                this.loadWeekPlan(new Date(this.weekStart));
            },
            error: (err) => {
                alert(err);
            }
        });
    }

    // =========================
    // WEEK SWITCH
    // =========================

    prevWeek(): void {
        if (this.activeWeekIndex > 0) {
            this.activeWeekIndex--;
            this.applyWeekIndex();
        }
    }

    nextWeek(): void {
        if (this.activeWeekIndex < 3) {
            this.activeWeekIndex++;
            this.applyWeekIndex();
        }
    }

    private applyWeekIndex(): void {
        const newMonday = new Date(this.baseMonday);
        newMonday.setDate(newMonday.getDate() + this.activeWeekIndex * 7);

        this.weekStart = this.toIsoDate(newMonday);
        this.loadWeekPlan(newMonday);
    }

    private toIsoDate(d: Date): string {
        return d.toISOString().split('T')[0];
    }

    // =========================
    // WORD-EXPORT
    // =========================

    exportAsWord(): void {
        this.menuApiService.getExportedWord(this.weekStart).subscribe({
            next: (response) => {
                const contentDisposition = response.headers.get('content-disposition');
                let filename = 'Speiseplan.docx';

                if (contentDisposition) {
                    const matches =
                        /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDisposition);
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

    // =========================
    // DATUM
    // =========================

    dateCleaned(index: number): string {
        const dateForDay = new Date(this.weekStart);
        dateForDay.setDate(dateForDay.getDate() + index);

        const day = dateForDay.getDate();
        const month = dateForDay.getMonth() + 1;

        const dayString = day < 10 ? '0' + day : day.toString();
        const monthString = month < 10 ? '0' + month : month.toString();

        return `${dayString}. ${monthString}`;
    }

    private getThisWeeksMonday(base: Date = new Date()): Date {
        const d = new Date(base);
        const day = d.getDay();
        const diff = d.getDate() - day + (day === 0 ? -6 : 1);
        d.setDate(diff);
        d.setHours(12, 0, 0, 0);
        return d;
    }

    // =========================
    // LOAD WEEK PLAN
    // =========================

    private loadWeekPlan(date: Date): void {
        this.weekPlanLoading = true;

        this.menuApiService.getWeekPlan(date).subscribe((data) => {
            this.currentWeekPlan = data;

            this.currentWeekPlan.dayPlans.forEach((dp) => {
                dp.selectedMenu = dp.selectedMenu ?? null;
                dp.selectedEvening = dp.selectedEvening ?? null;
            });

            this.currentWeekPlanCopy = JSON.parse(JSON.stringify(this.currentWeekPlan));
            this.weekPlanLoading = false;

            // Badges neu für aktuelle Woche
            this.refreshPersonHasSelectionFlags();

            // Wenn Person gewählt -> apply
            if (this.selectedPersonOption) {
                this.applySelectionsForSelectedPerson();
            }
        });
    }

    // =========================
    // STORAGE
    // =========================

    private loadStoredSelections(): any {
        const raw = localStorage.getItem(this.STORAGE_KEY);
        if (!raw) return {};
        try {
            return JSON.parse(raw);
        } catch {
            return {};
        }
    }

    private saveStoredSelections(data: any): void {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(data));
    }

    private persistCurrentSelectionsForSelectedPerson(): void {
        if (!this.selectedPersonOption) return;

        const storage = this.loadStoredSelections();
        const weekKey = this.weekStart;

        if (!storage[weekKey]) storage[weekKey] = {};

        const residentId = String(this.selectedPersonOption.id);

        const daySelections: any = {};
        this.currentWeekPlan.dayPlans.forEach((dp, index) => {
            daySelections[index] = {
                selectedMenu: dp.selectedMenu ?? null,
                selectedEvening: dp.selectedEvening ?? null
            };
        });

        storage[weekKey][residentId] = daySelections;
        this.saveStoredSelections(storage);

        this.refreshPersonHasSelectionFlags();
    }

    private applySelectionsForSelectedPerson(): void {
        this.currentWeekPlan.dayPlans.forEach((dp) => {
            dp.selectedMenu = null;
            dp.selectedEvening = null;
        });

        if (!this.selectedPersonOption) return;

        const storage = this.loadStoredSelections();
        const weekKey = this.weekStart;
        const residentId = String(this.selectedPersonOption.id);

        const storedWeek = storage[weekKey]?.[residentId];
        if (!storedWeek) return;

        Object.keys(storedWeek).forEach((idxStr) => {
            const index = Number(idxStr);
            const dp = this.currentWeekPlan.dayPlans[index];
            if (!dp) return;

            const sel = storedWeek[idxStr];
            dp.selectedMenu = sel.selectedMenu ?? null;
            dp.selectedEvening = sel.selectedEvening ?? null;
        });
    }

    private refreshPersonHasSelectionFlags(): void {
        const storage = this.loadStoredSelections();
        const weekKey = this.weekStart;
        const weekData = storage[weekKey] || {};

        const totalSlots = (this.currentWeekPlan?.dayPlans?.length ?? 7) * 2;

        this.personSelectorList.forEach((p) => {
            const residentWeek = weekData[String(p.id)];
            const hasAny = !!residentWeek;

            let selectedCount = 0;

            if (residentWeek) {
                Object.keys(residentWeek).forEach((idxStr) => {
                    const sel = residentWeek[idxStr];
                    if (sel?.selectedMenu) selectedCount++;
                    if (sel?.selectedEvening) selectedCount++;
                });
            }

            p.hasSelection = hasAny;
            p.missingCount = hasAny ? Math.max(0, totalSlots - selectedCount) : 0;
        });
    }

    // =========================
    // SAVE BUTTON STATE
    // =========================

    hasAnySelectionForSelectedPerson(): boolean {
        if (!this.selectedPersonOption) return false;

        return this.currentWeekPlan.dayPlans?.some(
            (dp) => !!dp.selectedMenu || !!dp.selectedEvening
        );
    }

    // =========================
    // UI HANDLER
    // =========================

    onPersonSelected(person: PersonOption | null): void {
        this.selectedPersonOption = person;
        this.applySelectionsForSelectedPerson();
    }

    onSaveSelections(): void {
        this.persistCurrentSelectionsForSelectedPerson();
    }

    // =========================
    // TOGGLES (wieder mit Persist!)
    // =========================

    toggleMenu(dayPlan: DayPlan, menu: 'one' | 'two'): void {
        dayPlan.selectedMenu = dayPlan.selectedMenu === menu ? null : menu;
        this.persistCurrentSelectionsForSelectedPerson();
    }

    toggleEvening(dayPlan: DayPlan, evening: 'one' | 'two'): void {
        dayPlan.selectedEvening = dayPlan.selectedEvening === evening ? null : evening;
        this.persistCurrentSelectionsForSelectedPerson();
    }
}
