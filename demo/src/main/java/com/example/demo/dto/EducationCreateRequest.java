package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class EducationCreateRequest {
    
    @NotBlank(message = "기관명은 필수입니다")
    @Size(max = 100, message = "기관명은 100자를 초과할 수 없습니다")
    private String institution;
    
    @NotBlank(message = "과정/전공은 필수입니다")
    @Size(max = 100, message = "과정/전공은 100자를 초과할 수 없습니다")
    private String course;
    
    @NotBlank(message = "기간은 필수입니다")
    @Size(max = 50, message = "기간은 50자를 초과할 수 없습니다")
    private String period;
    
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    private String description;
}