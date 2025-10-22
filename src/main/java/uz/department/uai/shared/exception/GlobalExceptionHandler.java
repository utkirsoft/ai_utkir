package uz.department.uai.shared.exception; // Yoki sizning exception'lar uchun paketingiz

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException; // Import qiling

import java.io.IOException; // Import qiling
import java.nio.channels.UnresolvedAddressException; // Import qiling

@Slf4j
@ControllerAdvice // Bu klass global exception handler ekanligini bildiradi
public class GlobalExceptionHandler {

    /**
     * Tashqi API'larga ulanishda tarmoq xatoliklarini (masalan, DNS, Timeout, Connection Refused) ushlaydi.
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException ex) {
        log.error("Tashqi servis bilan aloqa yo'q: {}", ex.getMessage(), ex); // To'liq stack trace'ni loglaymiz

        // Xatolikning asl sababini topishga harakat qilamiz
        Throwable rootCause = findRootCause(ex);
        String detailedMessage;

        if (rootCause instanceof UnresolvedAddressException) {
            detailedMessage = String.format(
                    "Tashqi servis manzilini aniqlab bo'lmadi (DNS muammosi). Iltimos, tarmoq sozlamalarini tekshiring yoki administrator bilan bog'laning. Asl xabar: %s",
                    ex.getMessage()
            );
        } else if (rootCause instanceof java.net.ConnectException) {
            detailedMessage = String.format(
                    "Tashqi servisga ulanib bo'lmadi (Connection Refused). Servis ishlayotganiga ishonch hosil qiling. Asl xabar: %s",
                    ex.getMessage()
            );
        } else if (rootCause instanceof java.net.SocketTimeoutException) {
            detailedMessage = String.format(
                    "Tashqi servisdan javob kutish vaqti tugadi (Timeout). Tarmoq ulanishini yoki servis yuklamasini tekshiring. Asl xabar: %s",
                    ex.getMessage()
            );
        }
        else {
            // Boshqa barcha tarmoq xatolari uchun umumiy xabar
            detailedMessage = String.format(
                    "Tashqi servis bilan aloqa o'rnatishda xatolik. Iltimos keyinroq urinib ko'ring. Asl xabar: %s",
                    ex.getMessage()
            );
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), detailedMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 4xx status kodli xatoliklarni ushlaydi (masalan, noto'g'ri so'rov, avtorizatsiya xatosi).
     */
    @ExceptionHandler(ApiClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleApiClientError(ApiClientErrorException ex) {
        log.warn("Tashqi API'ga noto'g'ri so'rov yuborildi: {}", ex.getMessage()); // Ogohlantirish sifatida loglaymiz
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        // Haqiqiy status kodni olish mumkin (agar kerak bo'lsa)
        // HttpStatus status = ((HttpClientErrorException) ex.getCause()).getStatusCode();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Yoki 4xx status kodlardan biri
    }

    /**
     * 5xx status kodli xatoliklarni ushlaydi (tashqi serverdagi xatoliklar).
     */
    @ExceptionHandler(ApiServerException.class)
    public ResponseEntity<ErrorResponse> handleApiServerError(ApiServerException ex) {
        log.error("Tashqi API serverida xatolik: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_GATEWAY.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY); // Yoki 5xx status kodlardan biri
    }

    /**
     * Boshqa barcha kutilmagan xatoliklarni ushlaydi.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Kutilmagan xatolik yuz berdi: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Tizimda kutilmagan xatolik yuz berdi.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    // --- Yordamchi metodlar ---

    /**
     * Frontend uchun standart xatolik javobi formati.
     */
    private record ErrorResponse(int status, String message) {}

    /**
     * Exception'ning eng tub sababini topadi.
     */
    private Throwable findRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}