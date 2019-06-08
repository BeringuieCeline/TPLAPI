package org.jeallasia.tplapi;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class ParkingSlot<T> implements Cloneable{

    private final String id;
    private final PredicateWithAlt<T> policy;
    private T car;
    private LocalDateTime incomingDateTime;
    private Boolean usingAlternative;
    private LocalDateTime outgoingDateTime;

    public ParkingSlot(final String id, final PredicateWithAlt<T> policy, T car, LocalDateTime incomingDateTime, Boolean usingAlternative, LocalDateTime outgoingDateTime) {
        Objects.requireNonNull(policy, "You have to specify at least one parking slot policy !");
        this.id = id;
        this.policy = policy;
        this.car = car;
        this.incomingDateTime = incomingDateTime;
        this.usingAlternative = usingAlternative;
        this.outgoingDateTime = outgoingDateTime;
    }

    ParkingSlot(final String id, final PredicateWithAlt<T> policy) {
        Objects.requireNonNull(policy, "You have to specify at least one parking slot policy !");
        this.id = id;
        this.policy = policy;
    }

    boolean isFree() {
        return car == null;
    }
    /**
     * @param car the car you want to test
     * @return {@code true} if the slot is made for this kind of car
     */
    boolean test(T car){
        return policy.test(car);
    }

    /**
     * @param car the car you want to test
     * @return {@code true} if the slot is a valid alternative for this kind of car
     */
    boolean testAlt(T car){
        return policy.testAlt(car);
    }

    /**
     * @param car the car you want to test
     * @return {@code true} if the slot if made for this kind of car or is a valid alternative for this kind of car
     */
    boolean testCompatible(T car){
        return policy.testCompatible(car);
    }

    public Duration computeDuration() {
        if(incomingDateTime==null) return Duration.ZERO;
        LocalDateTime outgoingDateTime = this.outgoingDateTime;
        if(outgoingDateTime==null) outgoingDateTime = LocalDateTime.now();
        return Duration.between(incomingDateTime, outgoingDateTime);
    }

    ParkingSlot<T> allocate(T car, LocalDateTime incomingDateTime, boolean usingAlternative){
        Objects.requireNonNull(incomingDateTime);
        Objects.requireNonNull(car);
        this.car = car;
        this.incomingDateTime = incomingDateTime;
        this.usingAlternative = usingAlternative;
        return new ParkingSlot<>(id, policy, car, incomingDateTime, usingAlternative, outgoingDateTime);
    }

    ParkingSlot<T> free(LocalDateTime outgoingDateTime){
        Objects.requireNonNull(outgoingDateTime);
        if (incomingDateTime.isAfter(outgoingDateTime)) {
            throw new IllegalArgumentException("Outgoing date should be after incoming date !");
        }
        ParkingSlot<T> result = new ParkingSlot<>(id, policy, car, incomingDateTime, usingAlternative, outgoingDateTime);
        this.car = null;
        this.incomingDateTime = null;
        this.usingAlternative = null;
        return result;
    }

    public String getId() { return id; }
    public PredicateWithAlt<T> getPolicy() { return policy; }
    public T getCar() { return car; }
    public LocalDateTime getIncomingDateTime() { return incomingDateTime; }
    public Boolean getUsingAlternative() { return usingAlternative; }
    public LocalDateTime getOutgoingDateTime() { return outgoingDateTime; }

}