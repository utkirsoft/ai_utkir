package uz.department.uai.shared.exception;

import org.springframework.http.HttpStatusCode;

// 5xx status kodli xatoliklar uchun (Internal Server Error, Bad Gateway, etc.)
public class ApiServerException extends IntegrationException {
    public ApiServerException(String serviceName, HttpStatusCode status, String responseBody, Throwable cause) {
        super(String.format("'%s' servisida ichki xatolik yuz berdi (%d): %s", serviceName, status.value(), responseBody), cause);
    }
}