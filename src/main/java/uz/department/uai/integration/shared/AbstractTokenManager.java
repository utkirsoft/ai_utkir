// integration/shared/AbstractTokenManager.java
package uz.department.uai.integration.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException; // Redis xatoligi uchun import
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

    /**
     * Yaroqli access token'ni qaytaradi.
     * Avval Redis'dan qidiradi. Agar topilmasa yoki Redis ishlamasa, tashqi API'dan oladi.
     */
    public final String getAccessToken() {
        String cacheKey = getCacheKey();
        String token = null;

        try {
            // 1. Redis'dan tokenni o'qishga harakat qilamiz
            token = redisTemplate.opsForValue().get(cacheKey);
            if (token != null) {
                log.debug(">>> Token found in Redis for key [{}]", cacheKey);
                return token;
            } else {
                // Redis ishlayapti, lekin token yo'q (yoki muddati o'tgan)
                log.info(">>> Token not found in Redis for key [{}]. Fetching a new one and trying to cache...", cacheKey);
                return fetchAndTryCacheNewToken(); // Yangisini olib, keshga yozishga harakat qilamiz
            }
        } catch (RedisConnectionFailureException | IllegalStateException e) { // Redis'ga ulanish xatolarini ushlaymiz
            // 2. Agar Redis ishlamasa
            log.error(">>> Failed to connect to Redis while getting token for key [{}]. Fetching directly from source API...", cacheKey, e);
            // To'g'ridan-to'g'ri API'dan olamiz (keshga yozishga harakat qilmaymiz)
            return fetchTokenDirectlyFromSource();
        } catch (Exception e) {
            // Redis bilan ishlashda boshqa kutilmagan xatolar
            log.error(">>> Unexpected error accessing Redis for key [{}]. Fetching directly from source API...", cacheKey, e);
            return fetchTokenDirectlyFromSource();
        }
    }

    /**
     * Tashqi API'dan yangi token oladi va uni Redis'ga yozishga HARAKAT qiladi.
     * Agar Redis'ga yozishda xato bo'lsa, xatoni loglaydi, lekin tokenni baribir qaytaradi.
     */
    private String fetchAndTryCacheNewToken() {
        String cacheKey = getCacheKey();
        TokenResponse response = fetchTokenFromSource(); // Bu metod API xatolarini ushlaydi
        if (response == null || response.getAccessToken() == null) {
            throw new IntegrationException(String.format("Could not retrieve access token details for %s", cacheKey), null);
        }

        String newToken = response.getAccessToken();
        long expiresIn = response.getExpiresIn();
        long ttlInSeconds = (expiresIn > 60) ? expiresIn - 60 : expiresIn; // Xavfsizlik buferi

        try {
            // Redis'ga yozishga harakat qilamiz
            redisTemplate.opsForValue().set(cacheKey, newToken, ttlInSeconds, TimeUnit.SECONDS);
            log.info(">>> Successfully fetched and cached new token in Redis for key [{}]. TTL: {}s.", cacheKey, ttlInSeconds);
        } catch (Exception e) {
            // Agar Redis'ga yozishda xato bo'lsa, xatoni loglaymiz, lekin exception tashlamaymiz
            log.error(">>> Successfully fetched token for key [{}], BUT FAILED TO CACHE in Redis. Returning token without caching.", cacheKey, e);
        }

        return newToken; // Yangi olingan tokenni baribir qaytaramiz
    }

    /**
     * Tashqi API'dan yangi token oladi (Redis'ga yozishga harakat qilmaydi).
     * Bu metod Redis ishlamay qolganda chaqiriladi.
     */
    private String fetchTokenDirectlyFromSource() {
        String cacheKey = getCacheKey();
        TokenResponse response = fetchTokenFromSource(); // Bu metod API xatolarini ushlaydi
        if (response == null || response.getAccessToken() == null) {
            throw new IntegrationException(String.format("Could not retrieve access token details for %s even when fetching directly.", cacheKey), null);
        }
        log.warn(">>> Successfully fetched token directly from source API for key [{}]. Token was NOT cached because Redis is unavailable.", cacheKey);
        return response.getAccessToken();
    }


    // --- Qolgan metodlar o'zgarishsiz qoladi ---
    protected abstract String getCacheKey();
    protected abstract HttpEntity<?> buildTokenRequest();
    protected abstract String getTokenUrl();
    protected abstract Class<? extends TokenResponse> getResponseType();

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
            log.error("Network error while fetching token for key [{}]. URL: {}", getCacheKey(), url, e);
            throw new ServiceUnavailableException(getCacheKey() + " (Token Service)", e);
        } catch (RestClientException e) {
            log.error("Client/Server error while fetching token for key [{}]. URL: {}. Error: {}", getCacheKey(), url, e.getMessage());
            throw new IntegrationException(String.format("Error fetching token for %s: %s", getCacheKey(), e.getMessage()), e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching token for key [{}]. URL: {}", getCacheKey(), url, e);
            throw new IntegrationException(String.format("Unexpected error fetching token for %s: %s", getCacheKey(), e.getMessage()), e);
        }
    }

    public interface TokenResponse {
        String getAccessToken();
        long getExpiresIn();
    }
}