package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.entity.Game;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameRepository implements PanacheRepository<Game> {
}
