package au.com.geekseat.config;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class FlywayMigration {

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String datasourceUrl;

    @ConfigProperty(name = "quarkus.datasource.username")
    String datasourceUsername;

    @ConfigProperty(name = "quarkus.datasource.password")
    String datasourcePassword;

    @ConfigProperty(name = "flyway.enable")
    boolean isEnabled;

    public void runFlywayMigration(@Observes StartupEvent event) {
        if (isEnabled) {
            Flyway flyway =
                    Flyway.configure()
                            .dataSource("jdbc:" + datasourceUrl,
                                    datasourceUsername,
                                    datasourcePassword).load();
            flyway.migrate();
        }
    }
}