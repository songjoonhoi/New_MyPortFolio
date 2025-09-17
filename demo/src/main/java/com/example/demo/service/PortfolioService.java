package com.example.demo.service;

import com.example.demo.domain.PortfolioData;
import com.example.demo.dto.PortfolioResponse;
import com.example.demo.dto.PortfolioUpdateRequest;
import com.example.demo.repository.CareerRepository;
import com.example.demo.repository.PortfolioDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.domain.Career;
import com.example.demo.domain.PortfolioData;
import com.example.demo.dto.CareerCreateRequest;
import com.example.demo.dto.CareerDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {
    
    private final PortfolioDataRepository portfolioDataRepository;
    private final CareerRepository careerRepository;

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

    // ▼▼▼ [새로 추가] ID로 특정 경력 항목을 찾는 메소드 ▼▼▼
    public CareerDto findCareerById(Long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new IllegalArgumentException("경력 정보를 찾을 수 없습니다. id=" + careerId));
        return new CareerDto(career); // Entity를 DTO로 변환하여 반환
    }

    // ▼▼▼ [새로 추가] 경력(Career) 항목을 수정하는 메소드 ▼▼▼
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

}
