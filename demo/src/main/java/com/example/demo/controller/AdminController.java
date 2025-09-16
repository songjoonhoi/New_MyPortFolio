package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.PortfolioResponse;
import com.example.demo.service.PortfolioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin") // 이 컨트롤러의 모든 요청은 /admin 으로 시작
@RequiredArgsConstructor
public class AdminController {

    private final PortfolioService portfolioService;

    
    
    // 관리자 메인 페이지 (포트폴리오 수정 폼)
    @GetMapping
    public String adminPage(Model model) {
        // 기존 포트폴리오 데이터를 불러와서 모델에 담아 전달
        PortfolioResponse portfolio = portfolioService.findPortfolio(1L);
        model.addAttribute("portfolio", portfolio);
        return "admin/edit"; // templates/admin/edit.html
    }
    
    // TODO: 포트폴리오 데이터를 실제로 수정(POST), 삭제(DELETE)하는 핸들러 메소드 추가 예정
}