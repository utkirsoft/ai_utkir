package uz.department.uai.shared.exception;

import org.springframework.http.HttpStatusCode;

// 4xx status kodli xatoliklar uchun (Bad Request, Unauthorized, etc.)
public class ApiClientErrorException extends IntegrationException {
    public ApiClientErrorException(String serviceName, HttpStatusCode status, String responseBody, Throwable cause) {
        super(String.format("'%s' servisiga yuborilgan so'rovda xatolik (%d): %s", serviceName, status.value(), responseBody), cause);
    }
}
