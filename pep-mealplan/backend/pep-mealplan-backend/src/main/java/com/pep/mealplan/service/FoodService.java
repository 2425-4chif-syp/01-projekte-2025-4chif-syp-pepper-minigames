package com.pep.mealplan.service;

import com.pep.mealplan.entity.Allergen;
import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.FoodAllergen;
import com.pep.mealplan.entity.Picture;
import com.pep.mealplan.repository.AllergenRepository;
import com.pep.mealplan.repository.FoodAllergenRepository;
import com.pep.mealplan.repository.FoodRepository;
import com.pep.mealplan.repository.PictureRepository;
import com.pep.mealplan.resource.dto.FoodCreateRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FoodService {

    @Inject
    FoodRepository foodRepo;

    @Inject
    PictureRepository pictureRepository;

    @Inject
    AllergenRepository allergenRepository;

    @Inject
    FoodAllergenRepository foodAllergenRepository;


    // ---------------------------------------
    // BASIC CRUD
    // ---------------------------------------

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


    // ---------------------------------------
    // CREATE WITH DTO (Food + Picture + Allergens)
    // ---------------------------------------

    @Transactional
    public Food createFromDto(FoodCreateRequest dto) {

        // 1) Food speichern
        Food food = new Food();
        food.name = dto.name;
        food.type = dto.type;
        food.description = dto.description;
        food.price = dto.price;
        foodRepo.persist(food);

        // 2) Picture speichern
        if (dto.picture != null) {
            Picture pic = new Picture();
            pic.name = dto.picture.name;
            pic.mediaType = dto.picture.mediaType;
            pic.base64 = dto.picture.base64;
            pic.food = food;
            pictureRepository.persist(pic);
        }

        // 3) Allergene speichern
        if (dto.allergens != null) {
            for (String shortname : dto.allergens) {
                Allergen allergen = allergenRepository.findById(shortname);
                if (allergen != null) {
                    FoodAllergen fa = new FoodAllergen(food, allergen);
                    foodAllergenRepository.persist(fa);
                }
            }
        }

        return food;
    }

}
