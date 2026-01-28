package com.pep.mealplan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "pe_image")
public class Picture extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_id")
    public Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "i_p_id")
    public Person person;

    @Column(name = "i_description")
    public String description;

    @Column(name = "i_url")
    public String url;

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "i_image")
    public byte[] image;

    public Picture() {}
}
