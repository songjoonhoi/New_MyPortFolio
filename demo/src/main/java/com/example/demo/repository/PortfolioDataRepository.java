package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.domain.PortfolioData;

import java.util.Optional;

public interface PortfolioDataRepository extends JpaRepository<PortfolioData, Long>  {

    // 포트폴리오 데이터를 불러올 때 모든 연관 데이터를 한번에 가져오는 JPQL 쿼리
      @Query("SELECT DISTINCT p FROM PortfolioData p " +
           "LEFT JOIN FETCH p.skills " +
           "LEFT JOIN FETCH p.projects " +
           "LEFT JOIN FETCH p.educations " +
           "LEFT JOIN FETCH p.careers " +
           "LEFT JOIN FETCH p.certifications " +
           "WHERE p.id = :id")
    Optional<PortfolioData> findPortfolioWithDetailsById(Long id);
}
