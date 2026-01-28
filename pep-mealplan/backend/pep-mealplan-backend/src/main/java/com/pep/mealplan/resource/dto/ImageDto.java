package com.pep.mealplan.resource.dto;

public record ImageDto(
    Long id,
    String base64Image,
    String imageUrl,
    String description
) {}
