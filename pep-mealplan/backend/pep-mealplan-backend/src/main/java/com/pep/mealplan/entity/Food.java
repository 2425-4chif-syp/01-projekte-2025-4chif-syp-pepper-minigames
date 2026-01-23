package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "pe_food")
public class Food extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id; // passt zu pe_food.id (identity)

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String type; // soup | main | dessert

    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY)
    public Set<FoodAllergen> allergens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pictureid") // <-- genau so heißt es in der DB
    public Picture picture;
}
