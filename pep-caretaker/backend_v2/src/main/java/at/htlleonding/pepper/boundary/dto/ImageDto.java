package at.htlleonding.pepper.boundary.dto;

import at.htlleonding.pepper.domain.Person;

public record ImageDto(Long id, Long personId, String base64Image, String imageUrl, String description) {
}
