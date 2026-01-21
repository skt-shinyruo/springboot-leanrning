package com.learning.springboot.bootsecurity.part01_security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
class SelfInvocationPitfallService {

    String outerCallsAdminOnly() {
        return "outer:" + adminOnly();
    }

    @PreAuthorize("hasRole('ADMIN')")
    String adminOnly() {
        return "admin_only";
    }
}

