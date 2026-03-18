package com.pep.mealplan.resource.dto;

public record FoodCreateDTO(
    String name,
    String type,
    Long pictureId
) {}
