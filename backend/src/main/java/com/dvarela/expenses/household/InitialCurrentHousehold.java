package com.dvarela.expenses.household;

import org.springframework.stereotype.Component;

/**
 * Temporary: every request operates on the initial household (id 1, created by V6).
 * Replaced in step 1.5 by an implementation based on the authenticated user.
 */
@Component
class InitialCurrentHousehold implements CurrentHousehold {

    private static final Long INITIAL_HOUSEHOLD_ID = 1L;

    @Override
    public Long id() {
        return INITIAL_HOUSEHOLD_ID;
    }
}