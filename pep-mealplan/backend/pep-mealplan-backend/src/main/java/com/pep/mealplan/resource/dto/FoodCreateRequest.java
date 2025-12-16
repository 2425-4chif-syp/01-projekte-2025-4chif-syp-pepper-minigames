package com.pep.mealplan.resource.dto;

import java.util.List;

public class FoodCreateRequest {
    public String name;
    public String type;
    public String description;
    public Integer price;

    // Beispiel: ["A", "C", "G"]
    public List<String> allergens;

    public PictureCreateRequest picture;
}
