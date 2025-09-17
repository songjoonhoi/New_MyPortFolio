package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String position;
    private String period;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_data_id")
    private PortfolioData portfolioData;

    public Career(String company, String position, String period, String description, PortfolioData portfolioData) {
        this.company = company;
        this.position = position;
        this.period = period;
        this.description = description;
        this.portfolioData = portfolioData;
    }
}