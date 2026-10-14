package com.dvarela.expenses.household;

import java.time.Instant;

import com.dvarela.expenses.user.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "household_member")
public class Membership {

    @EmbeddedId
    private MembershipId id;

    @MapsId("householdId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "household_id")
    private Household household;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MembershipRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    protected Membership() {
        // required by JPA
    }

    /** Both household and user must already be persisted (they need their ids). */
    public Membership(Household household, AppUser user, MembershipRole role) {
        this.id = new MembershipId(household.getId(), user.getId());
        this.household = household;
        this.user = user;
        this.role = role;
        this.joinedAt = Instant.now();
    }

    public MembershipId getId() { return id; }
    public Household getHousehold() { return household; }
    public AppUser getUser() { return user; }
    public MembershipRole getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }
}