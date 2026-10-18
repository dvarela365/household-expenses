package com.dvarela.expenses.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dvarela.expenses.household.CurrentHousehold;

@Component
class AuthenticatedCurrentHousehold implements CurrentHousehold {

    @Override
    public Long id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new IllegalStateException("No authenticated user in the security context");
        }
        return principal.getHouseholdId();
    }
}