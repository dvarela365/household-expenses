package com.dvarela.expenses.household;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.dvarela.expenses.TestcontainersConfiguration;
import com.dvarela.expenses.user.AppUser;
import com.dvarela.expenses.user.AppUserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class MembershipRepositoryTest {

    @Autowired private AppUserRepository userRepository;
    @Autowired private HouseholdRepository householdRepository;
    @Autowired private MembershipRepository membershipRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void save_persistsMembershipWithCompositeKey() {
        AppUser user = userRepository.save(new AppUser("ana@example.com", "hash", "Ana"));
        Household household = householdRepository.save(new Household("Casa de Ana"));
        membershipRepository.save(new Membership(household, user, MembershipRole.OWNER));
        membershipRepository.flush();
        entityManager.clear();

        Membership found = membershipRepository
                .findById(new MembershipId(household.getId(), user.getId()))
                .orElseThrow();

        assertThat(found.getRole()).isEqualTo(MembershipRole.OWNER);
        assertThat(found.getUser().getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    void findByEmailIgnoreCase_matchesRegardlessOfCase() {
        userRepository.save(new AppUser("Diego@Example.com", "hash", "Diego"));

        assertThat(userRepository.findByEmailIgnoreCase("diego@example.com")).isPresent();
    }

    @Test
    void save_rejectsADuplicateEmailWithDifferentCase() {
        userRepository.save(new AppUser("bruno@example.com", "hash", "Bruno"));

        assertThatThrownBy(() -> {
            userRepository.save(new AppUser("BRUNO@example.com", "hash", "Otro Bruno"));
            userRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}