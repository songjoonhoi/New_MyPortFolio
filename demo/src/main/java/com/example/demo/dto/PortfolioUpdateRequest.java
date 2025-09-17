package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioUpdateRequest {
    
    private String name;
    private String jobTitle;
   private String philosophy;
    private String birth;
    private String address;
    private String email;
    private String phone;
    private String githubUrl;
}
