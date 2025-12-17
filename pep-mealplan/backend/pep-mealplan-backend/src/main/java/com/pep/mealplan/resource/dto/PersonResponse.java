package com.pep.mealplan.resource.dto;

import com.pep.mealplan.entity.Person;

public class PersonResponse {

    public Long id;
    public String firstname;
    public String lastname;
    public String email;
    public String faceId;

    public static PersonResponse fromEntity(Person p) {
        PersonResponse r = new PersonResponse();
        r.id = p.id;
        r.firstname = p.firstname;
        r.lastname = p.lastname;
        r.email = p.email;
        r.faceId = p.faceId;
        return r;
    }
}
