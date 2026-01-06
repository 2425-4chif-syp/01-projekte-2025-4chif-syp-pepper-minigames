package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Food extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String type; // soup | main | dessert

    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY)
    public Set<FoodAllergen> allergens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PictureId")
    public Picture picture;
}
