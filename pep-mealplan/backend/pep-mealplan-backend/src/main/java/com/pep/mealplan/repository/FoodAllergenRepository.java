package com.pep.mealplan.repository;

import com.pep.mealplan.entity.FoodAllergen;
import com.pep.mealplan.entity.FoodAllergenId;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FoodAllergenRepository implements PanacheRepository<FoodAllergen> {

    public boolean exists(Long foodId, String allergenShortname) {
        return find("id.foodId = ?1 and id.allergenShortname = ?2",
                foodId, allergenShortname).firstResult() != null;
    }
}
