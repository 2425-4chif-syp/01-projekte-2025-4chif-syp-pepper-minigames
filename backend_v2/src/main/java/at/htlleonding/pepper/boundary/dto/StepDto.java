package at.htlleonding.pepper.boundary.dto;

public record StepDto(
    Long id,
    String text,
    String image,
    int duration,
    String moveNameAndDuration
) { }
