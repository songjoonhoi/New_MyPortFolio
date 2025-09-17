package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String issuer;
    private String acquisitionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_data_id")
    private PortfolioData portfolioData;

    public Certification(String name, String issuer, String acquisitionDate, PortfolioData portfolioData) {
        this.name = name;
        this.issuer = issuer;
        this.acquisitionDate = acquisitionDate;
        this.portfolioData = portfolioData;
    }
}
