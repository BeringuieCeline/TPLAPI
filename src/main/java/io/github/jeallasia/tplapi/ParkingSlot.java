package io.github.jeallasia.tplapi;

import java.util.Objects;

public final class ParkingSlot<T> {

    private final String id;
    private final PredicateWithAlt<T> policy;

    ParkingSlot(final String id, final PredicateWithAlt<T> policy) {
        Objects.requireNonNull(policy, "You have to specify at least one parking slot policy !");
        this.id = id;
        this.policy = policy;
    }

    public String getId() { return id; }
    public PredicateWithAlt<T> getPolicy() { return policy; }
}