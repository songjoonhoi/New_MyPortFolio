package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_data_id")
    private PortfolioData portfolioData;

    // --- DataInitializer를 위한 생성자 추가 ---
    public Skill(String category, String name, PortfolioData portfolioData) {
        this.category = category;
        this.name = name;
        this.portfolioData = portfolioData;
    }
}