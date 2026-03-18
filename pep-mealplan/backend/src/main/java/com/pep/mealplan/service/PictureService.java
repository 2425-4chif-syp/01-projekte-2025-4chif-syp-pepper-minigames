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
    PictureRepository pictureRepo;

    public List<Picture> getAll() {
        return pictureRepo.listAll();
    }

    public Picture getById(Long id) {
        return pictureRepo.findById(id);
    }

    @Transactional
    public byte[] getImageData(Long id) {
        Picture picture = pictureRepo.findById(id);
        return picture != null ? picture.image : null;
    }

    @Transactional
    public Picture create(String description, String url, byte[] imageData) {
        Picture picture = new Picture();
        picture.description = description;
        picture.url = url;
        picture.image = imageData;
        pictureRepo.persist(picture);
        return picture;
    }

    @Transactional
    public boolean delete(Long id) {
        return pictureRepo.deleteById(id);
    }
}
