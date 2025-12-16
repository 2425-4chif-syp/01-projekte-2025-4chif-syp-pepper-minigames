package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.util.Set;

@Entity
public class Food extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    public String type;

    public String description;

    @OneToMany(mappedBy = "food")
    Set<FoodAllergen> allergens;

    @ManyToOne
    @JoinColumn(name = "PictureId")
    public Picture picture;


    @Column(nullable = false)
    public Integer price; // Cent or integer value, adjust later
}
