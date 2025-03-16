package at.htlleonding.pepper.util;

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
        // TODO: Bin mir nicht sicher, ob das funktioniert, da der data:<imagetype>-Prefix nicht berücksichtigt wird.
        game.setStoryIconBinary(Base64.getDecoder().decode(gameDTO.icon()));
        return game;
    }

    public static Step convertToStep(StepDto stepDTO){
        Step step = new Step();
        step.setGame(stepDTO.game());
        step.setImage(new Image(null, Base64.getDecoder().decode((stepDTO.image())), null, null));
        step.setMove(stepDTO.move());
        step.setIndex(stepDTO.index());
        step.setText(stepDTO.text());
        return step;
    }
}
