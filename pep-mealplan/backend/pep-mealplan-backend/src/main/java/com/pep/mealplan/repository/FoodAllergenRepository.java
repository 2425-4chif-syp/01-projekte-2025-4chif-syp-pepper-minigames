package com.pep.mealplan.repository;

import com.pep.mealplan.entity.FoodAllergen;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FoodAllergenRepository implements PanacheRepository<FoodAllergen> {

    public boolean exists(Long foodId, String allergenShortname) {
        return count("id.foodId = ?1 and id.allergenShortname = ?2",
                foodId, allergenShortname) > 0;
    }
}
