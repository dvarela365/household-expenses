package com.dvarela.expenses.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvarela.expenses.category.CategoryRepository;
import com.dvarela.expenses.category.DefaultCategories;
import com.dvarela.expenses.household.Household;
import com.dvarela.expenses.household.HouseholdRepository;
import com.dvarela.expenses.household.Membership;
import com.dvarela.expenses.household.MembershipRepository;
import com.dvarela.expenses.household.MembershipRole;
import com.dvarela.expenses.user.AppUser;
import com.dvarela.expenses.user.AppUserRepository;

@Service
public class RegistrationService {

    private final AppUserRepository userRepository;
    private final HouseholdRepository householdRepository;
    private final MembershipRepository membershipRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(AppUserRepository userRepository,
                               HouseholdRepository householdRepository,
                               MembershipRepository membershipRepository,
                               CategoryRepository categoryRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.householdRepository = householdRepository;
        this.membershipRepository = membershipRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrationResponse register(RegisterRequest request) {
        String email = request.email().trim();
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new EmailAlreadyRegisteredException();
        }

        AppUser user = userRepository.save(new AppUser(
                email, passwordEncoder.encode(request.password()), request.displayName().trim()));
        Household household = householdRepository.save(
                new Household("Hogar de " + user.getDisplayName()));
        membershipRepository.save(new Membership(household, user, MembershipRole.OWNER));
        categoryRepository.saveAll(DefaultCategories.forHousehold(household.getId()));

        return new RegistrationResponse(user.getId(), user.getEmail(), user.getDisplayName());
    }
}