// src/main/java/com/example/demo/util/DataInitializer.java
package com.example.demo.util;

import com.example.demo.domain.*;
import com.example.demo.repository.PortfolioDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
            "개발은 결국 더 나은 사용자 경험을 만드는 일이라 생각합니다. 단순히 기능을 구현하는 것을 넘어서, 사용자가 진정으로 필요로 하는 가치를 전달하는 것이 저의 개발 철학입니다.",
            "1993.05.03 (만 32세)", "서울시 도봉구 노해로 70길", "wnsghl9897@naver.com",
            "010-5592-9271", "https://github.com/songjoonhoi"
        );

        // --- 2. 기술 스택 데이터 추가 ---
        
        // Languages
        portfolio.getSkills().add(new Skill("Languages", "Java", portfolio));
        portfolio.getSkills().add(new Skill("Languages", "Python", portfolio));
        portfolio.getSkills().add(new Skill("Languages", "JavaScript", portfolio));
        portfolio.getSkills().add(new Skill("Languages", "TypeScript", portfolio));
        portfolio.getSkills().add(new Skill("Languages", "HTML/CSS", portfolio));

        // Frameworks
        portfolio.getSkills().add(new Skill("Frameworks", "Spring Boot", portfolio));
        portfolio.getSkills().add(new Skill("Frameworks", "Spring Security", portfolio));
        portfolio.getSkills().add(new Skill("Frameworks", "Spring Data JPA", portfolio));
        portfolio.getSkills().add(new Skill("Frameworks", "React", portfolio));
        portfolio.getSkills().add(new Skill("Frameworks", "Node.js", portfolio));
        portfolio.getSkills().add(new Skill("Frameworks", "Express.js", portfolio));

        // Database
        portfolio.getSkills().add(new Skill("Database", "MySQL", portfolio));
        portfolio.getSkills().add(new Skill("Database", "PostgreSQL", portfolio));
        portfolio.getSkills().add(new Skill("Database", "MongoDB", portfolio));
        portfolio.getSkills().add(new Skill("Database", "Redis", portfolio));
        portfolio.getSkills().add(new Skill("Database", "H2", portfolio));

        // Cloud
        portfolio.getSkills().add(new Skill("Cloud", "AWS EC2", portfolio));
        portfolio.getSkills().add(new Skill("Cloud", "AWS RDS", portfolio));
        portfolio.getSkills().add(new Skill("Cloud", "AWS S3", portfolio));
        portfolio.getSkills().add(new Skill("Cloud", "Docker", portfolio));
        portfolio.getSkills().add(new Skill("Cloud", "GitHub Actions", portfolio));

        // Tools
        portfolio.getSkills().add(new Skill("Tools", "Git/GitHub", portfolio));
        portfolio.getSkills().add(new Skill("Tools", "IntelliJ IDEA", portfolio));
        portfolio.getSkills().add(new Skill("Tools", "VS Code", portfolio));
        portfolio.getSkills().add(new Skill("Tools", "Postman", portfolio));
        portfolio.getSkills().add(new Skill("Tools", "Figma", portfolio));

        // --- 3. 경력 데이터 추가 ---
        portfolio.getCareers().add(new Career(
            "(주)테크이노베이션", "백엔드 개발자",
            "2024.03 ~ 현재", "Spring Boot 기반 웹 애플리케이션 개발 및 유지보수",
            portfolio
        ));

        portfolio.getCareers().add(new Career(
            "프리랜서", "풀스택 개발자",
            "2023.01 ~ 2024.02", "중소기업 웹사이트 제작 및 솔루션 개발",
            portfolio
        ));

        // --- 4. 학력 데이터 추가 ---
        portfolio.getEducations().add(new Education(
            "코드스테이츠", "백엔드 부트캠프",
            "2022.06 ~ 2022.12", "Java, Spring Boot, AWS 등 백엔드 개발 전반에 대한 실무 중심 교육",
            portfolio
        ));

        portfolio.getEducations().add(new Education(
            "서울대학교", "컴퓨터공학과",
            "2012.03 ~ 2018.02", "자료구조, 알고리즘, 데이터베이스, 네트워크 등 컴퓨터 과학 기초 학습",
            portfolio
        ));

        // --- 5. 자격증 데이터 추가 ---
        portfolio.getCertifications().add(new Certification(
            "정보처리기사", "한국산업인력공단", "2023.11", portfolio
        ));

        portfolio.getCertifications().add(new Certification(
            "AWS Certified Solutions Architect", "Amazon Web Services", "2024.01", portfolio
        ));

        portfolio.getCertifications().add(new Certification(
            "SQLD", "한국데이터산업진흥원", "2023.09", portfolio
        ));

        // --- 6. 프로젝트 경험 추가 ---
        
        // 첫 번째 프로젝트: Filmora
        Project filmora = new Project(
            "Filmora - 실시간 영화 예매 시스템",
            "2024.10 ~ 2025.03",
            "AWS EC2 + RDS 배포, 동시성 제어로 응답속도 70% 개선",
            "Spring Boot, JPA, MySQL, Redis, AWS EC2/RDS, OAuth2, JWT",
            "https://filmora.kafolio.kr",
            "팀 프로젝트 (5명) - 백엔드 개발 담당",
            portfolio
        );
        filmora.setThumbnailUrl("/images/filmora-thumbnail.jpg");

        // Filmora 프로젝트 상세 정보 추가
        filmora.getDetails().add(new ProjectDetail(
            "/images/filmora-problem.jpg",
            "영화관 예매 서비스에서 동시 접속 시 좌석 중복 예약과 실시간 상태 반영 지연 문제가 빈번히 발생했습니다. 특히 인기 영화의 경우 수십 명이 동시에 같은 좌석을 예약하려 할 때 데이터 일관성 문제가 심각했습니다.",
            filmora
        ));
        
        filmora.getDetails().add(new ProjectDetail(
            "/images/filmora-action.jpg",
            "Spring Boot 기반 REST API 설계, JPA + MySQL로 동시성 제어 적용했습니다. Redis를 활용한 분산 락 구현으로 좌석 예약 시 Race Condition을 방지하고, 실시간 좌석 상태 업데이트를 위한 WebSocket 통신을 도입했습니다.",
            filmora
        ));
        
        filmora.getDetails().add(new ProjectDetail(
            "/images/filmora-result.jpg",
            "동시 예약 충돌률 0% 달성, 좌석 상태 업데이트 평균 응답 속도 70% 개선을 이루었습니다. 또한 AWS EC2 + RDS 환경에서 안정적인 서비스 운영이 가능해졌으며, 일일 활성 사용자 1000명 이상을 처리할 수 있는 확장성을 확보했습니다.",
            filmora
        ));
        
        portfolio.getProjects().add(filmora);

        // 두 번째 프로젝트: E-Commerce Platform
        Project ecommerce = new Project(
            "E-Commerce Platform",
            "2023.08 ~ 2023.12",
            "MSA 구조 도입으로 시스템 가용성 99.9% 달성",
            "Spring Boot, Spring Cloud, MySQL, Docker, Kubernetes",
            "https://github.com/songjoonhoi/ecommerce",
            "개인 프로젝트",
            portfolio
        );
        ecommerce.setThumbnailUrl("/images/ecommerce-thumbnail.jpg");

        ecommerce.getDetails().add(new ProjectDetail(
            "/images/ecommerce-architecture.jpg",
            "기존 모놀리틱 구조에서 발생하던 단일 장애점과 확장성 문제를 해결하기 위해 마이크로서비스 아키텍처로 전환했습니다. 사용자 서비스, 상품 서비스, 주문 서비스, 결제 서비스로 도메인을 분리하여 독립적인 배포와 확장이 가능하도록 설계했습니다.",
            ecommerce
        ));
        
        ecommerce.getDetails().add(new ProjectDetail(
            "/images/ecommerce-implementation.jpg",
            "Spring Cloud Gateway를 통한 API 라우팅, Eureka Server를 이용한 서비스 디스커버리, Spring Cloud Config로 중앙화된 설정 관리를 구현했습니다. Docker 컨테이너화와 Kubernetes 오케스트레이션으로 배포 자동화를 구축했습니다.",
            ecommerce
        ));
        
        ecommerce.getDetails().add(new ProjectDetail(
            "/images/ecommerce-monitoring.jpg",
            "시스템 가용성 99.9% 달성, 개별 서비스 장애 시에도 전체 시스템 영향도 최소화, 트래픽 증가 시 자동 스케일링으로 안정적인 서비스 제공이 가능해졌습니다. Prometheus + Grafana를 통한 모니터링 체계도 구축했습니다.",
            ecommerce
        ));
        
        portfolio.getProjects().add(ecommerce);

        // 세 번째 프로젝트: AI 챗봇 서비스
        Project chatBot = new Project(
            "AI 챗봇 서비스",
            "2023.03 ~ 2023.07",
            "자연어 처리 정확도 85% 달성, 고객 만족도 20% 향상",
            "Python, Django, PostgreSQL, OpenAI API, React, WebSocket",
            "https://github.com/songjoonhoi/ai-chatbot",
            "팀 프로젝트 (3명) - 백엔드 및 AI 모델 연동 담당",
            portfolio
        );
        chatBot.setThumbnailUrl("/images/chatbot-thumbnail.jpg");

        chatBot.getDetails().add(new ProjectDetail(
            "/images/chatbot-problem.jpg",
            "고객 문의 처리에 많은 인력과 시간이 소요되고, 반복적인 질문에 대한 일관된 답변 제공이 어려운 상황이었습니다. 24시간 고객 지원의 필요성과 응답 시간 단축이 주요 과제였습니다.",
            chatBot
        ));
        
        chatBot.getDetails().add(new ProjectDetail(
            "/images/chatbot-development.jpg",
            "OpenAI GPT API를 활용한 자연어 처리 엔진 구축, Django REST Framework로 API 서버 개발, WebSocket을 통한 실시간 채팅 구현, PostgreSQL로 대화 이력 관리 시스템을 구축했습니다. React 기반 웹 인터페이스로 사용자 친화적인 UI를 제공했습니다.",
            chatBot
        ));
        
        chatBot.getDetails().add(new ProjectDetail(
            "/images/chatbot-results.jpg",
            "자연어 처리 정확도 85% 달성, 평균 응답 시간 2초 이내, 고객 만족도 20% 향상을 이루었습니다. 반복 문의의 80%를 자동화하여 상담사 업무 효율성을 크게 개선했으며, 24시간 무중단 고객 지원 서비스를 실현했습니다.",
            chatBot
        ));
        
        portfolio.getProjects().add(chatBot);

        // 네 번째 프로젝트: 블로그 플랫폼
        Project blogPlatform = new Project(
            "개발자 커뮤니티 블로그 플랫폼",
            "2022.10 ~ 2023.02",
            "월간 활성 사용자 5,000명 달성, 게시글 로딩 속도 60% 개선",
            "Spring Boot, JPA, MySQL, Redis, Elasticsearch, AWS S3",
            "https://devblog.kafolio.kr",
            "개인 프로젝트",
            portfolio
        );
        blogPlatform.setThumbnailUrl("/images/blog-thumbnail.jpg");

        blogPlatform.getDetails().add(new ProjectDetail(
            "/images/blog-search.jpg",
            "기존 LIKE 검색으로는 한계가 있어 사용자들이 원하는 글을 찾기 어려웠고, 대량의 게시글 로딩 시 성능 저하가 심각했습니다. 또한 코드 하이라이팅과 마크다운 렌더링 성능 최적화가 필요했습니다.",
            blogPlatform
        ));
        
        blogPlatform.getDetails().add(new ProjectDetail(
            "/images/blog-implementation.jpg",
            "Elasticsearch를 도입하여 전문 검색 기능을 구현하고, Redis 캐싱으로 인기 글과 카테고리별 게시글 목록을 최적화했습니다. 이미지 업로드는 AWS S3를 활용하고, 페이지네이션과 무한 스크롤을 통해 UX를 개선했습니다.",
            blogPlatform
        ));
        
        blogPlatform.getDetails().add(new ProjectDetail(
            "/images/blog-performance.jpg",
            "검색 응답 시간 90% 단축, 게시글 로딩 속도 60% 개선을 달성했습니다. 월간 활성 사용자 5,000명을 달성했으며, 일일 평균 1,000건의 검색 쿼리를 안정적으로 처리하는 시스템을 구축했습니다.",
            blogPlatform
        ));
        
        portfolio.getProjects().add(blogPlatform);

        // --- 7. Repository를 통해 모든 데이터 저장 ---
        portfolioDataRepository.save(portfolio);
        
        System.out.println("=== 초기 데이터 생성 완료 ===");
        System.out.println("포트폴리오 기본 정보: " + portfolio.getName());
        System.out.println("기술 스택: " + portfolio.getSkills().size() + "개");
        System.out.println("경력: " + portfolio.getCareers().size() + "개");
        System.out.println("학력: " + portfolio.getEducations().size() + "개");
        System.out.println("자격증: " + portfolio.getCertifications().size() + "개");
        System.out.println("프로젝트: " + portfolio.getProjects().size() + "개");
        
        // 각 프로젝트별 상세 정보 개수 출력
        portfolio.getProjects().forEach(project -> {
            System.out.println("- " + project.getName() + ": " + project.getDetails().size() + "개 상세 정보");
        });
    }
}