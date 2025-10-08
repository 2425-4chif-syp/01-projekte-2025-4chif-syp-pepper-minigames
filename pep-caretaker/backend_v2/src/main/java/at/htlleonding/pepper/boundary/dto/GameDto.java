package at.htlleonding.pepper.boundary.dto;

import java.util.List;

public record GameDto(
        Long id,
        String name,
        String storyIcon,
        List<StepDto> steps,
        boolean isEnabled
) { }
