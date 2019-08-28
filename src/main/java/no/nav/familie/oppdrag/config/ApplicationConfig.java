package no.nav.familie.oppdrag.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan({"no.nav.familie.oppdrag"})
public class ApplicationConfig {

    @Bean
    ServletWebServerFactory servletWebServerFactory() {

        JettyServletWebServerFactory serverFactory = new JettyServletWebServerFactory();

        serverFactory.setPort(8087);

        return serverFactory;
    }
}
