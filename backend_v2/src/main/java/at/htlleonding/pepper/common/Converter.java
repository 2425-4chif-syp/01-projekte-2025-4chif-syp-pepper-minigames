package at.htlleonding.pepper.common;

import at.htlleonding.pepper.boundary.dto.ImageDto;
import at.htlleonding.pepper.domain.Image;
import at.htlleonding.pepper.dto.GameDto;
import at.htlleonding.pepper.dto.StepDto;
import at.htlleonding.pepper.domain.Game;
import at.htlleonding.pepper.domain.Step;

import java.util.Base64;

public class Converter {


    public static Game convertToTagAlongStory(GameDto gameDTO)
    {
        Game game = new Game();
        game.setName(gameDTO.name());
        game.setEnabled(gameDTO.isEnabled());
        game.setGameType(gameDTO.gameType());
        game.setStoryIconBase64(gameDTO.icon());
        return game;
    }

    public static Step convertToStep(StepDto stepDTO){
        Step step = new Step();
        step.setGame(stepDTO.game());
        step.setMove(stepDTO.move());
        step.setIndex(stepDTO.index());
        step.setText(stepDTO.text());
        step.setDurationInSeconds(stepDTO.durationInSeconds());
        return step;
    }

    public static ImageDto convertToImageDto(Image image){
        return new ImageDto(image.getId(), image.getPerson(), Base64.getEncoder().encodeToString(image.getImage()), image.getUrl(), image.getDescription());
    }

    public static String extractBase64String(String dataUrl) {
        // Check if the input is valid
        if (dataUrl == null || !dataUrl.startsWith("data:")) {
            throw new IllegalArgumentException("Invalid data URL format");
        }

        // Find the index of the comma after the prefix
        int commaIndex = dataUrl.indexOf(',');

        // If the comma is not found, the input is invalid
        if (commaIndex == -1) {
            throw new IllegalArgumentException("Invalid data URL format: no comma found");
        }

        // Extract the Base64-encoded string after the comma
        return dataUrl.substring(commaIndex + 1);
    }

}
