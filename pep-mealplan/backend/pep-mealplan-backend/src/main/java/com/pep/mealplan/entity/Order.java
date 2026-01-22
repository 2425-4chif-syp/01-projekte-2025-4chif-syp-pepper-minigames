package com.pep.mealplan.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="orders", uniqueConstraints=@UniqueConstraint(columnNames={"person_id","order_date"}))
public class Order extends PanacheEntity {

    @ManyToOne(optional=false)
    @JoinColumn(name="person_id", referencedColumnName = "p_id")
    public Person person;

    @Column(name="order_date", nullable=false)
    public LocalDate date;

    @ManyToOne(optional=false)
    @JoinColumn(name="selected_lunch_id")
    public Food selectedLunch;

    @ManyToOne(optional=false)
    @JoinColumn(name="selected_dinner_id")
    public Food selectedDinner;
}
