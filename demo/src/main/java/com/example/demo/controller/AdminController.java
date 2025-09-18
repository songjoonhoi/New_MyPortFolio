// src/main/java/com/example/demo/controller/AdminController.java
package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PortfolioService portfolioService;
    private final FileStorageService fileStorageService;

    /**
     * 관리자 메인 페이지
     */
    @GetMapping
    public String adminPage(Model model) {
        try {
            PortfolioResponse portfolio = portfolioService.findPortfolio(1L);
            model.addAttribute("portfolio", portfolio);
            return "admin/edit";
        } catch (Exception e) {
            log.error("관리자 페이지 로딩 실패", e);
            model.addAttribute("error", "포트폴리오 정보를 불러올 수 없습니다.");
            return "admin/edit";
        }
    }

    /**
     * 포트폴리오 기본 정보 업데이트
     */
    @PostMapping("/update")
    public Object updatePortfolio(
            @Valid PortfolioUpdateRequest updateRequest,
            BindingResult bindingResult,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        // AJAX 요청인지 확인
        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);
        
        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            if (isAjaxRequest) {
                return createErrorResponse("입력 정보를 확인해주세요.", bindingResult);
            } else {
                return "redirect:/admin?error=입력 정보를 확인해주세요.";
            }
        }

        try {
            String profileImageUrl = null;
            
            // 새 이미지가 업로드된 경우
            if (profileImage != null && !profileImage.isEmpty()) {
                profileImageUrl = fileStorageService.storeFile(profileImage);
                log.info("프로필 이미지 업로드 완료: {}", profileImageUrl);
            }

            // 서비스 호출하여 업데이트
            portfolioService.updatePortfolio(1L, updateRequest, profileImageUrl);

            if (isAjaxRequest) {
                return createSuccessResponse("기본 정보가 성공적으로 업데이트되었습니다.", null);
            } else {
                return "redirect:/admin?success=기본 정보가 업데이트되었습니다.";
            }

        } catch (Exception e) {
            log.error("포트폴리오 업데이트 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("업데이트 중 오류가 발생했습니다: " + e.getMessage(), null);
            } else {
                return "redirect:/admin?error=업데이트 중 오류가 발생했습니다.";
            }
        }
    }

    // ==================== 경력 관리 ====================

    /**
     * 경력 추가
     */
    @PostMapping("/career/add")
    public Object addCareer(
            @Valid CareerCreateRequest request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);

        if (bindingResult.hasErrors()) {
            if (isAjaxRequest) {
                return createErrorResponse("입력 정보를 확인해주세요.", bindingResult);
            }
            return "redirect:/admin?error=입력 정보를 확인해주세요.";
        }

        try {
            portfolioService.addCareer(1L, request);
            
            if (isAjaxRequest) {
                Map<String, Object> data = new HashMap<>();
                data.put("career", createCareerData(request));
                return createSuccessResponse("경력이 성공적으로 추가되었습니다.", data);
            }
            
            return "redirect:/admin?success=경력이 추가되었습니다.";

        } catch (Exception e) {
            log.error("경력 추가 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("경력 추가 중 오류가 발생했습니다: " + e.getMessage(), null);
            }
            return "redirect:/admin?error=경력 추가에 실패했습니다.";
        }
    }

    /**
     * 경력 삭제
     */
    @GetMapping("/career/delete/{careerId}")
    public Object deleteCareer(
            @PathVariable Long careerId,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);

        try {
            portfolioService.deleteCareer(1L, careerId);
            
            if (isAjaxRequest) {
                return createSuccessResponse("경력이 성공적으로 삭제되었습니다.", null);
            }
            
            return "redirect:/admin?success=경력이 삭제되었습니다.";

        } catch (Exception e) {
            log.error("경력 삭제 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("경력 삭제 중 오류가 발생했습니다: " + e.getMessage(), null);
            }
            return "redirect:/admin?error=경력 삭제에 실패했습니다.";
        }
    }

    /**
     * 경력 수정 페이지
     */
    @GetMapping("/career/edit/{careerId}")
    public String editCareerPage(@PathVariable Long careerId, Model model) {
        try {
            CareerDto career = portfolioService.findCareerById(careerId);
            model.addAttribute("career", career);
            return "admin/edit-career";
        } catch (Exception e) {
            log.error("경력 수정 페이지 로딩 실패", e);
            return "redirect:/admin?error=경력 정보를 불러올 수 없습니다.";
        }
    }

    /**
     * 경력 업데이트
     */
    @PostMapping("/career/update/{careerId}")
    public String updateCareer(@PathVariable Long careerId, @Valid CareerCreateRequest request) {
        try {
            portfolioService.updateCareer(careerId, request);
            return "redirect:/admin?success=경력이 수정되었습니다.";
        } catch (Exception e) {
            log.error("경력 업데이트 실패", e);
            return "redirect:/admin?error=경력 수정에 실패했습니다.";
        }
    }

    // ==================== 학력 관리 ====================

    /**
     * 학력 추가
     */
    @PostMapping("/education/add")
    public Object addEducation(
            @Valid EducationCreateRequest request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);

        if (bindingResult.hasErrors()) {
            if (isAjaxRequest) {
                return createErrorResponse("입력 정보를 확인해주세요.", bindingResult);
            }
            return "redirect:/admin?error=입력 정보를 확인해주세요.";
        }

        try {
            portfolioService.addEducation(1L, request);
            
            if (isAjaxRequest) {
                Map<String, Object> data = new HashMap<>();
                data.put("education", createEducationData(request));
                return createSuccessResponse("학력이 성공적으로 추가되었습니다.", data);
            }
            
            return "redirect:/admin?success=학력이 추가되었습니다.";

        } catch (Exception e) {
            log.error("학력 추가 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("학력 추가 중 오류가 발생했습니다: " + e.getMessage(), null);
            }
            return "redirect:/admin?error=학력 추가에 실패했습니다.";
        }
    }

    /**
     * 학력 삭제
     */
    @GetMapping("/education/delete/{educationId}")
    public Object deleteEducation(
            @PathVariable Long educationId,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);

        try {
            portfolioService.deleteEducation(educationId);
            
            if (isAjaxRequest) {
                return createSuccessResponse("학력이 성공적으로 삭제되었습니다.", null);
            }
            
            return "redirect:/admin?success=학력이 삭제되었습니다.";

        } catch (Exception e) {
            log.error("학력 삭제 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("학력 삭제 중 오류가 발생했습니다: " + e.getMessage(), null);
            }
            return "redirect:/admin?error=학력 삭제에 실패했습니다.";
        }
    }

    /**
     * 학력 수정 페이지
     */
    @GetMapping("/education/edit/{educationId}")
    public String editEducationPage(@PathVariable Long educationId, Model model) {
        try {
            EducationDto education = portfolioService.findEducationById(educationId);
            model.addAttribute("education", education);
            return "admin/edit-education";
        } catch (Exception e) {
            log.error("학력 수정 페이지 로딩 실패", e);
            return "redirect:/admin?error=학력 정보를 불러올 수 없습니다.";
        }
    }

    /**
     * 학력 업데이트
     */
    @PostMapping("/education/update/{educationId}")
    public String updateEducation(@PathVariable Long educationId, @Valid EducationCreateRequest request) {
        try {
            portfolioService.updateEducation(educationId, request);
            return "redirect:/admin?success=학력이 수정되었습니다.";
        } catch (Exception e) {
            log.error("학력 업데이트 실패", e);
            return "redirect:/admin?error=학력 수정에 실패했습니다.";
        }
    }

    // ==================== 자격증 관리 ====================

    /**
     * 자격증 추가
     */
    @PostMapping("/certification/add")
    public Object addCertification(
            @Valid CertificationCreateRequest request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);

        if (bindingResult.hasErrors()) {
            if (isAjaxRequest) {
                return createErrorResponse("입력 정보를 확인해주세요.", bindingResult);
            }
            return "redirect:/admin?error=입력 정보를 확인해주세요.";
        }

        try {
            portfolioService.addCertification(1L, request);
            
            if (isAjaxRequest) {
                Map<String, Object> data = new HashMap<>();
                data.put("certification", createCertificationData(request));
                return createSuccessResponse("자격증이 성공적으로 추가되었습니다.", data);
            }
            
            return "redirect:/admin?success=자격증이 추가되었습니다.";

        } catch (Exception e) {
            log.error("자격증 추가 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("자격증 추가 중 오류가 발생했습니다: " + e.getMessage(), null);
            }
            return "redirect:/admin?error=자격증 추가에 실패했습니다.";
        }
    }

    /**
     * 자격증 삭제
     */
    @GetMapping("/certification/delete/{certificationId}")
    public Object deleteCertification(
            @PathVariable Long certificationId,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        boolean isAjaxRequest = "XMLHttpRequest".equals(requestedWith);

        try {
            portfolioService.deleteCertification(certificationId);
            
            if (isAjaxRequest) {
                return createSuccessResponse("자격증이 성공적으로 삭제되었습니다.", null);
            }
            
            return "redirect:/admin?success=자격증이 삭제되었습니다.";

        } catch (Exception e) {
            log.error("자격증 삭제 실패", e);
            if (isAjaxRequest) {
                return createErrorResponse("자격증 삭제 중 오류가 발생했습니다: " + e.getMessage(), null);
            }
            return "redirect:/admin?error=자격증 삭제에 실패했습니다.";
        }
    }

    /**
     * 자격증 수정 페이지
     */
    @GetMapping("/certification/edit/{certificationId}")
    public String editCertificationPage(@PathVariable Long certificationId, Model model) {
        try {
            CertificationDto certification = portfolioService.findCertificationById(certificationId);
            model.addAttribute("certification", certification);
            return "admin/edit-certification";
        } catch (Exception e) {
            log.error("자격증 수정 페이지 로딩 실패", e);
            return "redirect:/admin?error=자격증 정보를 불러올 수 없습니다.";
        }
    }

    /**
     * 자격증 업데이트
     */
    @PostMapping("/certification/update/{certificationId}")
    public String updateCertification(@PathVariable Long certificationId, @Valid CertificationCreateRequest request) {
        try {
            portfolioService.updateCertification(certificationId, request);
            return "redirect:/admin?success=자격증이 수정되었습니다.";
        } catch (Exception e) {
            log.error("자격증 업데이트 실패", e);
            return "redirect:/admin?error=자격증 수정에 실패했습니다.";
        }
    }

    // ==================== 프로젝트 관리 ====================

    /**
     * 프로젝트 추가
     */
    @PostMapping("/project/add")
    public String addProject(
            @Valid ProjectCreateRequest request,
            @RequestParam("thumbnailImage") MultipartFile thumbnailImage) {
        try {
            com.example.demo.domain.Project newProject = portfolioService.addProject(1L, request);
            
            if (!thumbnailImage.isEmpty()) {
                String thumbnailUrl = fileStorageService.storeFile(thumbnailImage);
                portfolioService.updateProject(newProject.getId(), request, thumbnailUrl);
            }
            
            return "redirect:/admin/project/edit/" + newProject.getId();
        } catch (Exception e) {
            log.error("프로젝트 추가 실패", e);
            return "redirect:/admin?error=프로젝트 추가에 실패했습니다.";
        }
    }

    /**
     * 프로젝트 수정 페이지
     */
    @GetMapping("/project/edit/{projectId}")
    public String editProjectPage(@PathVariable Long projectId, Model model) {
        try {
            ProjectDto project = portfolioService.findProjectById(projectId);
            model.addAttribute("project", project);
            return "admin/edit-project";
        } catch (Exception e) {
            log.error("프로젝트 수정 페이지 로딩 실패", e);
            return "redirect:/admin?error=프로젝트 정보를 불러올 수 없습니다.";
        }
    }

    /**
     * 프로젝트 업데이트
     */
    @PostMapping("/project/update/{projectId}")
    public String updateProject(
            @PathVariable Long projectId,
            @Valid ProjectCreateRequest request,
            @RequestParam(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
        try {
            String thumbnailUrl = null;
            if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
                thumbnailUrl = fileStorageService.storeFile(thumbnailImage);
            }
            portfolioService.updateProject(projectId, request, thumbnailUrl);
            return "redirect:/admin/project/edit/" + projectId + "?success=프로젝트가 수정되었습니다.";
        } catch (Exception e) {
            log.error("프로젝트 업데이트 실패", e);
            return "redirect:/admin/project/edit/" + projectId + "?error=프로젝트 수정에 실패했습니다.";
        }
    }

    /**
     * 프로젝트 삭제
     */
    @GetMapping("/project/delete/{projectId}")
    public String deleteProject(@PathVariable Long projectId) {
        try {
            portfolioService.deleteProject(projectId);
            return "redirect:/admin?success=프로젝트가 삭제되었습니다.";
        } catch (Exception e) {
            log.error("프로젝트 삭제 실패", e);
            return "redirect:/admin?error=프로젝트 삭제에 실패했습니다.";
        }
    }

    /**
     * 프로젝트 상세 정보 추가
     */
    @PostMapping("/project/{projectId}/addDetail")
    public String addProjectDetail(
            @PathVariable Long projectId,
            @RequestParam("detailImage") MultipartFile detailImage,
            @RequestParam("description") String description) {
        try {
            String imageUrl = fileStorageService.storeFile(detailImage);
            portfolioService.addProjectDetail(projectId, imageUrl, description);
            return "redirect:/admin/project/edit/" + projectId + "?success=상세 정보가 추가되었습니다.";
        } catch (Exception e) {
            log.error("프로젝트 상세 정보 추가 실패", e);
            return "redirect:/admin/project/edit/" + projectId + "?error=상세 정보 추가에 실패했습니다.";
        }
    }

    /**
     * 프로젝트 상세 정보 삭제
     */
    @GetMapping("/project/detail/delete/{detailId}")
    public String deleteProjectDetail(@PathVariable Long detailId) {
        try {
            Long projectId = portfolioService.findProjectByDetailId(detailId);
            portfolioService.deleteProjectDetail(detailId);
            return "redirect:/admin/project/edit/" + projectId + "?success=상세 정보가 삭제되었습니다.";
        } catch (Exception e) {
            log.error("프로젝트 상세 정보 삭제 실패", e);
            return "redirect:/admin?error=상세 정보 삭제에 실패했습니다.";
        }
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 성공 응답 생성
     */
    private ResponseEntity<Map<String, Object>> createSuccessResponse(String message, Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 에러 응답 생성
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        
        if (bindingResult != null && bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            response.put("errors", errors);
        }
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 경력 데이터 생성
     */
    private Map<String, String> createCareerData(CareerCreateRequest request) {
        Map<String, String> career = new HashMap<>();
        career.put("company", request.getCompany());
        career.put("position", request.getPosition());
        career.put("period", request.getPeriod());
        career.put("description", request.getDescription());
        return career;
    }

    /**
     * 학력 데이터 생성
     */
    private Map<String, String> createEducationData(EducationCreateRequest request) {
        Map<String, String> education = new HashMap<>();
        education.put("institution", request.getInstitution());
        education.put("course", request.getCourse());
        education.put("period", request.getPeriod());
        education.put("description", request.getDescription());
        return education;
    }

    /**
     * 자격증 데이터 생성
     */
    private Map<String, String> createCertificationData(CertificationCreateRequest request) {
        Map<String, String> certification = new HashMap<>();
        certification.put("name", request.getName());
        certification.put("issuer", request.getIssuer());
        certification.put("acquisitionDate", request.getAcquisitionDate());
        return certification;
    }
}