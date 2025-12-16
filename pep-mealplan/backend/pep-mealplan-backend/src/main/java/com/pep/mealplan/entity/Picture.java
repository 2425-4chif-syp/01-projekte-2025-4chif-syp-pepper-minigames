package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class Picture extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String mediaType;

    @Column(columnDefinition = "TEXT")
    public String base64;

    @ManyToOne
    @JoinColumn(name = "food_id")
    public Food food;

    public Picture() {}
}
