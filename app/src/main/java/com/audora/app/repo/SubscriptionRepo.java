package com.audora.app.repo;

import com.audora.app.model.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepo extends JpaRepository<Subscriptions,String> {
}
