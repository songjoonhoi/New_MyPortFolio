package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PortfolioResponse;
import com.example.demo.repository.PortfolioDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {
    
    private final PortfolioDataRepository portfolioDataRepository;

    public PortfolioResponse findPortfolio(Long id){
        // ID에 해당하는 포트폴리오 데이터를 모든 연관 정보와 함께 조회
        return portfolioDataRepository.findPortfolioWithDetailsById(id)
                .map(PortfolioResponse::new) // 조회된 Entity를 DTO로 변환
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오 정보를 찾을 수 없습니다. id=" + id));
    }
}
