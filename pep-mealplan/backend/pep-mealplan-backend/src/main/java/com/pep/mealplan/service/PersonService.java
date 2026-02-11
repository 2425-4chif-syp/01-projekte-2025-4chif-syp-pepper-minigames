package com.pep.mealplan.service;

import com.pep.mealplan.entity.Person;
import com.pep.mealplan.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    PersonRepository personRepo;

    // GET ALL
    public List<Person> getAll() {
        return personRepo.listAll();
    }

    // GET BY ID
    public Person getById(Long id) {
        return personRepo.findById(id);
    }

    // COUNT
    public long count() {
        return personRepo.count();
    }

    // CREATE
    @Transactional
    public Person create(Person person) {
        personRepo.persist(person);
        return person;
    }

    // UPDATE
    @Transactional
    public Person update(Long id, Person person) {
        Person existing = personRepo.findById(id);
        if (existing == null) {
            return null;
        }
        existing.firstname = person.firstname;
        existing.lastname = person.lastname;
        existing.dob = person.dob;
        existing.faceId = person.faceId;
        return existing;
    }

    // DELETE
    @Transactional
    public boolean delete(Long id) {
        return personRepo.deleteById(id);
    }
}
