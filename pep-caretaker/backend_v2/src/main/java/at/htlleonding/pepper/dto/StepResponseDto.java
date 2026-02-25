package at.htlleonding.pepper.dto;

public record StepResponseDto(
        Long id,
        int index,
        ImageDto image,
        String text,
        int durationInSeconds
) {
}
