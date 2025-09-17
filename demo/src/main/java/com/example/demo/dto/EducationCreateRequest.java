package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationCreateRequest {
    private String institution;
    private String course;
    private String period;
    private String description;
}
