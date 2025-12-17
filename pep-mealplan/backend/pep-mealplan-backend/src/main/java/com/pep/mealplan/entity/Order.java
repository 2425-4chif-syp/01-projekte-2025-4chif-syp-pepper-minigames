package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Order extends PanacheEntity {

    // -----------------------------
    // RELATIONEN
    // -----------------------------

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    public Person person;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mealplan_id")
    public MealPlan mealPlan;

    // -----------------------------
    // AUSGEWÄHLTE SPEISEN (IDs)
    // -----------------------------

    @ManyToOne
    @JoinColumn(name = "starter_id")
    public Food selectedStarter;

    @ManyToOne
    @JoinColumn(name = "main_id")
    public Food selectedMain;

    @ManyToOne
    @JoinColumn(name = "dessert_id")
    public Food selectedDessert;

    // -----------------------------
    // TIMESTAMP
    // -----------------------------

    @Column(nullable = false)
    public LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
