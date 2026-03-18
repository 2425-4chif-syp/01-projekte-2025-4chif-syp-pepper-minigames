package com.pep.mealplan.repository;

import com.pep.mealplan.entity.MealPlan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MealPlanRepository implements PanacheRepository<MealPlan> {
}
