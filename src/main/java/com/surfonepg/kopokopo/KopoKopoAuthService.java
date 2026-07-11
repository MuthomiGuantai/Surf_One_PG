package com.surfonepg.kopokopo;

import com.surfonepg.config.KopoKopoProperties;
import com.surfonepg.kopokopo.dto.TokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

/**
 * Requests and caches a KopoKopo application access token using the OAuth2
 * client_credentials grant (POST {base-url}/oauth/token). Tokens are valid for
 * 1 hour; we refresh 60s before expiry to avoid races.
 */
@Service
public class KopoKopoAuthService {

    private final RestClient restClient;
    private final KopoKopoProperties props;

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public KopoKopoAuthService(RestClient kopoKopoRestClient, KopoKopoProperties props) {
        this.restClient = kopoKopoRestClient;
        this.props = props;
    }

    public synchronized String getAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(expiresAt.minusSeconds(60))) {
            return cachedToken;
        }
        return refreshToken();
    }

    private String refreshToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", props.getClientId());
        form.add("client_secret", props.getClientSecret());
        form.add("grant_type", "client_credentials");

        TokenResponse response = restClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (response == null || response.access_token() == null) {
            throw new IllegalStateException("KopoKopo token request returned no access_token");
        }

        this.cachedToken = response.access_token();
        long expiresIn = response.expires_in() != null ? response.expires_in() : 3600L;
        this.expiresAt = Instant.now().plusSeconds(expiresIn);
        return cachedToken;
    }
}
