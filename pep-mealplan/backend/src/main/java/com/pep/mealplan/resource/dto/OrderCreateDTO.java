package com.pep.mealplan.resource.dto;

import java.time.LocalDate;

public class OrderCreateDTO {
    public Long personId;
    public LocalDate date;
    public Long selectedLunchId;
    public Long selectedDinnerId;
}
