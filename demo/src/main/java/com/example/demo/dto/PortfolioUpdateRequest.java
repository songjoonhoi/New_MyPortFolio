package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

@Getter
@Setter
public class PortfolioUpdateRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private String name;
    
    @NotBlank(message = "직무는 필수입니다")
    @Size(max = 100, message = "직무는 100자를 초과할 수 없습니다")
    private String jobTitle;
    
    @Size(max = 1000, message = "직무 철학은 1000자를 초과할 수 없습니다")
    private String philosophy;
    
    @Size(max = 50, message = "생년월일은 50자를 초과할 수 없습니다")
    private String birth;
    
    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
    private String address;
    
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;
    
    @Size(max = 20, message = "연락처는 20자를 초과할 수 없습니다")
    private String phone;
    
    @Size(max = 200, message = "Github URL은 200자를 초과할 수 없습니다")
    private String githubUrl;
}