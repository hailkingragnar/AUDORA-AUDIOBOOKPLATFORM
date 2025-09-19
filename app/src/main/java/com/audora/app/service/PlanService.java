package com.audora.app.service;

import com.audora.app.model.Plan;
import com.audora.app.repo.PlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepo planRepo;

    public List<Plan> getAllPlans() {
        return planRepo.findAll();
    }
}
