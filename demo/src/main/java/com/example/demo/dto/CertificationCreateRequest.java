package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CertificationCreateRequest {
    
    @NotBlank(message = "자격증명은 필수입니다")
    @Size(max = 100, message = "자격증명은 100자를 초과할 수 없습니다")
    private String name;
    
    @NotBlank(message = "발급기관은 필수입니다")
    @Size(max = 100, message = "발급기관은 100자를 초과할 수 없습니다")
    private String issuer;
    
    @NotBlank(message = "취득일은 필수입니다")
    @Size(max = 20, message = "취득일은 20자를 초과할 수 없습니다")
    private String acquisitionDate;
}