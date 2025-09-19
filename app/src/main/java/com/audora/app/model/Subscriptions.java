package com.audora.app.model;

import com.audora.app.enumpackage.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscriptions {
    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "userId" , nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "planId" , nullable = false)
    private Plan plan;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean autoRenew;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;


}
