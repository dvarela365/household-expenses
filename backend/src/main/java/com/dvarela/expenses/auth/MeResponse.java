package com.dvarela.expenses.auth;

public record MeResponse(Long id, String email, String displayName, Long householdId) {

    static MeResponse from(AppUserPrincipal principal) {
        return new MeResponse(principal.getUserId(), principal.getUsername(),
                principal.getDisplayName(), principal.getHouseholdId());
    }
}