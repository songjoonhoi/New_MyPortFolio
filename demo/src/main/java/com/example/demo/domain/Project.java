// src/main/java/com/example/demo/domain/Project.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String period;

    @Column(length = 1000)
    private String description; // 한 줄 요약 설명

    private String techStack;
    private String projectUrl;
    private String teamInfo;
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_data_id")
    private PortfolioData portfolioData;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ProjectDetail> details = new HashSet<>();

    // ▼▼▼ 이 생성자가 올바른 모양입니다 ▼▼▼
    public Project(String name, String period, String description, String techStack, String projectUrl, String teamInfo, PortfolioData portfolioData) {
        this.name = name;
        this.period = period;
        this.description = description;
        this.techStack = techStack;
        this.projectUrl = projectUrl;
        this.teamInfo = teamInfo;
        this.portfolioData = portfolioData;
    }
}