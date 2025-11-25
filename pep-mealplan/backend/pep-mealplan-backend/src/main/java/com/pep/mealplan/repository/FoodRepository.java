package com.pep.mealplan.repository;

import com.pep.mealplan.entity.Food;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FoodRepository implements PanacheRepository<Food> {
}
