// shared/http/HttpLoggingInterceptor.java
package uz.department.uai.shared.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // SLF4J loggerini qo'shamiz
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.department.uai.shared.logging.http.domain.HttpLog;
import uz.department.uai.shared.logging.http.repository.HttpLogRepository;
import uz.department.uai.user.domain.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j // Logger uchun annotatsiya
@Component
@RequiredArgsConstructor
public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final HttpLogRepository httpLogRepository;

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {

        HttpLog logEntry = new HttpLog(); // O'zgaruvchi nomini o'zgartirdik (log bilan chalkashmasligi uchun)
        logEntry.setTimestamp(Instant.now());
        logEntry.setRequestUrl(request.getURI().toString());
        logEntry.setRequestMethod(request.getMethod().name());
        logEntry.setRequestHeaders(request.getHeaders().toString());
        logEntry.setRequestBody(new String(body, StandardCharsets.UTF_8));

        // Joriy foydalanuvchi ID'sini olish
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                logEntry.setUserId(((User) principal).getId());
            }
        }

        ClientHttpResponse response = null;
        try {
            // So'rovni bajarishga harakat qilamiz
            response = execution.execute(request, body);
            // Muvaffaqiyatli bo'lsa, status kodni loglaymiz
            logEntry.setResponseStatus(response.getStatusCode().value());
            // Javob tanasini loglash kerak bo'lsa, ehtiyotkorlik bilan shu yerda qilinadi

        } catch (IOException ex) { // Faqat IOException'ni ushlaymiz (ResourceAccessException ham shu yerga tushadi)
            log.warn("Interceptor tarmoq xatoligini ushladi (IOException): {}", ex.getMessage()); // Ogohlantirish sifatida loglaymiz
            logEntry.setResponseStatus(503); // Yoki boshqa mos kod
            logEntry.setResponseBody("Network Error: " + ex.getMessage());
            httpLogRepository.save(logEntry); // Xatolik bo'lsa darhol saqlaymiz
            throw ex; // ASL XATOLIKNI QAYTA TASHLAYMIZ
        } catch (Exception ex) { // Boshqa kutilmagan xatoliklar
            log.error("Interceptor kutilmagan xatolikni ushladi: {}", ex.getMessage(), ex); // Xatolik sifatida loglaymiz
            logEntry.setResponseStatus(500);
            logEntry.setResponseBody("Unexpected Error: " + ex.getMessage());
            httpLogRepository.save(logEntry); // Xatolik bo'lsa darhol saqlaymiz
            // Bu yerda IOException'ga o'rash shart emas, asl xatolikni tashlagan ma'qul,
            // lekin agar yuqori qatlam IOException kutsa, o'rash mumkin.
            // Hozircha asl xatolikni tashlaymiz:
            throw ex; // ASL XATOLIKNI QAYTA TASHLAYMIZ
        } finally {
            // Muvaffaqiyatli javob bo'lsa (catch'ga tushmagan bo'lsa), log'ni saqlaymiz
            if (logEntry.getId() == null && response != null) {
                httpLogRepository.save(logEntry);
            }
        }

        return response; // Muvaffaqiyatli javobni qaytaramiz
    }
}