package com.surfonepg.transaction;

import com.surfonepg.kopokopo.KopoKopoStkService;
import com.surfonepg.kopokopo.dto.WebhookPayload;
import com.surfonepg.packages.entity.DataPackage;
import com.surfonepg.packages.repository.DataPackageRepository;
import com.surfonepg.radius.RadiusProvisioningService;
import com.surfonepg.transaction.entity.PaymentTransaction;
import com.surfonepg.transaction.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final DataPackageRepository packageRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final KopoKopoStkService stkService;
    private final RadiusProvisioningService radiusProvisioningService;

    public PaymentService(DataPackageRepository packageRepository,
                           PaymentTransactionRepository transactionRepository,
                           KopoKopoStkService stkService,
                           RadiusProvisioningService radiusProvisioningService) {
        this.packageRepository = packageRepository;
        this.transactionRepository = transactionRepository;
        this.stkService = stkService;
        this.radiusProvisioningService = radiusProvisioningService;
    }

    @Transactional
    public PaymentTransaction initiate(String phoneNumber, String packageCode) {
        DataPackage pkg = packageRepository.findByCodeAndActiveTrue(packageCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown or inactive package: " + packageCode));

        String merchantReference = "SPG-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        PaymentTransaction txn = new PaymentTransaction(merchantReference, phoneNumber, pkg, pkg.getPriceKes());
        txn = transactionRepository.save(txn);

        try {
            String locationUrl = stkService.initiateStkPush(phoneNumber, pkg.getPriceKes().toPlainString(), merchantReference);
            txn.setKopoKopoLocationUrl(locationUrl);
        } catch (Exception e) {
            log.error("STK push failed for reference {}: {}", merchantReference, e.getMessage(), e);
            txn.setStatus(PaymentTransaction.Status.FAILED);
        }

        return transactionRepository.save(txn);
    }

    /**
     * Called by the webhook controller once signature verification has passed.
     * Matches the payment back to our transaction via metadata.reference (the
     * merchant_reference we sent when initiating the STK push).
     */
    @Transactional
    public void handleWebhook(WebhookPayload payload) {
        WebhookPayload.Resource resource = payload.event() != null ? payload.event().resource() : null;
        if (resource == null) {
            log.warn("Webhook payload missing event.resource, ignoring: topic={}", payload.topic());
            return;
        }

        String merchantReference = resource.metadata() != null ? resource.metadata().get("reference") : null;
        if (merchantReference == null) {
            log.warn("Webhook resource {} has no metadata.reference, cannot match a transaction", resource.id());
            return;
        }

        PaymentTransaction txn = transactionRepository.findByMerchantReference(merchantReference).orElse(null);
        if (txn == null) {
            log.warn("No transaction found for merchant reference {}", merchantReference);
            return;
        }

        txn.setKopoKopoResourceId(resource.id());
        txn.setMpesaReceipt(resource.reference());

        boolean received = "Received".equalsIgnoreCase(resource.status()) || "Success".equalsIgnoreCase(resource.status());
        if (!received) {
            txn.setStatus(PaymentTransaction.Status.FAILED);
            transactionRepository.save(txn);
            log.info("Transaction {} did not succeed, status={}", merchantReference, resource.status());
            return;
        }

        txn.setStatus(PaymentTransaction.Status.RECEIVED);

        String pin = radiusProvisioningService.provision(txn.getPhoneNumber(), txn.getDataPackage());
        txn.setRadiusUsername(normalizeMsisdn(txn.getPhoneNumber()));
        txn.setRadiusPassword(pin);
        txn.setStatus(PaymentTransaction.Status.PROVISIONED);

        transactionRepository.save(txn);
        log.info("Provisioned RADIUS access for {} (package {}) after payment {}",
                txn.getRadiusUsername(), txn.getDataPackage().getCode(), merchantReference);

        // TODO: send the username/PIN to the customer via SMS here (e.g. Africa's Talking or Daraja
        // isn't needed for SMS -- use your existing SMS gateway). The captive portal can also poll
        // GET /api/v1/payments/{merchantReference} and display the PIN directly once status=PROVISIONED.
    }

    private String normalizeMsisdn(String phoneNumber) {
        String digits = phoneNumber.replaceAll("[^0-9]", "");
        if (digits.startsWith("0")) {
            digits = "254" + digits.substring(1);
        }
        return digits;
    }
}
