package com.pep.mealplan.resource.dto;

import java.time.LocalDate;
import java.util.Map;

public class OrderBulkRequest {

    public LocalDate weekStartDate;

    // Map<PersonId, Map<DayIndex, DaySelection>>
    public Map<Long, Map<Integer, DaySelection>> orders;

    public static class DaySelection {
        public Integer selectedMenu;    // 1 | 2 | null
        public Integer selectedEvening; // 1 | 2 | null
    }
}
