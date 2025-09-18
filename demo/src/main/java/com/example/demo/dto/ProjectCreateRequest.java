package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class ProjectCreateRequest {
    
    @NotBlank(message = "프로젝트명은 필수입니다")
    @Size(max = 200, message = "프로젝트명은 200자를 초과할 수 없습니다")
    private String name;
    
    @NotBlank(message = "기간은 필수입니다")
    @Size(max = 50, message = "기간은 50자를 초과할 수 없습니다")
    private String period;
    
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;
    
    @Size(max = 300, message = "기술 스택은 300자를 초과할 수 없습니다")
    private String techStack;
    
    @Size(max = 200, message = "프로젝트 URL은 200자를 초과할 수 없습니다")
    private String projectUrl;
    
    @Size(max = 100, message = "팀 정보는 100자를 초과할 수 없습니다")
    private String teamInfo;
}