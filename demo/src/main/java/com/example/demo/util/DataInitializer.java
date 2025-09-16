package com.example.demo.util;

import com.example.demo.domain.Career;
import com.example.demo.domain.Certification;
import com.example.demo.domain.Education;
import com.example.demo.domain.PortfolioData;
import com.example.demo.domain.Project;
import com.example.demo.domain.Skill;
import com.example.demo.repository.PortfolioDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set; // List 대신 Set을 import 합니다.

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PortfolioDataRepository portfolioDataRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 데이터가 이미 존재하면 초기화하지 않음
        if (portfolioDataRepository.count() > 0) {
            return;
        }

        // 1. 포트폴리오 메인 데이터 생성
        PortfolioData portfolio = new PortfolioData(
            "송준회", "백엔드 개발자",
            "\"개발은 결국 더 나은 사용자 경험을 만드는 일이라 생각합니다. Redis·MySQL·AWS를 활용해 실시간 예매 시스템을 구축하며, 동시성 제어로 좌석 충돌률 0%를 달성했습니다. 안정적이고 확장 가능한 백엔드 아키처를 설계하여 사용자에게 신뢰받는 서비스를 만드는 개발자가 되겠습니다.\"",
            "1993.05.03 (만 32세)", "서울시 도봉구 노해로 70길", "wnsghl9897@naver.com",
            "010-5592-9271", "https://github.com/songjoonhoi"
        );

        // 2. 연관 데이터 생성 및 연결
        // 기술 스택 (List -> Set 으로 변경)
        Set<Skill> skills = Set.of( // List.of() -> Set.of()
            new Skill("Languages", "Java", portfolio),
            new Skill("Languages", "Python", portfolio),
            new Skill("Languages", "JavaScript", portfolio),
            new Skill("Frameworks", "Spring Boot", portfolio),
            new Skill("Frameworks", "Spring Security", portfolio),
            new Skill("Database", "MySQL", portfolio),
            new Skill("Database", "Oracle", portfolio),
            new Skill("Cloud", "AWS (EC2, RDS, S3, VPC)", portfolio),
            new Skill("Tools", "Redis", portfolio),
            new Skill("Tools", "JPA", portfolio),
            new Skill("Tools", "JWT", portfolio)
        );
        portfolio.getSkills().addAll(skills);

        // 프로젝트 경험
        Project filmora = new Project(
            "Filmora - 실시간 영화 예매 시스템", "2024.10 ~ 2025.03",
            "AWS EC2 + RDS 배포, 동시성 제어로 응답속도 70% 개선",
            "영화관 예매 서비스에서 동시 접속 시 좌석 중복 예약과 실시간 상태 반영 지연 문제가 빈번히 발생. 안정적인 좌석 관리와 결제 프로세스를 지원하는 백엔드 아키텍처 필요.",
            "Spring Boot 기반 REST API 설계, JPA + MySQL로 동시성 제어(Optimistic Locking) 적용, Redis 캐싱으로 조회 속도 최적화, AWS EC2 + RDS 환경에 배포 및 모니터링",
            "동시 예약 충돌률 0% 달성, 좌석 상태 업데이트 평균 응답 속도 70% 개선, DB 조회 부하 약 60% 감소, 데이터 동기화 정확도 100% 유지",
            "Spring Boot, JPA, MySQL, Redis, AWS EC2/RDS, OAuth2, JWT",
            "https://filmora.kafolio.kr",
            "팀 프로젝트 (5명) - 백엔드 개발 담당 (예매 핵심 데이터 API, 실시간 좌석 정보, 결제/환불 시스템)",
            portfolio
        );
        portfolio.getProjects().add(filmora);

        // 교육사항
        Education koriaIt = new Education(
            "코리아IT아카데미 노원", "클라우드 활용 자바개발자 과정", "2024.07 ~ 2025.07",
            "백엔드 개발(Java/Spring), 클라우드 인프라(AWS), 웹 개발(Frontend), 데이터베이스 & 성능 최적화, 네트워크 & 서버 운영",
            portfolio
        );
        portfolio.getEducations().add(koriaIt);
        portfolio.getEducations().add(new Education("상명대학교", "사회체육학과 졸업 (편입)", "2015.03 – 2021.02", "", portfolio));
        portfolio.getEducations().add(new Education("명지전문대", "사회체육학과 졸업", "2013.02 – 2015.02", "", portfolio));

        // 자격증 (List -> Set 으로 변경)
        portfolio.getCertifications().addAll(Set.of( // List.of() -> Set.of()
            new Certification("AWS Certified Cloud Practitioner", "Amazon Web Services", "2025.07", portfolio),
            new Certification("리눅스마스터 2급", "한국정보통신진흥협회", "2025.03", portfolio),
            new Certification("정보처리기사", "한국산업인력공단", "2025.08 필기합격, 11월 실기 예정", portfolio)
        ));

        // 기타 경력 (List -> Set 으로 변경)
        portfolio.getCareers().addAll(Set.of( // List.of() -> Set.of()
            new Career("(주) 청솔기획", "사원", "2024.05 ~ 2025.06", "보건소 B2B 영업, 엑셀 데이터 관리 및 매출 분석", portfolio),
            new Career("(주) 에이블짐", "트레이너 → 팀장", "2021.04 ~ 2024.02", "7명 팀 운영, 전국 8개 지점 중 개인 매출 3위 달성", portfolio)
        ));

        // 3. Repository를 통해 모든 데이터 저장
        portfolioDataRepository.save(portfolio);
    }
}