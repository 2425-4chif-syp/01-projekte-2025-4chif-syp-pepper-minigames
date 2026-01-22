package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
@Entity
@Table(name = "pe_person")
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
