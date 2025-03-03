package at.htlleonding.pepper.boundary;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InitBean {


    @Transactional
    @Startup
    void init() {
    }
}
