package com.surfonepg.packages.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "data_packages")
public class DataPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_kes", nullable = false)
    private BigDecimal priceKes;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "download_speed_kbps", nullable = false)
    private Integer downloadSpeedKbps;

    @Column(name = "upload_speed_kbps", nullable = false)
    private Integer uploadSpeedKbps;

    @Column(name = "data_cap_mb")
    private Integer dataCapMb;

    @Column(nullable = false)
    private boolean active = true;

    protected DataPackage() {}

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getPriceKes() { return priceKes; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Integer getDownloadSpeedKbps() { return downloadSpeedKbps; }
    public Integer getUploadSpeedKbps() { return uploadSpeedKbps; }
    public Integer getDataCapMb() { return dataCapMb; }
    public boolean isActive() { return active; }
}
