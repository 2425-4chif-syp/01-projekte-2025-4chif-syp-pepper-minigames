package com.pep.mealplan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FoodAllergenId implements Serializable {

    @Column(name = "FoodId")
    public Long foodId;

    @Column(name = "AllergenShortname")
    public String allergenShortname;

    public FoodAllergenId() {}

    public FoodAllergenId(Long foodId, String allergenShortname) {
        this.foodId = foodId;
        this.allergenShortname = allergenShortname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodAllergenId)) return false;
        FoodAllergenId that = (FoodAllergenId) o;
        return Objects.equals(foodId, that.foodId) &&
                Objects.equals(allergenShortname, that.allergenShortname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodId, allergenShortname);
    }
}
