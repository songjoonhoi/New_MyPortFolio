package com.example.demo.dto;

import com.example.demo.domain.*;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PortfolioResponse {
    
    // --- 기본 정보 DTO ---
    private final String name;
    private final String jobTitle;
    private final String philosophy;
    private final String birth;
    private final String address;
    private final String email;
    private final String phone;
    private final String githubUrl;

    // --- 연관 데이터 DTO 리스트 ---
    private final List<SkillDto> skills;
    private final List<ProjectDto> projects;
    private final List<EducationDto> educations;
    private final List<CareerDto> careers;
    private final List<CertificationDto> certifications;

    /**
     * Entity를 DTO로 변환하는 생성자입니다.
     * PortfolioData Entity를 받아서 화면에 필요한 데이터만 추출하여 초기화
     * @param entity 데이터베이스에서 조회한 PortfolioData Entity
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

        // Entity 리스트를 각각의 DTO 리스트로 변환
        this.skills = entity.getSkills().stream().map(SkillDto::new).collect(Collectors.toList());
        this.projects = entity.getProjects().stream().map(ProjectDto::new).collect(Collectors.toList());
        this.educations = entity.getEducations().stream().map(EducationDto::new).collect(Collectors.toList());
        this.careers = entity.getCareers().stream().map(CareerDto::new).collect(Collectors.toList());
        this.certifications = entity.getCertifications().stream().map(CertificationDto::new).collect(Collectors.toList());
    }

    // --- 내부 DTO 클래스들 ---
    // 각 섹션별 데이터를 담기 위한 내부 클래스

    @Getter
    private static class SkillDto {
        private final String category;
        private final String name;

        public SkillDto(Skill skill) {
            this.category = skill.getCategory();
            this.name = skill.getName();
        }
    }

    @Getter
    private static class ProjectDto {
        private final String name;
        private final String period;
        private final String description;
        private final String problem;
        private final String action;
        private final String result;
        private final String techStack;
        private final String projectUrl;
        private final String teamInfo;

        public ProjectDto(Project project) {
            this.name = project.getName();
            this.period = project.getPeriod();
            this.description = project.getDescription();
            this.problem = project.getProblem();
            this.action = project.getAction();
            this.result = project.getResult();
            this.techStack = project.getTechStack();
            this.projectUrl = project.getProjectUrl();
            this.teamInfo = project.getTeamInfo();
        }
    }

    @Getter
    private static class EducationDto {
        private final String institution;
        private final String course;
        private final String period;
        private final String description;

        public EducationDto(Education education) {
            this.institution = education.getInstitution();
            this.course = education.getCourse();
            this.period = education.getPeriod();
            this.description = education.getDescription();
        }
    }

    @Getter
    private static class CareerDto {
        private final String company;
        private final String position;
        private final String period;
        private final String description;

        public CareerDto(Career career) {
            this.company = career.getCompany();
            this.position = career.getPosition();
            this.period = career.getPeriod();
            this.description = career.getDescription();
        }
    }

    @Getter
    private static class CertificationDto {
        private final String name;
        private final String issuer;
        private final String acquisitionDate;

        public CertificationDto(Certification certification) {
            this.name = certification.getName();
            this.issuer = certification.getIssuer();
            this.acquisitionDate = certification.getAcquisitionDate();
        }
    }

}
