package com.example.demo.dto;

import com.example.demo.domain.ProjectDetail;
import lombok.Getter;

@Getter
public class ProjectDetailDto {
    private final Long id;
    private final String imageUrl;
    private final String description;

    public ProjectDetailDto(ProjectDetail detail) {
        this.id = detail.getId();
        this.imageUrl = detail.getImageUrl();
        this.description = detail.getDescription();
    }
}