package com.dvarela.expenses.user;

import java.util.Optional;

import org.springframework.data.repository.Repository;

public interface AppUserRepository extends Repository<AppUser, Long> {

    <S extends AppUser> S save(S user);

    void flush();

    Optional<AppUser> findByEmailIgnoreCase(String email);
}