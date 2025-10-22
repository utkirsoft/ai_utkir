package uz.department.uai.integration.notarial.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "integration.notarial")
public class NotarialApiProperties {

    private ServiceConfig ban;
    private ServiceConfig notarialActs;

    @Data
    public static class ServiceConfig {
        private String baseUrl;
        private String basicAuth; // Base64 encoded credentials
        private Paths paths;
    }

    @Data
    public static class Paths {
        // Ban Service Paths
        private String search;
        private String addBan;
        private String cancelBan;

        // Integration Service Paths
        private String searchActions;
    }
}