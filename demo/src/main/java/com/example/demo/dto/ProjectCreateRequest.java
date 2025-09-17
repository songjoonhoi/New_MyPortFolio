package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateRequest {
    // 썸네일과 상세 정보는 별도로 처리하므로, 프로젝트의 기본 정보만 받습니다.
    private String name;
    private String period;
    private String description;
    private String techStack;
    private String projectUrl;
    private String teamInfo;
}