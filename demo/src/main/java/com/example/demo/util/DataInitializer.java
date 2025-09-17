// src/main/java/com/example/demo/util/DataInitializer.java
package com.example.demo.util;

import com.example.demo.domain.*;
import com.example.demo.repository.PortfolioDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PortfolioDataRepository portfolioDataRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (portfolioDataRepository.count() > 0) {
            return;
        }

        // --- 1. 포트폴리오 메인 데이터 생성 ---
        PortfolioData portfolio = new PortfolioData(
            "송준회", "백엔드 개발자",
            "\"개발은 결국 더 나은 사용자 경험을 만드는 일이라 생각합니다...\"",
            "1993.05.03 (만 32세)", "서울시 도봉구 노해로 70길", "wnsghl9897@naver.com",
            "010-5592-9271", "https://github.com/songjoonhoi"
        );

        // --- 2. 연관 데이터 생성 및 연결 ---
        
        // ... (Skill, Career, Education, Certification 데이터 추가 로직은 동일) ...

        // ▼▼▼ [수정됨] 프로젝트 경험 (새로운 구조) ▼▼▼
        Project filmora = new Project(
            "Filmora - 실시간 영화 예매 시스템",
            "2024.10 ~ 2025.03",
            "AWS EC2 + RDS 배포, 동시성 제어로 응답속도 70% 개선",
            "Spring Boot, JPA, MySQL, Redis, AWS EC2/RDS, OAuth2, JWT",
            "https://filmora.kafolio.kr",
            "팀 프로젝트 (5명) - 백엔드 개발 담당",
            portfolio
        );
        filmora.setThumbnailUrl("/images/filmora-thumbnail.jpg"); // 썸네일 설정

        // 상세 정보 (이미지 + 설명) 추가
        filmora.getDetails().add(new ProjectDetail(
            "/images/filmora-problem.jpg",
            "영화관 예매 서비스에서 동시 접속 시 좌석 중복 예약과 실시간 상태 반영 지연 문제가 빈번히 발생...",
            filmora
        ));
        filmora.getDetails().add(new ProjectDetail(
            "/images/filmora-action.jpg",
            "Spring Boot 기반 REST API 설계, JPA + MySQL로 동시성 제어 적용...",
            filmora
        ));
        filmora.getDetails().add(new ProjectDetail(
            "/images/filmora-result.jpg",
            "동시 예약 충돌률 0% 달성, 좌석 상태 업데이트 평균 응답 속도 70% 개선...",
            filmora
        ));
        portfolio.getProjects().add(filmora);


        // --- 3. Repository를 통해 모든 데이터 저장 ---
        portfolioDataRepository.save(portfolio);
    }
}