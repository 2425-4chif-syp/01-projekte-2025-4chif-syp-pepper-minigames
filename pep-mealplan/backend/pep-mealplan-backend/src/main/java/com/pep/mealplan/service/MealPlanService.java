package com.pep.mealplan.service;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.repository.FoodRepository;
import com.pep.mealplan.repository.MealPlanRepository;
import com.pep.mealplan.resource.dto.MealPlanCreateRequest;
import com.pep.mealplan.resource.dto.MealPlanResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MealPlanService {

    @Inject
    MealPlanRepository mealPlanRepo;

    @Inject
    FoodRepository foodRepo;

    // -------------------------------------------------------------
    // GET ALL → returns LIST of DTOs
    // -------------------------------------------------------------
    public List<MealPlanResponse> getAll() {
        return mealPlanRepo.listAll()
                .stream()
                .map(MealPlanResponse::fromEntity)
                .toList();
    }

    // -------------------------------------------------------------
    // GET BY ID → returns DTO
    // -------------------------------------------------------------
    public MealPlanResponse getById(Long id) {
        MealPlan mp = mealPlanRepo.findById(id);
        if (mp == null) return null;
        return MealPlanResponse.fromEntity(mp);
    }

    // -------------------------------------------------------------
    // GET BY DATE → returns DTO
    // -------------------------------------------------------------
    public MealPlanResponse getByDate(LocalDate date) {
        MealPlan mp = mealPlanRepo.find("date", date).firstResult();
        if (mp == null) return null;
        return MealPlanResponse.fromEntity(mp);
    }

    // -------------------------------------------------------------
    // CREATE FROM DTO
    // -------------------------------------------------------------
    @Transactional
    public MealPlanResponse createFromDto(MealPlanCreateRequest dto) {

        MealPlan mp = new MealPlan();
        mp.date = dto.date;
        mp.starters = new ArrayList<>();
        mp.mains = new ArrayList<>();
        mp.desserts = new ArrayList<>();

        // Starter Foods
        if (dto.starters != null) {
            for (Long id : dto.starters) {
                Food f = foodRepo.findById(id);
                if (f != null) mp.starters.add(f);
            }
        }

        // Main Foods
        if (dto.mains != null) {
            for (Long id : dto.mains) {
                Food f = foodRepo.findById(id);
                if (f != null) mp.mains.add(f);
            }
        }

        // Dessert Foods
        if (dto.desserts != null) {
            for (Long id : dto.desserts) {
                Food f = foodRepo.findById(id);
                if (f != null) mp.desserts.add(f);
            }
        }

        mealPlanRepo.persist(mp);
        return MealPlanResponse.fromEntity(mp);
    }

    // -------------------------------------------------------------
    // ADD FOOD
    // -------------------------------------------------------------
    @Transactional
    public MealPlanResponse addFood(Long mealPlanId, Long foodId, String category) {
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

        return MealPlanResponse.fromEntity(mp);
    }

    // -------------------------------------------------------------
    // REMOVE FOOD
    // -------------------------------------------------------------
    @Transactional
    public MealPlanResponse removeFood(Long mealPlanId, Long foodId, String category) {
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

        return MealPlanResponse.fromEntity(mp);
    }
}
