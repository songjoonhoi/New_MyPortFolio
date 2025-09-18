package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CareerCreateRequest {
    
    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 100, message = "회사명은 100자를 초과할 수 없습니다")
    private String company;
    
    @NotBlank(message = "직책은 필수입니다")
    @Size(max = 100, message = "직책은 100자를 초과할 수 없습니다")
    private String position;
    
    @NotBlank(message = "기간은 필수입니다")
    @Size(max = 50, message = "기간은 50자를 초과할 수 없습니다")
    private String period;
    
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;
}