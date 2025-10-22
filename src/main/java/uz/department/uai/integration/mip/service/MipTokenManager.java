package uz.department.uai.integration.mip.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uz.department.uai.integration.mip.config.MipApiProperties;
import uz.department.uai.integration.mip.dto.MipTokenResponseDTO;
import uz.department.uai.integration.shared.AbstractTokenManager;

@Service
public class MipTokenManager extends AbstractTokenManager {

    private final MipApiProperties mipApiProperties;

    public MipTokenManager(StringRedisTemplate redisTemplate, RestTemplate restTemplate, MipApiProperties mipApiProperties) {
        super(redisTemplate, restTemplate);
        this.mipApiProperties = mipApiProperties;
    }

    @Override
    protected String getCacheKey() {
        return "external_api:mip:access_token";
    }

    @Override
    protected HttpEntity<?> buildTokenRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + mipApiProperties.getCredentials().getBasicAuth());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", mipApiProperties.getCredentials().getGrantType());
        body.add("username", mipApiProperties.getCredentials().getUsername());
        body.add("password", mipApiProperties.getCredentials().getPassword());

        return new HttpEntity<>(body, headers);
    }

    @Override
    protected String getTokenUrl() {
        return mipApiProperties.getTokenUrl();
    }

    @Override
    protected Class<? extends TokenResponse> getResponseType() {
        return MipTokenResponseDTO.class;
    }
}
