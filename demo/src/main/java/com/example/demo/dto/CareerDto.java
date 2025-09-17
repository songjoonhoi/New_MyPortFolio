package com.example.demo.dto;

import com.example.demo.domain.Career;
import lombok.Getter;

@Getter
public class CareerDto {
    private final Long id;
    private final String company;
    private final String position;
    private final String period;
    private final String description;

    public CareerDto(Career career) {
        this.id = career.getId();
        this.company = career.getCompany();
        this.position = career.getPosition();
        this.period = career.getPeriod();
        this.description = career.getDescription();
    }
}
