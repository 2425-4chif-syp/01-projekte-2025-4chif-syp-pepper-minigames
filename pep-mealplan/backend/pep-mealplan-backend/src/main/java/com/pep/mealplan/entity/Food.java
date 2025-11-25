package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;

import java.util.Set;

@Entity
public class Food extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    public String type;

    public String description;

    @OneToMany(mappedBy = "food")
    Set<FoodAllergen> allergens;

    @Column(nullable = false)
    public Integer price; // Cent or integer value, adjust later
}
