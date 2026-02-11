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
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class FoodAllergenService {

    @Inject
    FoodAllergenRepository repository;

    @Inject
    FoodRepository foodRepository;

    @Inject
    AllergenRepository allergenRepository;

    // ---------------------------------------
    // READ
    // ---------------------------------------

    public List<FoodAllergen> getAll() {
        return repository.listAll();
    }

    public List<FoodAllergen> getByFoodId(Long foodId) {
        return repository.list("id.foodId", foodId);
    }

    public List<FoodAllergen> getByAllergenShortname(String shortname) {
        return repository.list("id.allergenShortname", shortname);
    }

    public FoodAllergen getById(Long foodId, String allergenShortname) {
        FoodAllergenId id = new FoodAllergenId(foodId, allergenShortname);
        return repository.find("id", id).firstResult();
    }

    public boolean exists(Long foodId, String allergenShortname) {
        return repository.exists(foodId, allergenShortname);
    }

    // ---------------------------------------
    // WRITE
    // ---------------------------------------

    @Transactional
    public FoodAllergen create(Long foodId, String allergenShortname) {
        Food food = foodRepository.findById(foodId);
        if (food == null) {
            throw new BadRequestException("Food mit ID " + foodId + " existiert nicht");
        }

        Allergen allergen = allergenRepository.findById(allergenShortname);
        if (allergen == null) {
            throw new BadRequestException("Allergen mit Shortname " + allergenShortname + " existiert nicht");
        }

        if (exists(foodId, allergenShortname)) {
            throw new BadRequestException("Verknüpfung existiert bereits");
        }

        FoodAllergen foodAllergen = new FoodAllergen(food, allergen);
        repository.persist(foodAllergen);
        return foodAllergen;
    }

    @Transactional
    public boolean delete(Long foodId, String allergenShortname) {
        FoodAllergenId id = new FoodAllergenId(foodId, allergenShortname);
        FoodAllergen existing = repository.find("id", id).firstResult();
        if (existing == null) {
            return false;
        }
        repository.delete(existing);
        return true;
    }

    @Transactional
    public void deleteAllByFoodId(Long foodId) {
        repository.delete("id.foodId", foodId);
    }
}
