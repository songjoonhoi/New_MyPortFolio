package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.HashSet; // HashSet import
import java.util.Set;    // Set import

@Entity
@Getter
@NoArgsConstructor
public class PortfolioData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 기본 정보 ---
    private String name;
    private String jobTitle;
    @Column(length = 1000)
    private String philosophy;
    private String birth;
    private String address;
    private String email;
    private String phone;
    private String githubUrl;

    // --- 연관 관계 (List -> Set 으로 변경) ---
    @OneToMany(mappedBy = "portfolioData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "portfolioData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "portfolioData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Education> educations = new HashSet<>();

    @OneToMany(mappedBy = "portfolioData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Career> careers = new HashSet<>();

    @OneToMany(mappedBy = "portfolioData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certification> certifications = new HashSet<>();

    // --- DataInitializer를 위한 생성자 ---
    public PortfolioData(String name, String jobTitle, String philosophy, String birth, String address, String email, String phone, String githubUrl) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.philosophy = philosophy;
        this.birth = birth;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.githubUrl = githubUrl;
    }
}
