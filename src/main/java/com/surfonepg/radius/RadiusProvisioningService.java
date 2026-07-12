package com.surfonepg.radius;

import com.surfonepg.packages.entity.DataPackage;
import com.surfonepg.radius.entity.RadCheck;
import com.surfonepg.radius.entity.RadReply;
import com.surfonepg.radius.repository.RadCheckRepository;
import com.surfonepg.radius.repository.RadReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * Provisions a RADIUS user for hotspot access. Mikrotik's hotspot server is configured
 * to use FreeRADIUS for both authentication and accounting, so once these rows exist,
 * the customer can log in on the captive portal with username = phone number and the
 * generated PIN, and Mikrotik enforces Session-Timeout / Mikrotik-Rate-Limit automatically.
 *
 * Username convention: the customer's phone number (msisdn, e.g. 254712345678).
 * Password: a short numeric PIN, generated per purchase and (in a full build) sent back
 * to the customer via SMS or shown on the payment-success page.
 */
@Service
public class RadiusProvisioningService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RadCheckRepository radCheckRepository;
    private final RadReplyRepository radReplyRepository;

    public RadiusProvisioningService(RadCheckRepository radCheckRepository, RadReplyRepository radReplyRepository) {
        this.radCheckRepository = radCheckRepository;
        this.radReplyRepository = radReplyRepository;
    }

    @Transactional
    public String provision(String phoneNumber, DataPackage pkg) {
        String username = normalizeMsisdn(phoneNumber);
        String pin = generatePin();

        // Wipe any prior attributes for this username so repeat purchases don't stack up
        // stale Session-Timeout/rate-limit rows from an earlier package.
        radCheckRepository.deleteByUsername(username);
        radReplyRepository.deleteByUsername(username);

        radCheckRepository.save(new RadCheck(username, "Cleartext-Password", ":=", pin));

        int sessionSeconds = pkg.getDurationMinutes() * 60;
        radReplyRepository.save(new RadReply(username, "Session-Timeout", "=", String.valueOf(sessionSeconds)));

        String rateLimit = pkg.getDownloadSpeedKbps() + "k/" + pkg.getUploadSpeedKbps() + "k";
        radReplyRepository.save(new RadReply(username, "Mikrotik-Rate-Limit", "=", rateLimit));

        if (pkg.getDataCapMb() != null) {
            long totalBytes = pkg.getDataCapMb() * 1024L * 1024L;
            radReplyRepository.save(new RadReply(username, "Mikrotik-Total-Limit", "=", String.valueOf(totalBytes)));
        }

        return pin;
    }

    private String normalizeMsisdn(String phoneNumber) {
        String digits = phoneNumber.replaceAll("[^0-9]", "");
        if (digits.startsWith("0")) {
            digits = "254" + digits.substring(1);
        }
        return digits;
    }

    private String generatePin() {
        int pin = 1000 + RANDOM.nextInt(9000);
        return String.valueOf(pin);
    }
}
