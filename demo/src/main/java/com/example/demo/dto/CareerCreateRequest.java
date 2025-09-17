package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CareerCreateRequest {
    
    private String company;
    private String position;
    private String period;
    private String description;
}
