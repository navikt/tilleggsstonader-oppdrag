package no.nav.familie.oppdrag;

import no.nav.familie.oppdrag.config.ApplicationConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class DevLauncher {

    public static void main(String... args) {
        new SpringApplicationBuilder(ApplicationConfig.class)
                .profiles("dev")
                .run(args);
    }
}
