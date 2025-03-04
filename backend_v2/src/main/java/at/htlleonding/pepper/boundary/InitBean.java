package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.entity.GameType;
import at.htlleonding.pepper.entity.Move;
import at.htlleonding.pepper.repository.GameTypeRepository;
import at.htlleonding.pepper.repository.MoveRepository;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InitBean {
    @Inject
    GameTypeRepository gameTypeRepository;

    @Inject
    MoveRepository moveRepository;

    @Transactional
    @Startup
    void init() {
//        String[][] gameTypeArr = {
//                {"MEMORY", "Memory"},
//                {"TIC_TAC_TOE", "TicTacToe"},
//                {"TAG_ALONG_STORY", "Mitmachgeschichten"},
//                {"CATCH_THE_THIEF", "Fang den Dieb"}
//        };
//
//        String[][] moveArr = {
//                {"Hurra", "emote_hurra"},
//                {"Essen", "essen"},
//                {"Gehen", "gehen"},
//                {"Hand heben", "hand_heben"},
//                {"Highfive links", "highfive_links"},
//                {"Highfive rechts", "highfive_rechts"},
//                {"Klatschen", "klatschen"},
//                {"Strecken", "strecken"},
//                {"Umhersehen", "umher_sehen"},
//                {"Winken", "winken"}
//        };
//
//        for(String[] gameType : gameTypeArr) {
//            GameType gameTypeEntity = new GameType();
//            gameTypeEntity.setId(gameType[0]);
//            gameTypeEntity.setName(gameType[1]);
//            gameTypeRepository.persist(gameTypeEntity);
//        }
//
//        for(String[] move : moveArr) {
//            Move moveEntity = new Move();
//            moveEntity.setDescription(move[0]);
//            moveEntity.setName(move[1]);
//            moveRepository.persist(moveEntity);
//        }
    }
}
