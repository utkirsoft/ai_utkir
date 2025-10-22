package uz.department.uai.shared.exception;

// Barcha tashqi API xatoliklari uchun asosiy klass
public class IntegrationException extends RuntimeException {
    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
