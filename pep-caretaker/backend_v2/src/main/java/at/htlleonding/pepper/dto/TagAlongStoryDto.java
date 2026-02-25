package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.domain.GameType;

public record TagAlongStoryDto(
        Long id,
        String name,
        ImageDto storyIcon,
        boolean isEnabled,
        GameType gameType
) {
}
