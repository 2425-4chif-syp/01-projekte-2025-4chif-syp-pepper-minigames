package com.pep.mealplan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "foodallergen")
public class FoodAllergen {

    @EmbeddedId
    public FoodAllergenId id;

    @ManyToOne
    @MapsId("foodId")
    @JoinColumn(name = "foodid")
    public Food food;

    @ManyToOne
    @MapsId("allergenShortname")
    @JoinColumn(name = "allergenshortname")
    public Allergen allergen;

    public FoodAllergen() {}

    public FoodAllergen(Food food, Allergen allergen) {
        this.food = food;
        this.allergen = allergen;
        this.id = new FoodAllergenId(food.id, allergen.shortname);
    }
}
