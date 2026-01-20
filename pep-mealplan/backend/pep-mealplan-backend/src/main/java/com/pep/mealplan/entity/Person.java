package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
@Entity
public class Person extends PanacheEntity {

    @Column(nullable = false)
    public String firstname;

    @Column(nullable = false)
    public String lastname;

    // optional
    public LocalDate dob;

    // optional
    public String faceId;
}
