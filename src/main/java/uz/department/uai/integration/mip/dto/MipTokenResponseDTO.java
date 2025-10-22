package uz.department.uai.integration.mip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uz.department.uai.integration.shared.AbstractTokenManager.TokenResponse;

@Data
public class MipTokenResponseDTO implements TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("token_type")
    private String tokenType;
}