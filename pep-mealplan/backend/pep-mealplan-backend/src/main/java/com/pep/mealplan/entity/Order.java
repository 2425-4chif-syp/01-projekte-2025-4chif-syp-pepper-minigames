package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_order") // <-- FIX HIER
public class Order extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    public Person person;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mealplan_id")
    public MealPlan mealPlan;

    @ManyToOne
    @JoinColumn(name = "starter_id")
    public Food selectedStarter;

    @ManyToOne
    @JoinColumn(name = "main_id")
    public Food selectedMain;

    @ManyToOne
    @JoinColumn(name = "evening_id")
    public Food selectedEvening;

    @ManyToOne
    @JoinColumn(name = "dessert_id")
    public Food selectedDessert;

    @Column(nullable = false)
    public LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
