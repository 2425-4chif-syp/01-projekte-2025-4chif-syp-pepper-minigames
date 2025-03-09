package at.htlleonding.pepper.entity.dto;

import at.htlleonding.pepper.entity.Game;
import at.htlleonding.pepper.entity.Image;
import at.htlleonding.pepper.entity.Move;
import at.htlleonding.pepper.entity.Step;

public record StepDto(Game game, int index, String image, String image_desc, Move move, String text, int durationInSeconds) {
}