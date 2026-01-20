package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"weekNumber", "weekDay"})
        }
)
public class MealPlan extends PanacheEntity {

    @Column(nullable = false)
    public int weekNumber;   // 1–4 (4-Wochen-Zyklus)

    @Column(nullable = false)
    public int weekDay;      // 0 = Montag … 6 = Sonntag

    @ManyToOne
    public Food soup;

    @ManyToOne
    public Food lunch1;

    @ManyToOne
    public Food lunch2;

    @ManyToOne
    public Food lunchDessert;

    @ManyToOne
    public Food dinner1;

    @ManyToOne
    public Food dinner2;
}
