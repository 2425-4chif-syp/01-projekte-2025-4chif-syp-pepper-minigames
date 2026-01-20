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
    FoodRepository repository;

    // ---------------------------------------
    // READ
    // ---------------------------------------

    public List<Food> getAll() {
        return repository.listAll();
    }

    public Food getById(Long id) {
        return repository.findById(id);
    }

    public List<Food> getByType(String type) {
        return repository.list("type", type);
    }

    public List<Food> searchByName(String name) {
        return repository.list(
                "LOWER(name) LIKE ?1",
                "%" + name.toLowerCase() + "%"
        );
    }

    // ---------------------------------------
    // WRITE
    // ---------------------------------------

    @Transactional
    public Food create(Food food) {
        repository.persist(food);
        return food;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}
