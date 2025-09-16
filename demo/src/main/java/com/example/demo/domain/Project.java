package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String period;
    private String description;

    @Column(length = 2000)
    private String problem;

    @Column(length = 2000)
    private String action;

    @Column(length = 2000)
    private String result;

    private String techStack;
    private String projectUrl;
    private String teamInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_data_id")
    private PortfolioData portfolioData;

    public Project(String name, String period, String description, String problem, String action, String result, String techStack, String projectUrl, String teamInfo, PortfolioData portfolioData) {
        this.name = name;
        this.period = period;
        this.description = description;
        this.problem = problem;
        this.action = action;
        this.result = result;
        this.techStack = techStack;
        this.projectUrl = projectUrl;
        this.teamInfo = teamInfo;
        this.portfolioData = portfolioData;
    }

}
