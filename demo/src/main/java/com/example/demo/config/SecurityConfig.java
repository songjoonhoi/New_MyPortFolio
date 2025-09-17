package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화를 위한 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 간단한 인메모리 방식의 사용자 인증 설정 (추후 DB 연동으로 변경 가능)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("admin")
                // 실제 운영 시에는 application.yml 등에서 비밀번호를 관리해야 합니다.
                .password(passwordEncoder().encode("admin1234")) 
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // HTTP 요청에 대한 보안 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // CSS, JS, H2 콘솔 등은 모두 접근 허용
                .requestMatchers("/css/**", "/js/**", "/h2-console/**").permitAll()
                // 메인 페이지("/")는 모두 접근 허용
                .requestMatchers("/").permitAll()
                // "/admin/**" 경로는 ADMIN 역할을 가진 사용자만 접근 가능
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 나머지 모든 요청은 인증된 사용자만 접근 가능
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // 커스텀 로그인 페이지 경로 설정
                .loginPage("/login")
                // 로그인 성공 시 이동할 기본 경로
                .defaultSuccessUrl("/admin", true)
                // 로그인 페이지는 모두 접근 허용
                .permitAll()
            )
            .logout(logout -> logout
                // 로그아웃 처리 URL
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                // 로그아웃 성공 시 이동할 경로
                .logoutSuccessUrl("/")
                // 세션 무효화
                .invalidateHttpSession(true)
            )
            // H2 콘솔 사용을 위한 설정
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
