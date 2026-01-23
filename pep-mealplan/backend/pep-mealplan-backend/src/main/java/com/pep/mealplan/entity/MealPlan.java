package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(
        name = "pe_mealplan",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"weeknumber", "weekday"})
        }
)
public class MealPlan extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "weeknumber", nullable = false)
    public int weekNumber;

    @Column(name = "weekday", nullable = false)
    public int weekDay;

    @ManyToOne
    @JoinColumn(name = "soup_id")
    public Food soup;

    @ManyToOne
    @JoinColumn(name = "lunch1_id")
    public Food lunch1;

    @ManyToOne
    @JoinColumn(name = "lunch2_id")
    public Food lunch2;

    @ManyToOne
    @JoinColumn(name = "lunchdessert_id")
    public Food lunchDessert;

    @ManyToOne
    @JoinColumn(name = "dinner1_id")
    public Food dinner1;

    @ManyToOne
    @JoinColumn(name = "dinner2_id")
    public Food dinner2;
}
