package htl.leonding.Repository;

import htl.leonding.Model.Step;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StepRepository implements PanacheRepository<Step> {
}
