package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institution; // 기관명
    private String course;      // 과정명 또는 전공
    private String period;      // 기간
    
    @Column(length = 1000)
    private String description; // 핵심 학습 영역

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_data_id")
    private PortfolioData portfolioData;

     public Education(String institution, String course, String period, String description, PortfolioData portfolioData) {
        this.institution = institution;
        this.course = course;
        this.period = period;
        this.description = description;
        this.portfolioData = portfolioData;
    }

}

