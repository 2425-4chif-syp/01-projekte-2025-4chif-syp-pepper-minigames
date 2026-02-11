package com.pep.mealplan.service;

import com.pep.mealplan.entity.Allergen;
import com.pep.mealplan.repository.AllergenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AllergenService {

    @Inject
    AllergenRepository repository;

    // ---------------------------------------
    // READ
    // ---------------------------------------

    public List<Allergen> findAll() {
        return repository.listAll();
    }

    public Allergen findByShortname(String shortname) {
        return repository.findById(shortname);
    }

    // ---------------------------------------
    // WRITE
    // ---------------------------------------

    @Transactional
    public Allergen create(Allergen allergen) {
        repository.persist(allergen);
        return allergen;
    }

    @Transactional
    public Allergen update(String shortname, Allergen allergen) {
        Allergen existing = repository.findById(shortname);
        if (existing == null) {
            return null;
        }
        existing.description = allergen.description;
        return existing;
    }

    @Transactional
    public boolean delete(String shortname) {
        return repository.deleteById(shortname);
    }
}
