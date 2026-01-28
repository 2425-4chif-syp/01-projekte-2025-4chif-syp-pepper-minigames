package com.pep.mealplan.service;

import com.pep.mealplan.entity.Person;
import com.pep.mealplan.repository.OrderRepository;
import com.pep.mealplan.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    PersonRepository personRepo;

    @Inject
    OrderRepository orderRepo;

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
        person.id = null; // Ensure ID is null so PostgreSQL generates it
        personRepo.persist(person);
        return person;
    }

    // DELETE
    @Transactional
    public boolean delete(Long id) {
        orderRepo.deleteByPersonId(id);
        return personRepo.deleteById(id);
    }
}
