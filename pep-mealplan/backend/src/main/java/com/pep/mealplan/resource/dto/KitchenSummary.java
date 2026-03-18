package com.pep.mealplan.resource.dto;

import java.util.Map;

public class KitchenSummary {

    public Map<String, Long> lunch;
    public Map<String, Long> dinner;

    public KitchenSummary(Map<String, Long> lunch, Map<String, Long> dinner) {
        this.lunch = lunch;
        this.dinner = dinner;
    }
}
