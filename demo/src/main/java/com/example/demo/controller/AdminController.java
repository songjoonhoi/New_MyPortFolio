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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
    // ▼▼▼ [새로 추가] 경력 수정 페이지로 이동하는 메소드 ▼▼▼
    @GetMapping("/career/edit/{careerId}")
    public String editCareerPage(@PathVariable Long careerId, Model model) {
        // ID를 이용해 기존 경력 정보를 불러옵니다.
        CareerDto career = portfolioService.findCareerById(careerId);
        model.addAttribute("career", career);
        return "admin/edit-career"; // templates/admin/edit-career.html
    }

    // ▼▼▼ [새로 추가] 경력 수정을 처리하는 메소드 ▼▼▼
    @PostMapping("/career/update/{careerId}")
    public String updateCareer(@PathVariable Long careerId, CareerCreateRequest request) {
        portfolioService.updateCareer(careerId, request);
        return "redirect:/admin";
    }

}