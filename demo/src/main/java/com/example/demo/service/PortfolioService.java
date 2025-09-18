// src/main/java/com/example/demo/service/PortfolioService.java
package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.dto.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioDataRepository portfolioDataRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final CertificationRepository certificationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectDetailRepository projectDetailRepository;
    private final SkillRepository skillRepository;
    private final FileStorageService fileStorageService;

    public PortfolioResponse findPortfolio(Long id) {
        return portfolioDataRepository.findById(id)
                .map(PortfolioResponse::new)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오 정보를 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest request, String newImageUrl) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        portfolio.setName(request.getName());
        portfolio.setJobTitle(request.getJobTitle());
        portfolio.setPhilosophy(request.getPhilosophy());
        portfolio.setBirth(request.getBirth());
        portfolio.setAddress(request.getAddress());
        portfolio.setEmail(request.getEmail());
        portfolio.setPhone(request.getPhone());
        portfolio.setGithubUrl(request.getGithubUrl());
        if (newImageUrl != null) {
            portfolio.setProfileImageUrl(newImageUrl);
        }
    }

    @Transactional
    public void deleteProfileImage(Long portfolioId) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        fileStorageService.deleteFile(portfolio.getProfileImageUrl());
        portfolio.setProfileImageUrl("/images/default-profile.png");
    }

    // --- Career CRUD ---
    @Transactional
    public Career addCareer(Long portfolioId, CareerCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        Career newCareer = new Career(request.getCompany(), request.getPosition(), request.getPeriod(), request.getDescription(), portfolio);
        return careerRepository.save(newCareer);
    }

    public CareerDto findCareerById(Long careerId) {
        Career career = careerRepository.findById(careerId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 경력 없음: " + careerId));
        return new CareerDto(career);
    }

    @Transactional
    public Career updateCareer(Long careerId, CareerCreateRequest request) {
        Career career = careerRepository.findById(careerId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 경력 없음: " + careerId));
        career.setCompany(request.getCompany());
        career.setPosition(request.getPosition());
        career.setPeriod(request.getPeriod());
        career.setDescription(request.getDescription());
        return careerRepository.save(career);
    }

    @Transactional
    public void deleteCareer(Long portfolioId, Long careerId) {
        careerRepository.deleteById(careerId);
    }

    // --- Education CRUD ---
    @Transactional
    public Education addEducation(Long portfolioId, EducationCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        Education newEducation = new Education(request.getInstitution(), request.getCourse(), request.getPeriod(), request.getDescription(), portfolio);
        return educationRepository.save(newEducation);
    }

    public EducationDto findEducationById(Long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 학력 없음: " + educationId));
        return new EducationDto(education);
    }

    @Transactional
    public Education updateEducation(Long educationId, EducationCreateRequest request) {
        Education education = educationRepository.findById(educationId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 학력 없음: " + educationId));
        education.setInstitution(request.getInstitution());
        education.setCourse(request.getCourse());
        education.setPeriod(request.getPeriod());
        education.setDescription(request.getDescription());
        return educationRepository.save(education);
    }

    @Transactional
    public void deleteEducation(Long educationId) {
        educationRepository.deleteById(educationId);
    }

    // --- Certification CRUD ---
    @Transactional
    public Certification addCertification(Long portfolioId, CertificationCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        Certification newCertification = new Certification(request.getName(), request.getIssuer(), request.getAcquisitionDate(), portfolio);
        return certificationRepository.save(newCertification);
    }

    public CertificationDto findCertificationById(Long certificationId) {
        Certification certification = certificationRepository.findById(certificationId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 자격증 없음: " + certificationId));
        return new CertificationDto(certification);
    }

    @Transactional
    public Certification updateCertification(Long certificationId, CertificationCreateRequest request) {
        Certification certification = certificationRepository.findById(certificationId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 자격증 없음: " + certificationId));
        certification.setName(request.getName());
        certification.setIssuer(request.getIssuer());
        certification.setAcquisitionDate(request.getAcquisitionDate());
        return certificationRepository.save(certification);
    }

    @Transactional
    public void deleteCertification(Long certificationId) {
        certificationRepository.deleteById(certificationId);
    }

    // --- Project CRUD ---
    @Transactional
    public Project addProject(Long portfolioId, ProjectCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        Project newProject = new Project(request.getName(), request.getPeriod(), request.getDescription(), request.getTechStack(), request.getProjectUrl(), request.getTeamInfo(), portfolio);
        return projectRepository.save(newProject);
    }

    public ProjectDto findProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트 없음: " + projectId));
        return new ProjectDto(project);
    }

    @Transactional
    public Project updateProject(Long projectId, ProjectCreateRequest request, String thumbnailUrl) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트 없음: " + projectId));
        project.setName(request.getName());
        project.setPeriod(request.getPeriod());
        project.setDescription(request.getDescription());
        project.setTechStack(request.getTechStack());
        project.setProjectUrl(request.getProjectUrl());
        project.setTeamInfo(request.getTeamInfo());
        if (thumbnailUrl != null) {
            project.setThumbnailUrl(thumbnailUrl);
        }
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트 없음: " + projectId));
        fileStorageService.deleteFile(project.getThumbnailUrl());
        project.getDetails().forEach(detail -> fileStorageService.deleteFile(detail.getImageUrl()));
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public ProjectDetail addProjectDetail(Long projectId, String imageUrl, String description) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트 없음: " + projectId));
        ProjectDetail newDetail = new ProjectDetail(imageUrl, description, project);
        return projectDetailRepository.save(newDetail);
    }

    @Transactional
    public void deleteProjectDetail(Long projectDetailId) {
        ProjectDetail detail = projectDetailRepository.findById(projectDetailId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 상세 정보 없음: " + projectDetailId));
        fileStorageService.deleteFile(detail.getImageUrl());
        projectDetailRepository.deleteById(projectDetailId);
    }

    public Long findProjectByDetailId(Long projectDetailId) {
        ProjectDetail detail = projectDetailRepository.findById(projectDetailId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 상세 정보 없음: " + projectDetailId));
        return detail.getProject().getId();
    }

    @Transactional
    public void deleteProjectThumbnail(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트 없음: " + projectId));
        fileStorageService.deleteFile(project.getThumbnailUrl());
        project.setThumbnailUrl(null);
    }

    // --- Skill CRUD ---
    @Transactional
    public Skill addSkill(Long portfolioId, SkillCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 포트폴리오 없음: " + portfolioId));
        Skill newSkill = new Skill(request.getCategory(), request.getName(), portfolio);
        return skillRepository.save(newSkill);
    }

    public SkillDto findSkillById(Long skillId) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 스킬 없음: " + skillId));
        return new SkillDto(skill);
    }

    @Transactional
    public Skill updateSkill(Long skillId, SkillCreateRequest request) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 스킬 없음: " + skillId));
        skill.setCategory(request.getCategory());
        skill.setName(request.getName());
        return skillRepository.save(skill);
    }

    @Transactional
    public void deleteSkill(Long skillId) {
        skillRepository.deleteById(skillId);
    }
}