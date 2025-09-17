// src/main/java/com/example/demo/dto/PortfolioResponse.java
package com.example.demo.dto;

import com.example.demo.domain.*;
import lombok.Getter;

import java.util.LinkedHashMap; // LinkedHashMap import
import java.util.List;          // List import
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class PortfolioResponse {

    // --- 기본 정보 ---
    private final String name;
    private final String jobTitle;
    private final String philosophy;
    private final String birth;
    private final String address;
    private final String email;
    private final String phone;
    private final String githubUrl;
    private final String profileImageUrl;

    // --- 연관 데이터 (순서를 보장하기 위해 LinkedHashMap 사용) ---
    private final Map<String, Set<SkillDto>> skillsByCategory;
    private final Set<ProjectDto> projects;
    private final Set<EducationDto> educations;
    private final Set<CareerDto> careers;
    private final Set<CertificationDto> certifications;

    /**
     * Entity를 DTO로 변환하는 생성자입니다.
     */
    public PortfolioResponse(PortfolioData entity) {
        this.name = entity.getName();
        this.jobTitle = entity.getJobTitle();
        this.philosophy = entity.getPhilosophy();
        this.birth = entity.getBirth();
        this.address = entity.getAddress();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.githubUrl = entity.getGithubUrl();
        this.profileImageUrl = entity.getProfileImageUrl();
        

        // --- 스킬 카테고리 순서 지정 및 정렬 로직 ---

        // 1. 원하는 카테고리 순서를 정의합니다.
        List<String> categoryOrder = List.of("Languages", "Frameworks", "Database", "Cloud", "Tools");

        // 2. 기존 방식대로 카테고리별로 그룹화합니다.
        Map<String, Set<SkillDto>> tempSkillsMap = entity.getSkills().stream()
                .map(SkillDto::new)
                .collect(Collectors.groupingBy(SkillDto::getCategory, Collectors.toSet()));

        // 3. 순서가 보장되는 LinkedHashMap을 생성하고, 정의된 순서대로 데이터를 담습니다.
        this.skillsByCategory = new LinkedHashMap<>();
        for (String category : categoryOrder) {
            if (tempSkillsMap.containsKey(category)) {
                this.skillsByCategory.put(category, tempSkillsMap.get(category));
            }
        }

        // --- 나머지 데이터 변환 ---
        this.projects = entity.getProjects().stream().map(ProjectDto::new).collect(Collectors.toSet());
        this.educations = entity.getEducations().stream().map(EducationDto::new).collect(Collectors.toSet());
        this.careers = entity.getCareers().stream().map(CareerDto::new).collect(Collectors.toSet());
        this.certifications = entity.getCertifications().stream().map(CertificationDto::new).collect(Collectors.toSet());
    }

    // --- 내부 DTO 클래스들 (이하 동일) ---

    @Getter
    private static class SkillDto {
        private final Long id;
        private final String category;
        private final String name;
        public SkillDto(Skill skill) {
            this.id = skill.getId();
            this.category = skill.getCategory();
            this.name = skill.getName();
        }
    }

   @Getter
    private static class ProjectDto {
        private final Long id;
        private final String name;
        private final String period;
        private final String description;
        private final String techStack;
        private final String projectUrl;
        private final String teamInfo;
        private final String thumbnailUrl;
        private final Set<ProjectDetailDto> details; // 상세 정보 Set 추가

        public ProjectDto(Project project) {
            this.id = project.getId();
            this.name = project.getName();
            this.period = project.getPeriod();
            this.description = project.getDescription();
            this.techStack = project.getTechStack();
            this.projectUrl = project.getProjectUrl();
            this.teamInfo = project.getTeamInfo();
            this.thumbnailUrl = project.getThumbnailUrl();
            
            // 상세 정보 Set을 DTO Set으로 변환
            this.details = project.getDetails().stream()
                            .map(ProjectDetailDto::new)
                            .collect(Collectors.toSet());
        }
    }

    @Getter
    private static class EducationDto {
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

    @Getter
    private static class CareerDto {
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

    @Getter
    private static class CertificationDto {
        private final Long id;
        private final String name;
        private final String issuer;
        private final String acquisitionDate;
        public CertificationDto(Certification certification) {
            this.id = certification.getId();
            this.name = certification.getName();
            this.issuer = certification.getIssuer();
            this.acquisitionDate = certification.getAcquisitionDate();
        }
    }

     @Getter
    private static class ProjectDetailDto {
        private final Long id;
        private final String imageUrl;
        private final String description;

        public ProjectDetailDto(ProjectDetail detail) {
            this.id = detail.getId();
            this.imageUrl = detail.getImageUrl();
            this.description = detail.getDescription();
        }
    }
}