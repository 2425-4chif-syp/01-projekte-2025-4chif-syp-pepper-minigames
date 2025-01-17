package at.htlleonding.pepper.controller;

import at.htlleonding.pepper.entity.GameType;
import at.htlleonding.pepper.entity.Person;
import at.htlleonding.pepper.repository.GameTypeRepository;
import at.htlleonding.pepper.repository.PersonRepository;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

@ApplicationScoped
public class InitBean {

    @Inject
    GameTypeRepository gameTypeRepository;


    @Transactional
    @Startup
    void init() {
    }
}
