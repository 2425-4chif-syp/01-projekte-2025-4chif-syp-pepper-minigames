package com.pep.mealplan.service;

import com.pep.mealplan.entity.Allergen;
import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.FoodAllergen;
import com.pep.mealplan.entity.FoodAllergenId;
import com.pep.mealplan.repository.AllergenRepository;
import com.pep.mealplan.repository.FoodAllergenRepository;
import com.pep.mealplan.repository.FoodRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FoodAllergenService {

    @Inject
    FoodRepository foodRepository;

    @Inject
    AllergenRepository allergenRepository;

    @Inject
    FoodAllergenRepository foodAllergenRepository;

    public List<FoodAllergen> getForFood(Long foodId) {
        return foodAllergenRepository.list("id.foodId", foodId);
    }

    @Transactional
    public void addAllergen(Long foodId, String allergenShortname) {
        Food food = foodRepository.findById(foodId);
        Allergen allergen = allergenRepository.findById(allergenShortname);

        if (food == null || allergen == null)
            throw new IllegalArgumentException("Food oder Allergen nicht gefunden");

        if (foodAllergenRepository.exists(foodId, allergenShortname))
            return;

        FoodAllergen fa = new FoodAllergen(food, allergen);
        foodAllergenRepository.persist(fa);
    }

    @Transactional
    public void removeAllergen(Long foodId, String allergenShortname) {
        foodAllergenRepository.delete("id.foodId = ?1 and id.allergenShortname = ?2",
                foodId, allergenShortname);
    }
}
