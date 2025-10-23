// integration/shared/AbstractTokenManager.java
package uz.department.uai.integration.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uz.department.uai.shared.exception.IntegrationException;
import uz.department.uai.shared.exception.ServiceUnavailableException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean; // Muvaffaqiyatli ulanishni bir marta loglash uchun

@Slf4j
public abstract class AbstractTokenManager {

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    // Redis bilan ilk muvaffaqiyatli ulanishni loglash uchun flag
    private final AtomicBoolean redisConnectedLogged = new AtomicBoolean(false);

    protected AbstractTokenManager(StringRedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    public final String getAccessToken() {
        String cacheKey = getCacheKey();
        String token = null;

        try {
            // 1. Redis'dan tokenni o'qishga harakat qilamiz
            token = redisTemplate.opsForValue().get(cacheKey);

            // Agar Redis bilan ulanish muvaffaqiyatli bo'lsa (xatolik bo'lmadi)
            // va bu haqida hali log yozilmagan bo'lsa:
            if (redisConnectedLogged.compareAndSet(false, true)) {
                log.info("✅ Successfully connected to Redis."); // Muvaffaqiyatli ulanish logi
            }

            if (token != null) {
                log.debug(">>> Token found in Redis for key [{}]", cacheKey);
                return token;
            } else {
                log.info(">>> Token not found in Redis for key [{}]. Fetching a new one and trying to cache...", cacheKey);
                return fetchAndTryCacheNewToken();
            }
        } catch (RedisConnectionFailureException | IllegalStateException e) {
            log.error(">>> Failed to connect to Redis while getting token for key [{}]. Fetching directly from source API...", cacheKey, e);
            redisConnectedLogged.set(false); // Agar ulanish uzilsa, keyingi urinishda yana log yozishga ruxsat beramiz
            return fetchTokenDirectlyFromSource();
        } catch (Exception e) {
            log.error(">>> Unexpected error accessing Redis for key [{}]. Fetching directly from source API...", cacheKey, e);
            redisConnectedLogged.set(false); // Xatoda ham flagni reset qilamiz
            return fetchTokenDirectlyFromSource();
        }
    }

    private String fetchAndTryCacheNewToken() {
        String cacheKey = getCacheKey();
        TokenResponse response = fetchTokenFromSource();
        if (response == null || response.getAccessToken() == null) {
            throw new IntegrationException(String.format("Could not retrieve access token details for %s", cacheKey), null);
        }

        String newToken = response.getAccessToken();
        long expiresIn = response.getExpiresIn();
        long ttlInSeconds = (expiresIn > 60) ? expiresIn - 60 : expiresIn;

        try {
            redisTemplate.opsForValue().set(cacheKey, newToken, ttlInSeconds, TimeUnit.SECONDS);
            log.info(">>> Successfully fetched and cached new token in Redis for key [{}]. TTL: {}s.", cacheKey, ttlInSeconds);
            // Redis'ga yozish muvaffaqiyatli bo'lsa ham, ulanish haqida log yozamiz (agar hali yozilmagan bo'lsa)
            if (redisConnectedLogged.compareAndSet(false, true)) {
                log.info("✅ Successfully connected to Redis (confirmed during cache write).");
            }
        } catch ( Exception e) {
            log.error(">>> Successfully fetched token for key [{}], BUT FAILED TO CACHE in Redis. Returning token without caching.", cacheKey, e);
            redisConnectedLogged.set(false); // Xatoda flagni reset qilamiz
        }

        return newToken;
    }

    // --- Qolgan metodlar o'zgarishsiz qoladi ---
    // fetchTokenDirectlyFromSource()
    // getCacheKey()
    // buildTokenRequest()
    // getTokenUrl()
    // getResponseType()
    // fetchTokenFromSource()
    // interface TokenResponse
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