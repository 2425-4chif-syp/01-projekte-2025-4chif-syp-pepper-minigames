package com.pep.mealplan.entity;

import jakarta.persistence.*;

@Entity
public class FoodAllergen {

    @EmbeddedId
    public FoodAllergenId id;

    @ManyToOne
    @MapsId("foodId")   // Verbindet id.foodId ←→ Food.id
    @JoinColumn(name = "FoodId")
    public Food food;

    @ManyToOne
    @MapsId("allergenShortname") // id.allergenShortname ←→ Allergen.shortname
    @JoinColumn(name = "AllergenShortname")
    public Allergen allergen;

    public FoodAllergen() {}

    public FoodAllergen(Food food, Allergen allergen) {
        this.food = food;
        this.allergen = allergen;
        this.id = new FoodAllergenId(food.id, allergen.shortname);
    }
}
