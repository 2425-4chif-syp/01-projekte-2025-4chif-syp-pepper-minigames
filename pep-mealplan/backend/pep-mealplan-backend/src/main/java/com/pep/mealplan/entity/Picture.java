package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pe_picture")
public class Picture extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String mediaType;

    @Column(columnDefinition = "TEXT")
    public String base64;

    public Picture() {}
}
