package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.entity.GameType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameTypeRepository implements PanacheRepository<GameType> {
}
