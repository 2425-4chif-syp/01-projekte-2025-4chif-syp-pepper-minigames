package htl.leonding.Repository;

import htl.leonding.Model.TagAlongStory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TagAlongStoryRepository implements PanacheRepository<TagAlongStory> {
}
