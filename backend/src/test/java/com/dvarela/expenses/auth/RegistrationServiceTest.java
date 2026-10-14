package com.dvarela.expenses.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dvarela.expenses.category.Category;
import com.dvarela.expenses.category.CategoryRepository;
import com.dvarela.expenses.household.Household;
import com.dvarela.expenses.household.HouseholdRepository;
import com.dvarela.expenses.household.Membership;
import com.dvarela.expenses.household.MembershipRepository;
import com.dvarela.expenses.household.MembershipRole;
import com.dvarela.expenses.user.AppUser;
import com.dvarela.expenses.user.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock private AppUserRepository userRepository;
    @Mock private HouseholdRepository householdRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    @SuppressWarnings("unchecked")
    void register_createsUserHouseholdOwnerMembershipAndDefaultCategories() {
        when(userRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secreto123")).thenReturn("{bcrypt}hash");
        when(userRepository.save(any(AppUser.class))).thenAnswer(i -> i.getArgument(0));
        when(householdRepository.save(any(Household.class))).thenAnswer(i -> i.getArgument(0));

        RegistrationResponse response = registrationService.register(
                new RegisterRequest("  ana@example.com ", "secreto123", " Ana "));

        ArgumentCaptor<AppUser> user = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(user.capture());
        assertThat(user.getValue().getPasswordHash()).isEqualTo("{bcrypt}hash");
        assertThat(user.getValue().getEmail()).isEqualTo("ana@example.com");

        ArgumentCaptor<Membership> membership = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(membership.capture());
        assertThat(membership.getValue().getRole()).isEqualTo(MembershipRole.OWNER);

        ArgumentCaptor<List<Category>> categories = ArgumentCaptor.forClass(List.class);
        verify(categoryRepository).saveAll(categories.capture());
        assertThat(categories.getValue()).hasSize(10);

        assertThat(response.displayName()).isEqualTo("Ana");
    }

    @Test
    void register_rejectsAnEmailAlreadyRegistered() {
        when(userRepository.findByEmailIgnoreCase("ana@example.com"))
                .thenReturn(Optional.of(new AppUser("ana@example.com", "x", "Ana")));

        assertThatThrownBy(() -> registrationService.register(
                new RegisterRequest("ana@example.com", "secreto123", "Ana")))
                .isInstanceOf(EmailAlreadyRegisteredException.class);
        verify(userRepository, never()).save(any());
    }
}