package com.surfonepg.controller;

import com.surfonepg.packages.entity.DataPackage;
import com.surfonepg.packages.repository.DataPackageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
public class PackageController {

    private final DataPackageRepository packageRepository;

    public PackageController(DataPackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @GetMapping
    public List<DataPackage> list() {
        return packageRepository.findByActiveTrue();
    }
}
