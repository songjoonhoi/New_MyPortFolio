package com.example.demo.dto;

import com.example.demo.domain.Project;
import com.example.demo.domain.ProjectDetail;
import lombok.Getter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProjectDto {
    private final Long id;
    private final String name;
    private final String period;
    private final String description;
    private final String techStack;
    private final String projectUrl;
    private final String teamInfo;
    private final String thumbnailUrl;
    private final List<ProjectDetailDto> details;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.period = project.getPeriod();
        this.description = project.getDescription();
        this.techStack = project.getTechStack();
        this.projectUrl = project.getProjectUrl();
        this.teamInfo = project.getTeamInfo();
        this.thumbnailUrl = project.getThumbnailUrl();
        // 상세 정보 Set을 DTO List로 변환 (ID 순으로 정렬)
        this.details = project.getDetails().stream()
                .map(ProjectDetailDto::new)
                .sorted(Comparator.comparing(ProjectDetailDto::getId))
                .collect(Collectors.toList());
    }
}