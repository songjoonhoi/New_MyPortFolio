package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.service.PortfolioService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class PortfolioController {
    
    private final PortfolioService portfolioService;

    @GetMapping("/") // 웹사이트의 메인 페이지("/") 요청을 처리합니다.
    public String home(Model model) {
        // Service를 통해 ID가 1인 포트폴리오 정보를 조회합니다.
        model.addAttribute("portfolio", portfolioService.findPortfolio(1L));
        return "index"; // templates/index.html 파일을 찾아 렌더링합니다.
    }

    
    
}
