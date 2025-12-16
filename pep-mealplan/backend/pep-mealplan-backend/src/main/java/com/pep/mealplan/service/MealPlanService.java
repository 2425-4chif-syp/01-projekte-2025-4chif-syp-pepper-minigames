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


    public MealPlan getByDate(LocalDate date) {
        return mealPlanRepo.find("date", date).firstResult();
    }

    public List<MealPlan> getWeek(LocalDate startDate) {
        LocalDate end = startDate.plusDays(6);
        return mealPlanRepo.list("date between ?1 and ?2", startDate, end);
    }

    @Transactional
    public MealPlan createMealPlan(LocalDate date, Long starterId, Long mainId, Long dessertId) {

        MealPlan plan = new MealPlan();
        plan.date = date;

        plan.starter = foodRepo.findById(starterId);
        plan.main = foodRepo.findById(mainId);
        plan.dessert = foodRepo.findById(dessertId);

        mealPlanRepo.persist(plan);
        return plan;
    }


    @Transactional
    public MealPlan updateMealPlan(LocalDate date, Long starterId, Long mainId, Long dessertId) {

        MealPlan plan = getByDate(date);
        if (plan == null) return null;

        if (starterId != null) plan.starter = foodRepo.findById(starterId);
        if (mainId != null) plan.main = foodRepo.findById(mainId);
        if (dessertId != null) plan.dessert = foodRepo.findById(dessertId);

        return plan;
    }

    @Transactional
    public boolean delete(LocalDate date) {
        return mealPlanRepo.delete("date", date) > 0;
    }
}
