package com.surfonepg.webhook;

import com.surfonepg.config.KopoKopoProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Kopo Kopo signs every webhook request body with an HMAC-SHA256 hash, keyed by
 * your client_secret, and sends it in the X-KopoKopo-Signature header (hex encoded).
 * We recompute the hash over the raw request body and compare in constant time.
 */
@Component
public class WebhookSignatureVerifier {

    private static final String HMAC_ALGO = "HmacSHA256";

    private final KopoKopoProperties props;

    public WebhookSignatureVerifier(KopoKopoProperties props) {
        this.props = props;
    }

    public boolean isValid(String rawBody, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(props.getClientSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] computed = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            String computedHex = bytesToHex(computed);
            return MessageDigest.isEqual(
                    computedHex.getBytes(StandardCharsets.UTF_8),
                    signatureHeader.trim().getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
