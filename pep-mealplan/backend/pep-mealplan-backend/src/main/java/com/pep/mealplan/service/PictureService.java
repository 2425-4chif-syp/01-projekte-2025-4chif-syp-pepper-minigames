package com.pep.mealplan.service;

import com.pep.mealplan.entity.Picture;
import com.pep.mealplan.repository.PictureRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PictureService {

    @Inject
    PictureRepository repository;

    // ---------------------------------------
    // READ
    // ---------------------------------------

    public List<Picture> getAll() {
        return repository.listAll();
    }

    public Picture getById(Long id) {
        return repository.findById(id);
    }

    public List<Picture> searchByName(String name) {
        return repository.list(
                "LOWER(name) LIKE ?1",
                "%" + name.toLowerCase() + "%"
        );
    }

    // ---------------------------------------
    // WRITE
    // ---------------------------------------

    @Transactional
    public Picture create(Picture picture) {
        repository.persist(picture);
        return picture;
    }

    @Transactional
    public Picture update(Long id, Picture picture) {
        Picture existing = repository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.name = picture.name;
        existing.mediaType = picture.mediaType;
        existing.base64 = picture.base64;
        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}
