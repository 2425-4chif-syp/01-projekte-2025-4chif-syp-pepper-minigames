package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "MealPlan",
        uniqueConstraints = @UniqueConstraint(columnNames = {"date"})
)
public class MealPlan extends PanacheEntity {

    @Column(nullable = false)
    public LocalDate date;  // Plan für welchen Tag

    @ManyToOne
    @JoinColumn(name = "starter_id")
    public Food starter;    // Vorspeise

    @ManyToOne
    @JoinColumn(name = "main_id")
    public Food main;       // Hauptspeise

    @ManyToOne
    @JoinColumn(name = "dessert_id")
    public Food dessert;    // Nachspeise

    public MealPlan() {}
}
