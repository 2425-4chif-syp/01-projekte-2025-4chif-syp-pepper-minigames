package com.pep.mealplan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "pe_food")
public class Food extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String type; // soup | main | dessert

    @JsonIgnore
    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY)
    public Set<FoodAllergen> allergens;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "f_i_id")
    public Picture picture;
}
