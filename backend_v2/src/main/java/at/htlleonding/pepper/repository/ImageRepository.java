package at.htlleonding.pepper.repository;

import at.htlleonding.pepper.entity.Image;
import at.htlleonding.pepper.entity.Person;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.PathParam;

import java.util.List;

@ApplicationScoped
public class ImageRepository implements PanacheRepository<Image> {
    @Inject
    PersonRepository personRepository;

    public Image findImageByPersonId(Long id){
        Person person = personRepository.findById(id);
        if (person == null) {
            return null;
        }
        return find("person", person).firstResult();
    }
}
