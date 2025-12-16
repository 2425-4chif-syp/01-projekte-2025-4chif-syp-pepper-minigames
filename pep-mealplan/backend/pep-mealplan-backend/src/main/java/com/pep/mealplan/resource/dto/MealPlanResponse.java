package com.pep.mealplan.resource.dto;

import com.pep.mealplan.entity.MealPlan;

import java.time.LocalDate;
import java.util.List;

public class MealPlanResponse {

    public Long id;
    public LocalDate date;

    public List<Long> starters;
    public List<Long> mains;
    public List<Long> desserts;

    public static MealPlanResponse fromEntity(MealPlan mp) {
        MealPlanResponse dto = new MealPlanResponse();

        dto.id = mp.id;
        dto.date = mp.date;

        dto.starters = mp.starters.stream()
                .map(f -> f.id)
                .toList();

        dto.mains = mp.mains.stream()
                .map(f -> f.id)
                .toList();

        dto.desserts = mp.desserts.stream()
                .map(f -> f.id)
                .toList();

        return dto;
    }
}
