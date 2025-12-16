package com.pep.mealplan.repository;

import com.pep.mealplan.entity.Picture;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PictureRepository implements PanacheRepository<Picture> {
}
