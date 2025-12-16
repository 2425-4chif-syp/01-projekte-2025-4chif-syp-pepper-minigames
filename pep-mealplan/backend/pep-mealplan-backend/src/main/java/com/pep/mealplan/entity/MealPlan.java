package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class MealPlan extends PanacheEntity {

    @Column(nullable = false)
    public LocalDate date;   // Der Tag, für den der Speiseplan gilt

    // -------- Starter (Vorspeisen) --------
    @ManyToMany
    @JoinTable(
            name = "mealplan_starters",
            joinColumns = @JoinColumn(name = "mealplan_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    public List<Food> starters;

    // -------- Main Dishes (Hauptspeisen) --------
    @ManyToMany
    @JoinTable(
            name = "mealplan_mains",
            joinColumns = @JoinColumn(name = "mealplan_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    public List<Food> mains;

    // -------- Desserts (Nachspeisen) --------
    @ManyToMany
    @JoinTable(
            name = "mealplan_desserts",
            joinColumns = @JoinColumn(name = "mealplan_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    public List<Food> desserts;
}
