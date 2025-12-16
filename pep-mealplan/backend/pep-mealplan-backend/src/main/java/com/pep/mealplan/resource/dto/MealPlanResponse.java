package com.pep.mealplan.resource.dto;

import com.pep.mealplan.entity.Food;

import java.time.LocalDate;
import java.util.List;

public class MealPlanResponse {
    public Long id;
    public LocalDate date;

    public List<Food> starters;
    public List<Food> mains;
    public List<Food> desserts;
}
