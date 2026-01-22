package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "food")
public class Food extends PanacheEntity {

    @Column(name="name", nullable=false)
    public String name;

    @Column(name="type", nullable=false)
    public String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pictureid")
    public Picture picture;
}
