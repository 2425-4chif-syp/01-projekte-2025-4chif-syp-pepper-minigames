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
        repository.persist(food);
        return food;
    }

    @Transactional
    public Food create(String name, String type, Long pictureId) {
        Food food = new Food();
        food.name = name;
        food.type = type;

        if (pictureId != null) {
            Picture picture = pictureRepository.findById(pictureId);
            food.picture = picture;
        }

        repository.persist(food);
        return food;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}
