package com.pep.mealplan.resource.dto;

import java.time.LocalDate;
import java.util.List;

public class MealPlanCreateRequest {

    public LocalDate date;

    public List<Long> starters;  // Food IDs
    public List<Long> mains;     // Food IDs
    public List<Long> desserts;  // Food IDs
}
