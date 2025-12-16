package com.pep.mealplan.service;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.repository.FoodRepository;
import com.pep.mealplan.repository.MealPlanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class MealPlanService {

    @Inject
    MealPlanRepository mealPlanRepo;

    @Inject
    FoodRepository foodRepo;

    // -------------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------------
    public List<MealPlan> getAll() {
        return mealPlanRepo.listAll();
    }

    // -------------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------------
    public MealPlan getById(Long id) {
        return mealPlanRepo.findById(id);
    }

    // -------------------------------------------------------------
    // GET BY DATE
    // -------------------------------------------------------------
    public MealPlan getByDate(LocalDate date) {
        return mealPlanRepo.find("date", date).firstResult();
    }

    // -------------------------------------------------------------
    // CREATE MEALPLAN
    // -------------------------------------------------------------
    @Transactional
    public MealPlan create(LocalDate date) {
        MealPlan mp = new MealPlan();
        mp.date = date;

        mp.starters = new java.util.ArrayList<>();
        mp.mains = new java.util.ArrayList<>();
        mp.desserts = new java.util.ArrayList<>();

        mealPlanRepo.persist(mp);
        return mp;
    }

    // -------------------------------------------------------------
    // ADD FOOD TO A CATEGORY
    // -------------------------------------------------------------
    @Transactional
    public MealPlan addFood(Long mealPlanId, Long foodId, String category) {

        MealPlan mp = mealPlanRepo.findById(mealPlanId);
        Food food = foodRepo.findById(foodId);

        if (mp == null || food == null)
            return null;

        switch (category.toLowerCase()) {
            case "starter" -> mp.starters.add(food);
            case "main" -> mp.mains.add(food);
            case "dessert" -> mp.desserts.add(food);
            default -> throw new IllegalArgumentException("Unknown category: " + category);
        }

        return mp;
    }

    // -------------------------------------------------------------
    // REMOVE FOOD FROM CATEGORY
    // -------------------------------------------------------------
    @Transactional
    public MealPlan removeFood(Long mealPlanId, Long foodId, String category) {

        MealPlan mp = mealPlanRepo.findById(mealPlanId);
        Food food = foodRepo.findById(foodId);

        if (mp == null || food == null)
            return null;

        switch (category.toLowerCase()) {
            case "starter" -> mp.starters.remove(food);
            case "main" -> mp.mains.remove(food);
            case "dessert" -> mp.desserts.remove(food);
            default -> throw new IllegalArgumentException("Unknown category: " + category);
        }

        return mp;
    }
}
