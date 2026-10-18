package com.dvarela.expenses.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.dvarela.expenses.household.Household;
import com.dvarela.expenses.household.Membership;
import com.dvarela.expenses.household.MembershipRepository;
import com.dvarela.expenses.household.MembershipRole;
import com.dvarela.expenses.user.AppUser;
import com.dvarela.expenses.user.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock private AppUserRepository userRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks
    private AppUserDetailsService service;

    @Test
    void loadUserByUsername_returnsThePrincipalWithItsHousehold() {
        AppUser ana = new AppUser("ana@example.com", "{bcrypt}hash", "Ana");
        ReflectionTestUtils.setField(ana, "id", 7L);
        Household household = new Household("Hogar de Ana");
        ReflectionTestUtils.setField(household, "id", 3L);
        when(userRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.of(ana));
        when(membershipRepository.findByUserIdOrderByJoinedAtAsc(7L))
                .thenReturn(List.of(new Membership(household, ana, MembershipRole.OWNER)));

        AppUserPrincipal principal =
                (AppUserPrincipal) service.loadUserByUsername("ana@example.com");

        assertThat(principal.getUserId()).isEqualTo(7L);
        assertThat(principal.getHouseholdId()).isEqualTo(3L);
        assertThat(principal.getPassword()).isEqualTo("{bcrypt}hash");
    }

    @Test
    void loadUserByUsername_failsForAnUnknownEmail() {
        when(userRepository.findByEmailIgnoreCase("nadie@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nadie@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}