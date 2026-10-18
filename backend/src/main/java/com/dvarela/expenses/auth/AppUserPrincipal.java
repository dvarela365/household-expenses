package com.dvarela.expenses.auth;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserPrincipal implements UserDetails, CredentialsContainer, Serializable {

    private final Long userId;
    private final Long householdId;
    private final String email;
    private final String displayName;
    private String passwordHash;

    public AppUserPrincipal(Long userId, Long householdId, String email,
                            String passwordHash, String displayName) {
        this.userId = userId;
        this.householdId = householdId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }

    public Long getUserId() { return userId; }
    public Long getHouseholdId() { return householdId; }
    public String getDisplayName() { return displayName; }

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    /** Called by Spring Security after authentication: the hash does not stay in the session. */
    @Override
    public void eraseCredentials() {
        this.passwordHash = null;
    }
}