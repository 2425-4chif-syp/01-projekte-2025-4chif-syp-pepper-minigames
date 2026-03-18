package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pe_person")
public class Person extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    public Long id;

    @Column(name = "p_first_name")
    public String firstname;

    @Column(name = "p_last_name")
    public String lastname;

    @Column(name = "p_dob")
    public LocalDate dob;

    @Column(name = "p_face_id")
    public String faceId;
}
