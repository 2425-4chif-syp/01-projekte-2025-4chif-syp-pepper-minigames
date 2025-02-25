package at.htlleonding.pepper.service;

import at.htlleonding.pepper.entity.dto.GameDto;
import at.htlleonding.pepper.entity.dto.StepDto;
import at.htlleonding.pepper.entity.Game;
import at.htlleonding.pepper.entity.GameType;
import at.htlleonding.pepper.entity.Step;

import java.util.Base64;

public class Converter {
    public static Game convertToTagAlongStory(GameDto gameDTO)
    {
        Game game = new Game();
        game.setName(gameDTO.name());
        game.setEnabled(gameDTO.isEnabled());
        game.setGameType(gameDTO.gameType());
        game.setIcon(Base64.getDecoder().decode(gameDTO.icon()));
        return game;
    }

    public static Step convertToStep(StepDto stepDTO){
        Step step = new Step();
        step.setGame(stepDTO.game());
        step.setImage(stepDTO.image());
        step.setMove(stepDTO.move());
        step.setIndex(stepDTO.index());
        step.setText(stepDTO.text());
        return step;
    }
}
