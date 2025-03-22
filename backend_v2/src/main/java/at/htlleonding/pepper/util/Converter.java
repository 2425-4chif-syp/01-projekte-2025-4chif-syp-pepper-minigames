package at.htlleonding.pepper.util;

import at.htlleonding.pepper.domain.Game;
import at.htlleonding.pepper.domain.Image;
import at.htlleonding.pepper.domain.Step;
import at.htlleonding.pepper.dto.GameDto;
import at.htlleonding.pepper.dto.StepDto;
import io.smallrye.common.ref.StrongReference;

import java.util.Base64;

public class Converter {
    public static Game convertToTagAlongStory(GameDto gameDTO)
    {
        Game game = new Game();
        game.setName(gameDTO.name());
        game.setEnabled(gameDTO.isEnabled());
        game.setGameType(gameDTO.gameType());
        // TODO: Bin mir nicht sicher, ob das funktioniert, da der data:<imagetype>-Prefix nicht berücksichtigt wird.
        game.setStoryIconBinary(Base64.getDecoder().decode(extractBase64String(gameDTO.icon())));
        return game;
    }

    public static Step convertToStep(StepDto stepDTO){
        Step step = new Step();
        step.setGame(stepDTO.game());
        step.setImage(new Image(null, java.util.Base64.getDecoder().decode(extractBase64String(stepDTO.image())), null, null));
        step.setMove(stepDTO.move());
        step.setIndex(stepDTO.index());
        step.setText(stepDTO.text());
        return step;
    }

    public static String extractBase64String(String image){
        if (image == null || !image.contains(",")) {
            return image; // Return as is if it's null or doesn't contain a prefix
        }
        return image.substring(image.indexOf(",") + 1);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 35ca0212 (feat: Add tests for Person endpoints using AssertJ #PEP104)
