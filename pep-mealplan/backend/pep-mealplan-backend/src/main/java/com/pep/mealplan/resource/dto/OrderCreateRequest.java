package com.pep.mealplan.resource.dto;

import java.time.LocalDate;

public class OrderCreateRequest {

    public Long personId;
    public LocalDate date;

    // main meal selections
    public Long lunchMainId;
    public Long dinnerMainId;

    // Optional: falls später benötigt
    public Long mealPlanId;
}
