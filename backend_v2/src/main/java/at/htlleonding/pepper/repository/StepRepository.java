package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.domain.Step;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class StepRepository implements PanacheRepository<Step> {
    public List<Step> findByGameId(Long gameId) {
        return find("game.id", gameId).list();
    }

    @Transactional
    public void deleteByGameId(Long gameId) {
        delete("game.id", gameId);
    }

    @Transactional
    public void deleteAllStepsByGameId(Long gameId) {
        for (Step step : findByGameId(gameId)) {
            delete(step);
        }
    }
}