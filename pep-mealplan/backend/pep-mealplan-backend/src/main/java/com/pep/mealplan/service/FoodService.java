package com.pep.mealplan.service;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.Picture;
import com.pep.mealplan.repository.FoodRepository;
import com.pep.mealplan.repository.PictureRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FoodService {

    @Inject
    FoodRepository repository;

    @Inject
    PictureRepository pictureRepository;

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
        // Wenn ein Picture mitgeschickt wurde, zuerst speichern
        if (food.picture != null && food.picture.id == null) {
            pictureRepository.persist(food.picture);
        }
        repository.persist(food);
        return food;
    }

    @Transactional
    public Food update(Long id, Food food) {
        Food existing = repository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.name = food.name;
        existing.type = food.type;
        if (food.picture != null) {
            if (food.picture.id == null) {
                pictureRepository.persist(food.picture);
            }
            existing.picture = food.picture;
        }
        // Initialize lazy collection before session closes
        existing.allergens.size();
        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}
