package uz.department.uai.shared.logging.http.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "http_logs")
@Data
public class HttpLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;
    private String requestUrl;
    private String requestMethod;

    @Lob // Katta hajmdagi matn uchun
    private String requestHeaders;

    @Lob
    private String requestBody;

    private Integer responseStatus;

    @Lob
    private String responseBody;

    private Instant timestamp;
}