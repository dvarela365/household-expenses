package com.dvarela.expenses.household;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class MembershipId implements Serializable {

    private Long householdId;
    private Long userId;

    protected MembershipId() {
        // required by JPA
    }

    public MembershipId(Long householdId, Long userId) {
        this.householdId = householdId;
        this.userId = userId;
    }

    public Long getHouseholdId() { return householdId; }
    public Long getUserId() { return userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MembershipId other)) return false;
        return Objects.equals(householdId, other.householdId)
                && Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(householdId, userId);
    }
}