package uz.department.uai.shared.exception;

// Tarmoqqa ulanish, timeout kabi muammolar uchun
public class ServiceUnavailableException extends IntegrationException {
    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super(String.format("'%s' servisi bilan aloqa yo'q yoki u javob bermayapti.", serviceName), cause);
    }
}
