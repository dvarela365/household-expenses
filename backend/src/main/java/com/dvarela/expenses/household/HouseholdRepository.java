package com.dvarela.expenses.household;

import org.springframework.data.repository.Repository;

public interface HouseholdRepository extends Repository<Household, Long> {

    <S extends Household> S save(S household);
}