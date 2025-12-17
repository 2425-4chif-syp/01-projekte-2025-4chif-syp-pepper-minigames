package com.pep.mealplan.service;

import com.pep.mealplan.entity.Person;
import com.pep.mealplan.repository.PersonRepository;
import com.pep.mealplan.resource.dto.PersonCreateRequest;
import com.pep.mealplan.resource.dto.PersonResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    PersonRepository personRepo;

    // GET ALL
    public List<PersonResponse> getAll() {
        return personRepo.listAll()
                .stream()
                .map(PersonResponse::fromEntity)
                .toList();
    }

    // GET BY ID
    public PersonResponse getById(Long id) {
        Person p = personRepo.findById(id);
        return (p == null) ? null : PersonResponse.fromEntity(p);
    }

    // CREATE
    @Transactional
    public PersonResponse create(PersonCreateRequest req) {
        Person p = new Person();
        p.firstname = req.firstname;
        p.lastname = req.lastname;
        p.email = req.email;
        p.faceId = req.faceId;

        personRepo.persist(p);
        return PersonResponse.fromEntity(p);
    }

    // UPDATE
    @Transactional
    public PersonResponse update(Long id, PersonCreateRequest req) {
        Person p = personRepo.findById(id);
        if (p == null) return null;

        if (req.firstname != null) p.firstname = req.firstname;
        if (req.lastname != null) p.lastname = req.lastname;
        if (req.email != null) p.email = req.email;
        if (req.faceId != null) p.faceId = req.faceId;

        return PersonResponse.fromEntity(p);
    }

    // DELETE
    @Transactional
    public boolean delete(Long id) {
        return personRepo.deleteById(id);
    }
}
