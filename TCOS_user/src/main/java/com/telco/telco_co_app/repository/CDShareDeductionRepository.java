package com.telco.telco_co_app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.telco_co_app.model.CDShareDeductions;

import jakarta.transaction.Transactional;

public interface CDShareDeductionRepository extends JpaRepository<CDShareDeductions, Integer> {
	
	List<CDShareDeductions> findByUserId(String userId);
	
	CDShareDeductions findById(int id);
	
	@Modifying
    @Transactional
    @Query(value="update cd_share_deductions set cd_deduction = :amount where user_id = :id ORDER BY created_at DESC",nativeQuery = true)
	int updateCdDeductionByUserId(@Param(value = "id") String userId, @Param(value = "amount") double amount);
	
	@Query(value = "SELECT * FROM cd_share_deductions WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<CDShareDeductions> findLatestRecordByUserId(@Param("userId") String userId, Pageable pageable);

}
