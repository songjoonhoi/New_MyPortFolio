package com.example.demo.dto;

import com.example.demo.domain.Certification;
import lombok.Getter;

@Getter
public class CertificationDto {
    private final Long id;
    private final String name;
    private final String issuer;
    private final String acquisitionDate;

    public CertificationDto(Certification certification) {
        this.id = certification.getId();
        this.name = certification.getName();
        this.issuer = certification.getIssuer();
        this.acquisitionDate = certification.getAcquisitionDate();
    }
}