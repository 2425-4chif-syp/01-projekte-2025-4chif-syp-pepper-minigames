package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
public class Allergen extends PanacheEntityBase {

    @Id
    @Column(length = 5)
    public String shortname;

    @Column(nullable = false)
    public String description;


    public Allergen() {}
}
