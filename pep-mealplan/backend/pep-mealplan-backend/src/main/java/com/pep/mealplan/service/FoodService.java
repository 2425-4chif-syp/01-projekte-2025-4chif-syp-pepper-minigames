package com.pep.mealplan.service;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.repository.FoodRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FoodService {

    @Inject
    FoodRepository foodRepo;

    public List<Food> getAll() {
        return foodRepo.listAll();
    }

    public Food getById(Long id) {
        return foodRepo.findById(id);
    }

    public List<Food> getByType(String type) {
        return foodRepo.list("type", type);
    }

    public List<Food> searchByName(String name, boolean strict) {
        if (strict) {
            return foodRepo.list("name", name);
        } else {
            return foodRepo.list("LOWER(name) LIKE ?1", "%" + name.toLowerCase() + "%");
        }
    }

    @Transactional
    public Food create(Food food) {
        foodRepo.persist(food);
        return food;
    }

    @Transactional
    public Food update(Long id, Food update) {
        Food f = foodRepo.findById(id);
        if (f == null) return null;

        if (update.name != null) f.name = update.name;
        if (update.type != null) f.type = update.type;
        if (update.description != null) f.description = update.description;
        if (update.price != null) f.price = update.price;

        return f;
    }

    @Transactional
    public boolean delete(Long id) {
        return foodRepo.deleteById(id);
    }
}
