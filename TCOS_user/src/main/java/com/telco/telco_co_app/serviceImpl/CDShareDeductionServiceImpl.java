package com.telco.telco_co_app.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.model.CDShareDeductions;
import com.telco.telco_co_app.repository.CDShareDeductionRepository;
import com.telco.telco_co_app.service.CDShareDeductionService;

@Service
public class CDShareDeductionServiceImpl implements CDShareDeductionService {
	
	@Autowired
	private CDShareDeductionRepository cdShareDeductionRepository;

	@Override
	public List<CDShareDeductions> findByUserId(String userId) {
		// TODO Auto-generated method stub
		return cdShareDeductionRepository.findByUserId(userId);
	}

	@Override
	public int updateCdAmount(String userId, double amount) {
		// TODO Auto-generated method stub
		return cdShareDeductionRepository.updateCdDeductionByUserId(userId, amount);
	}

	@Override
	public CDShareDeductions findById(int id) {
		// TODO Auto-generated method stub
		return cdShareDeductionRepository.findById(id);
	}

	@Override
	public CDShareDeductions findLatestRecordByUserId(String userId) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(0, 1); // Get only the first (latest) record
        List<CDShareDeductions> latestDeductions = cdShareDeductionRepository.findLatestRecordByUserId(userId, pageable);
        
        if (!latestDeductions.isEmpty()) {
            return latestDeductions.get(0);  // Return the first element (latest record)
        } else {
            return null;  // Handle the case where no record is found
        }
	}

}
