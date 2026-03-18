package com.pep.mealplan.service;

import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.repository.MealPlanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class MealPlanService {

    @Inject
    MealPlanRepository repository;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    /**
     * Liefert einen Tagesplan anhand Woche + Wochentag
     * weekDay: 0 = Montag ... 6 = Sonntag
     */
    public MealPlan getByWeekAndDay(int weekNumber, int weekDay) {
        return repository.find(
                "weekNumber = ?1 and weekDay = ?2",
                weekNumber,
                weekDay
        ).firstResult();
    }

    /**
     * Liefert alle Tage einer Woche (MO–SO)
     */
    public List<MealPlan> getByWeek(int weekNumber) {
        return repository.list("weekNumber", weekNumber);
    }

    // -------------------------------------------------
    // WRITE (UPSERT)
    // -------------------------------------------------

    /**
     * Legt einen Tagesplan an oder überschreibt ihn,
     * falls Woche + Tag bereits existieren
     */
    @Transactional
    public MealPlan upsertDay(MealPlan plan) {

        MealPlan existing = getByWeekAndDay(
                plan.weekNumber,
                plan.weekDay
        );

        if (existing != null) {
            existing.soup = plan.soup;
            existing.lunch1 = plan.lunch1;
            existing.lunch2 = plan.lunch2;
            existing.lunchDessert = plan.lunchDessert;
            existing.dinner1 = plan.dinner1;
            existing.dinner2 = plan.dinner2;
            return existing;
        }

        repository.persist(plan);
        return plan;
    }

    /**
     * Speichert eine komplette Woche (MO–SO)
     */
    @Transactional
    public void upsertWeek(List<MealPlan> plans) {
        for (MealPlan plan : plans) {
            upsertDay(plan);
        }
    }

    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
