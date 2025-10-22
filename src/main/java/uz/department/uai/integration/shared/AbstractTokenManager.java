package uz.department.uai.integration.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uz.department.uai.shared.exception.IntegrationException;
import uz.department.uai.shared.exception.ServiceUnavailableException;

import java.util.concurrent.TimeUnit;
@Slf4j
public abstract class AbstractTokenManager {

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    protected AbstractTokenManager(StringRedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    // Bu metod o'zgarmaydi, shuning uchun "final"
    public final String getAccessToken() {
        String token = redisTemplate.opsForValue().get(getCacheKey());
        if (token == null) {
            System.out.println(">>> Token not found in Redis for key [" + getCacheKey() + "]. Fetching a new one...");
            return fetchAndCacheNewToken();
        }
        return token;
    }

    private String fetchAndCacheNewToken() {
        TokenResponse response = fetchTokenFromSource();
        if (response == null || response.getAccessToken() == null) {
            throw new RuntimeException("Could not fetch access token for " + getCacheKey());
        }

        String newToken = response.getAccessToken();
        long expiresIn = response.getExpiresIn();
        long ttlInSeconds = (expiresIn > 60) ? expiresIn - 60 : expiresIn; // Xavfsizlik buferi

        redisTemplate.opsForValue().set(getCacheKey(), newToken, ttlInSeconds, TimeUnit.SECONDS);
        System.out.println(">>> Successfully cached new token in Redis for key [" + getCacheKey() + "]. TTL: " + ttlInSeconds + "s.");

        return newToken;
    }

    // Har bir merosxo'r o'zi uchun implement qilishi SHART bo'lgan metodlar

    /**
     * @return Redis'da token saqlanadigan unikal kalit (masalan, "api:mip:token")
     */
    protected abstract String getCacheKey();

    /**
     * @return Token olish uchun yuboriladigan so'rov (URL, Headers, Body)
     */
    protected abstract HttpEntity<?> buildTokenRequest();

    /**
     * @return Token olish uchun API manzili
     */
    protected abstract String getTokenUrl();

    /**
     * @return Javobni qabul qilib oladigan DTO klassi
     */
    protected abstract Class<? extends TokenResponse> getResponseType();

    // Bu metodni chaqirib, tokenni olamiz
    private TokenResponse fetchTokenFromSource() {
        String url = getTokenUrl();
        HttpEntity<?> requestEntity = buildTokenRequest();
        Class<? extends TokenResponse> responseType = getResponseType();

        try {
            log.debug("Fetching token from URL: {}", url);
            TokenResponse response = restTemplate.postForObject(url, requestEntity, responseType);
            log.debug("Successfully fetched token for key [{}]", getCacheKey());
            return response;
        } catch (ResourceAccessException e) {
            // Tarmoq xatoliklarini ushlaymiz (DNS, Timeout, Connection Refused)
            log.error("Network error while fetching token for key [{}]. URL: {}", getCacheKey(), url, e);
            throw new ServiceUnavailableException(getCacheKey() + " (Token Service)", e); // <-- MUHIM O'ZGARISH
        } catch (RestClientException e) {
            // Boshqa RestTemplate xatoliklari (masalan, 4xx, 5xx javoblar token olishda)
            log.error("Client/Server error while fetching token for key [{}]. URL: {}. Error: {}", getCacheKey(), url, e.getMessage());
            // Bu yerda ham aniqroq exception tashlash mumkin, masalan 4xx uchun alohida
            throw new IntegrationException(String.format("Error fetching token for %s: %s", getCacheKey(), e.getMessage()), e);
        } catch (Exception e) {
            // Boshqa kutilmagan xatoliklar
            log.error("Unexpected error while fetching token for key [{}]. URL: {}", getCacheKey(), url, e);
            throw new IntegrationException(String.format("Unexpected error fetching token for %s: %s", getCacheKey(), e.getMessage()), e);
        }
    }

    // Barcha javob DTO'lari implement qilishi kerak bo'lgan umumiy interfeys
    public interface TokenResponse {
        String getAccessToken();
        long getExpiresIn();
    }
}