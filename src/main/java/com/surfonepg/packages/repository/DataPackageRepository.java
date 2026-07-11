package com.surfonepg.packages.repository;

import com.surfonepg.packages.entity.DataPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DataPackageRepository extends JpaRepository<DataPackage, Long> {
    List<DataPackage> findByActiveTrue();
    Optional<DataPackage> findByCodeAndActiveTrue(String code);
}
