package com.dvarela.expenses.household;

import java.util.Optional;

import org.springframework.data.repository.Repository;

public interface MembershipRepository extends Repository<Membership, MembershipId> {

    <S extends Membership> S save(S membership);

    Optional<Membership> findById(MembershipId id);

    void flush();
}