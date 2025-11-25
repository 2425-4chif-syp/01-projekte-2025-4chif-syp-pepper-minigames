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

    public List<Allergen> findAll() {
        return repository.listAll();
    }

    public Allergen findByShortname(String shortname) {
        return repository.find("shortname", shortname).firstResult();
    }

    @Transactional
    public Allergen create(Allergen allergen) {
        repository.persist(allergen);
        return allergen;
    }
}
