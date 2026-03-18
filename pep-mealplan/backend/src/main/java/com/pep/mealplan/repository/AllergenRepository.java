package com.pep.mealplan.repository;

import com.pep.mealplan.entity.Allergen;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AllergenRepository implements PanacheRepositoryBase<Allergen, String> {
}
