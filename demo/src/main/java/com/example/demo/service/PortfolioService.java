package com.example.demo.service;

import com.example.demo.domain.PortfolioData;
import com.example.demo.dto.PortfolioResponse;
import com.example.demo.dto.PortfolioUpdateRequest;
import com.example.demo.repository.CareerRepository;
import com.example.demo.repository.CertificationRepository;
import com.example.demo.repository.PortfolioDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.domain.Career;
import com.example.demo.domain.Certification;
import com.example.demo.domain.Education;
import com.example.demo.domain.PortfolioData;
import com.example.demo.dto.CareerCreateRequest;
import com.example.demo.dto.CareerDto;
import com.example.demo.dto.CertificationCreateRequest;
import com.example.demo.dto.CertificationDto;
import com.example.demo.dto.EducationCreateRequest;
import com.example.demo.dto.EducationDto;
import com.example.demo.repository.EducationRepository;
import com.example.demo.domain.Project;
import com.example.demo.domain.ProjectDetail;
import com.example.demo.dto.ProjectCreateRequest;
import com.example.demo.dto.ProjectDto;
import com.example.demo.repository.ProjectDetailRepository;
import com.example.demo.repository.ProjectRepository;


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

    public PortfolioResponse findPortfolio(Long id){
        // ID에 해당하는 포트폴리오 데이터를 모든 연관 정보와 함께 조회
        return portfolioDataRepository.findPortfolioWithDetailsById(id)
                .map(PortfolioResponse::new) // 조회된 Entity를 DTO로 변환
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오 정보를 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest request, String newImageUrl) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오 정보를 찾을 수 없습니다. id=" + portfolioId));

        // DTO의 값으로 Entity의 모든 필드를 업데이트
        portfolio.setName(request.getName());
        portfolio.setJobTitle(request.getJobTitle());
        portfolio.setPhilosophy(request.getPhilosophy());
        portfolio.setBirth(request.getBirth());
        portfolio.setAddress(request.getAddress());
        portfolio.setEmail(request.getEmail());
        portfolio.setPhone(request.getPhone());
        portfolio.setGithubUrl(request.getGithubUrl());

        // 새 이미지 URL이 있는 경우에만 업데이트
        if (newImageUrl != null) {
            portfolio.setProfileImageUrl(newImageUrl);
        }

        // 변경된 내용을 DB에 저장 (이미 @Transactional 상태이므로 save 호출은 선택사항이지만 명시적으로 작성)
        portfolioDataRepository.save(portfolio);
    }

    @Transactional
    public void addCareer(Long portfolioId, CareerCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오 정보를 찾을 수 없습니다. id=" + portfolioId));

        Career newCareer = new Career(
                request.getCompany(),
                request.getPosition(),
                request.getPeriod(),
                request.getDescription(),
                portfolio
        );

        portfolio.getCareers().add(newCareer);
    }

    @Transactional
    public void deleteCareer(Long portfolioId, Long careerId) {
        careerRepository.deleteById(careerId);
    }

    // ID로 특정 경력 항목을 찾는 메소드 
    public CareerDto findCareerById(Long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new IllegalArgumentException("경력 정보를 찾을 수 없습니다. id=" + careerId));
        return new CareerDto(career); // Entity를 DTO로 변환하여 반환
    }

    // 경력(Career) 항목을 수정하는 메소드 
    @Transactional
    public void updateCareer(Long careerId, CareerCreateRequest request) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new IllegalArgumentException("경력 정보를 찾을 수 없습니다. id=" + careerId));

        // 요청받은 DTO의 데이터로 기존 Entity의 값을 변경
        career.setCompany(request.getCompany());
        career.setPosition(request.getPosition());
        career.setPeriod(request.getPeriod());
        career.setDescription(request.getDescription());
        
    }

    // --- Education CRUD ---
    @Transactional
    public void addEducation(Long portfolioId, EducationCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(/* ... */);
        Education newEducation = new Education(request.getInstitution(), request.getCourse(), request.getPeriod(), request.getDescription(), portfolio);
        portfolio.getEducations().add(newEducation);
    }

    public EducationDto findEducationById(Long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(() -> new IllegalArgumentException("학력 정보를 찾을 수 없습니다. id=" + educationId));
        return new EducationDto(education);
    }

    @Transactional
    public void updateEducation(Long educationId, EducationCreateRequest request) {
        Education education = educationRepository.findById(educationId).orElseThrow(/* ... */);
        education.setInstitution(request.getInstitution());
        education.setCourse(request.getCourse());
        education.setPeriod(request.getPeriod());
        education.setDescription(request.getDescription());
    }

    @Transactional
    public void deleteEducation(Long educationId) {
        educationRepository.deleteById(educationId);
    }

    // --- Certification CRUD ---
    @Transactional
    public void addCertification(Long portfolioId, CertificationCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId).orElseThrow(/* ... */);
        Certification newCertification = new Certification(request.getName(), request.getIssuer(), request.getAcquisitionDate(), portfolio);
        portfolio.getCertifications().add(newCertification);
    }

    public CertificationDto findCertificationById(Long certificationId) {
        Certification certification = certificationRepository.findById(certificationId).orElseThrow(() -> new IllegalArgumentException("자격증 정보를 찾을 수 없습니다. id=" + certificationId));
        return new CertificationDto(certification);
    }

    @Transactional
    public void updateCertification(Long certificationId, CertificationCreateRequest request) {
        Certification certification = certificationRepository.findById(certificationId).orElseThrow(/* ... */);
        certification.setName(request.getName());
        certification.setIssuer(request.getIssuer());
        certification.setAcquisitionDate(request.getAcquisitionDate());
    }

    @Transactional
    public void deleteCertification(Long certificationId) {
        certificationRepository.deleteById(certificationId);
    }

    // --- Project CRUD ---
    @Transactional
    public Project addProject(Long portfolioId, ProjectCreateRequest request) {
        PortfolioData portfolio = portfolioDataRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오 정보를 찾을 수 없습니다. id=" + portfolioId));

        Project newProject = new Project(
                request.getName(),
                request.getPeriod(),
                request.getDescription(),
                request.getTechStack(),
                request.getProjectUrl(),
                request.getTeamInfo(),
                portfolio
        );
        
        return projectRepository.save(newProject);
    }

    public ProjectDto findProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 정보를 찾을 수 없습니다. id=" + projectId));
        return new ProjectDto(project);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectCreateRequest request, String thumbnailUrl) {
        Project project = projectRepository.findById(projectId).orElseThrow(/*...*/);
        project.setName(request.getName());
        project.setPeriod(request.getPeriod());
        project.setDescription(request.getDescription());
        project.setTechStack(request.getTechStack());
        project.setProjectUrl(request.getProjectUrl());
        project.setTeamInfo(request.getTeamInfo());
        if (thumbnailUrl != null) {
            project.setThumbnailUrl(thumbnailUrl);
        }
    }
    
    @Transactional
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public void addProjectDetail(Long projectId, String imageUrl, String description) {
        Project project = projectRepository.findById(projectId).orElseThrow(/*...*/);
        ProjectDetail newDetail = new ProjectDetail(imageUrl, description, project);
        project.getDetails().add(newDetail);
    }

    @Transactional
    public void deleteProjectDetail(Long projectDetailId) {
        projectDetailRepository.deleteById(projectDetailId);
    }

    public Long findProjectByDetailId(Long projectDetailId) {
        ProjectDetail detail = projectDetailRepository.findById(projectDetailId)
                .orElseThrow(() -> new IllegalArgumentException("상세 정보를 찾을 수 없습니다. id=" + projectDetailId));
        return detail.getProject().getId();
    }
}
