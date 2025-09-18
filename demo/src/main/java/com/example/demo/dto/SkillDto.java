package com.example.demo.dto;

import com.example.demo.domain.Skill;
import lombok.Getter;

@Getter
public class SkillDto {
    private final Long id;
    private final String category;
    private final String name;

    public SkillDto(Skill skill) {
        this.id = skill.getId();
        this.category = skill.getCategory();
        this.name = skill.getName();
    }
}