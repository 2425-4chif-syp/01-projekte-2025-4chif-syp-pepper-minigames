package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.domain.Game;
import at.htlleonding.pepper.domain.Move;

public record StepDto(Game game, int index, String image, String image_desc, Move move, String text, int durationInSeconds) {
}