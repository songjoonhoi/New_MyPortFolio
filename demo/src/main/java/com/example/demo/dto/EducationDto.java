package com.example.demo.dto;

import com.example.demo.domain.Education;
import lombok.Getter;

@Getter
public class EducationDto {
    private final Long id;
    private final String institution;
    private final String course;
    private final String period;
    private final String description;

    public EducationDto(Education education) {
        this.id = education.getId();
        this.institution = education.getInstitution();
        this.course = education.getCourse();
        this.period = education.getPeriod();
        this.description = education.getDescription();
    }
}