package com.audora.app.model;

import com.audora.app.enumpackage.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    private String id = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan name;

    private String Price;
    private String Duration;

    @OneToMany(mappedBy = "plan")
    private List<Subscriptions> subscriptionsList;

    private List<String> features;
    private boolean featured; // for "POPULAR"
    private boolean currentPlan;
    private String icon;

}
