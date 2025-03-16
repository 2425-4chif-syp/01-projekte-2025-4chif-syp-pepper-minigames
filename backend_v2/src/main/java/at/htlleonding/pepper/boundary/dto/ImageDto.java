package at.htlleonding.pepper.boundary.dto;

import at.htlleonding.pepper.domain.Person;

public record ImageDto(Long id, Person person, String base64Image, String imageUrl, String description) {
}
