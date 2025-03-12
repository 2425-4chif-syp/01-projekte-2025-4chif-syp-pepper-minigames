package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.domain.Game;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GameRepository implements PanacheRepository<Game> {
    @Inject
    StepRepository stepRepository;

    @Transactional
    public boolean deleteGameAndSteps(Long gameId) {
        Game game = findById(gameId);
        if (game == null) {
            return false;
        }

        stepRepository.deleteByGameId(gameId);
        delete(game);

        return true;
    }
}
