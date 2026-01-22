package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;   // <- WICHTIG (damit @Table gefunden wird)

@Entity
@Table(name = "picture")
public class Picture extends PanacheEntity {

    @Column(columnDefinition = "TEXT")
    public String base64;

    @Column(nullable = false)
    public String mediaType;

    @Column(nullable = false)
    public String name;
}
