package com.example.demo.controller;

import com.example.demo.dto.PortfolioResponse;
import com.example.demo.dto.PortfolioUpdateRequest; 
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.CareerCreateRequest;
import com.example.demo.dto.CareerDto;
import com.example.demo.dto.EducationCreateRequest; 
import com.example.demo.dto.EducationDto;
import com.example.demo.dto.CertificationCreateRequest;
import com.example.demo.dto.CertificationDto;
import com.example.demo.domain.Project;
import com.example.demo.dto.ProjectCreateRequest;
import com.example.demo.dto.ProjectDto;
import org.springframework.web.multipart.MultipartFile;

;


@Controller
@RequestMapping("/admin") // 이 컨트롤러의 모든 요청은 /admin 으로 시작
@RequiredArgsConstructor
public class AdminController {

    private final PortfolioService portfolioService;
    private final FileStorageService fileStorageService;

    
    
    // 관리자 메인 페이지 (포트폴리오 수정 폼)
    @GetMapping
    public String adminPage(Model model) {
        PortfolioResponse portfolio = portfolioService.findPortfolio(1L);
        model.addAttribute("portfolio", portfolio);
        return "admin/edit"; 
    }
    

    @PostMapping("/update")
    public String updatePortfolio(
            PortfolioUpdateRequest updateRequest, // 폼 데이터를 DTO로 받음
            @RequestParam("profileImage") MultipartFile profileImage) {
        
        String profileImageUrl = null;
        // 새 이미지가 업로드된 경우에만 파일 저장 및 URL 업데이트
        if (!profileImage.isEmpty()) {
            profileImageUrl = fileStorageService.storeFile(profileImage);
        }

        // Service를 호출하여 모든 데이터 업데이트
        portfolioService.updatePortfolio(1L, updateRequest, profileImageUrl);

        return "redirect:/admin";
    }

    @PostMapping("/career/add")
    public String addCareer(CareerCreateRequest request) {
        portfolioService.addCareer(1L, request);
        return "redirect:/admin";
    }
    

    @GetMapping("/career/delete/{careerId}")
    public String deleteCareer(@PathVariable Long careerId) {
        portfolioService.deleteCareer(1L, careerId); 
        return "redirect:/admin";
    }
    
    // 경력 수정 페이지로 이동하는 메소드
    @GetMapping("/career/edit/{careerId}")
    public String editCareerPage(@PathVariable Long careerId, Model model) {
        // ID를 이용해 기존 경력 정보를 불러옵니다.
        CareerDto career = portfolioService.findCareerById(careerId);
        model.addAttribute("career", career);
        return "admin/edit-career"; // templates/admin/edit-career.html
    }

    // 경력 수정을 처리하는 메소드
    @PostMapping("/career/update/{careerId}")
    public String updateCareer(@PathVariable Long careerId, CareerCreateRequest request) {
        portfolioService.updateCareer(careerId, request);
        return "redirect:/admin";
    }

    // --- Education ---
    @PostMapping("/education/add")
    public String addEducation(EducationCreateRequest request) {
        portfolioService.addEducation(1L, request);
        return "redirect:/admin";
    }

    @GetMapping("/education/edit/{educationId}")
    public String editEducationPage(@PathVariable Long educationId, Model model) {
        EducationDto education = portfolioService.findEducationById(educationId);
        model.addAttribute("education", education);
        return "admin/edit-education"; // templates/admin/edit-education.html
    }

    @PostMapping("/education/update/{educationId}")
    public String updateEducation(@PathVariable Long educationId, EducationCreateRequest request) {
        portfolioService.updateEducation(educationId, request);
        return "redirect:/admin";
    }

    @GetMapping("/education/delete/{educationId}")
    public String deleteEducation(@PathVariable Long educationId) {
        portfolioService.deleteEducation(educationId);
        return "redirect:/admin";
    }

    // --- Certification ---
    @PostMapping("/certification/add")
    public String addCertification(CertificationCreateRequest request) {
        portfolioService.addCertification(1L, request);
        return "redirect:/admin";
    }

    @GetMapping("/certification/edit/{certificationId}")
    public String editCertificationPage(@PathVariable Long certificationId, Model model) {
        CertificationDto certification = portfolioService.findCertificationById(certificationId);
        model.addAttribute("certification", certification);
        return "admin/edit-certification";
    }

    @PostMapping("/certification/update/{certificationId}")
    public String updateCertification(@PathVariable Long certificationId, CertificationCreateRequest request) {
        portfolioService.updateCertification(certificationId, request);
        return "redirect:/admin";
    }

    @GetMapping("/certification/delete/{certificationId}")
    public String deleteCertification(@PathVariable Long certificationId) {
        portfolioService.deleteCertification(certificationId);
        return "redirect:/admin";
    }

     // --- Project ---
    @PostMapping("/project/add")
    public String addProject(ProjectCreateRequest request, @RequestParam("thumbnailImage") MultipartFile thumbnailImage) {
        Project newProject = portfolioService.addProject(1L, request);
        
        if (!thumbnailImage.isEmpty()) {
            String thumbnailUrl = fileStorageService.storeFile(thumbnailImage);
            portfolioService.updateProject(newProject.getId(), request, thumbnailUrl);
        }
        
        // 새로 생성된 프로젝트의 수정 페이지로 바로 이동
        return "redirect:/admin/project/edit/" + newProject.getId();
    }

    @GetMapping("/project/edit/{projectId}")
    public String editProjectPage(@PathVariable Long projectId, Model model) {
        ProjectDto project = portfolioService.findProjectById(projectId);
        model.addAttribute("project", project);
        return "admin/edit-project"; // templates/admin/edit-project.html
    }

    @PostMapping("/project/update/{projectId}")
    public String updateProject(@PathVariable Long projectId, ProjectCreateRequest request, @RequestParam("thumbnailImage") MultipartFile thumbnailImage) {
        String thumbnailUrl = null;
        if (!thumbnailImage.isEmpty()) {
            thumbnailUrl = fileStorageService.storeFile(thumbnailImage);
        }
        portfolioService.updateProject(projectId, request, thumbnailUrl);
        return "redirect:/admin/project/edit/" + projectId;
    }

    @GetMapping("/project/delete/{projectId}")
    public String deleteProject(@PathVariable Long projectId) {
        portfolioService.deleteProject(projectId);
        return "redirect:/admin";
    }

    @PostMapping("/project/{projectId}/addDetail")
    public String addProjectDetail(@PathVariable Long projectId,
                                   @RequestParam("detailImage") MultipartFile detailImage,
                                   @RequestParam("description") String description) {
        String imageUrl = fileStorageService.storeFile(detailImage);
        portfolioService.addProjectDetail(projectId, imageUrl, description);
        return "redirect:/admin/project/edit/" + projectId;
    }

    @GetMapping("/project/detail/delete/{detailId}")
    public String deleteProjectDetail(@PathVariable Long detailId) {
        // detailId로 project를 찾아 redirect해야 하는 번거로움이 있으므로 서비스에서 처리
        Long projectId = portfolioService.findProjectByDetailId(detailId); // 이 메소드 추가 필요
        portfolioService.deleteProjectDetail(detailId);
        return "redirect:/admin/project/edit/" + projectId;
    }
}