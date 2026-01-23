package com.learning.springboot.bootsecurity.part01_security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
class AdminOnlyService {

    @PreAuthorize("hasRole('ADMIN')")
    String adminOnlyAction() {
        return "admin_action_done";
    }
}

