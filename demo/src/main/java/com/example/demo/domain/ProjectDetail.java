package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProjectDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl; // 상세 이미지 경로

    @Column(length = 2000)
    private String description; // 이미지에 대한 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 이 상세 정보가 속한 프로젝트

    // 생성자
    public ProjectDetail(String imageUrl, String description, Project project) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.project = project;
    }
}