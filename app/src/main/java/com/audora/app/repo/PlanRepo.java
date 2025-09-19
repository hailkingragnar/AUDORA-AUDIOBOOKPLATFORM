package com.audora.app.repo;

import com.audora.app.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepo extends JpaRepository<Plan,String> {
}
