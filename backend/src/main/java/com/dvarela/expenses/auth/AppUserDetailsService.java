package com.dvarela.expenses.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvarela.expenses.household.MembershipRepository;
import com.dvarela.expenses.user.AppUser;
import com.dvarela.expenses.user.AppUserRepository;

@Service
class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;
    private final MembershipRepository membershipRepository;

    AppUserDetailsService(AppUserRepository userRepository, MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Long householdId = membershipRepository.findByUserIdOrderByJoinedAtAsc(user.getId()).stream()
                .findFirst()
                .map(membership -> membership.getId().getHouseholdId())
                .orElseThrow(() -> new UsernameNotFoundException("User without household"));

        return new AppUserPrincipal(user.getId(), householdId, user.getEmail(),
                user.getPasswordHash(), user.getDisplayName());
    }
}