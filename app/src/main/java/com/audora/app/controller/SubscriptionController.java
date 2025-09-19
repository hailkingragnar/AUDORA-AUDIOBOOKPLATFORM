package com.audora.app.controller;

import com.audora.app.model.Plan;
import com.audora.app.model.Users;
import com.audora.app.service.PlanService;
import com.audora.app.service.UsersService;
import com.audora.app.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SubscriptionController {


    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private PlanService planService;

    @Autowired
    private UsersService usersService;

    @GetMapping("/user/subscription")
    public String subscriptionDetails(Model model){
        String userName = SecurityUtils.getUserEmail();
        Users user1 = usersService.getUser(userName);
        List<Plan> plans = planService.getAllPlans();
        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("plans",plans);
        model.addAttribute("users",user1);
        return "user/subscription";
    }
}
