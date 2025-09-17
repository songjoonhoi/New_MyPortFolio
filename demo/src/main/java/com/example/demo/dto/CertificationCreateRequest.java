package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificationCreateRequest {
    private String name;
    private String issuer;
    private String acquisitionDate;
}
