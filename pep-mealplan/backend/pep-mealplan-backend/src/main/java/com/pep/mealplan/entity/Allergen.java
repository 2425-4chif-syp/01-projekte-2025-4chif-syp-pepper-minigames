package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name="allergen")
public class Allergen extends PanacheEntityBase {

    @Id
    @Column(name="shortname", length=5)
    public String shortname;

    @Column(name="description", nullable=false)
    public String description;
}
