package org.jeallasia.tplapi;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Parking slot in the parking
 * @param <T> the type of the car (The car class you are using)
 */
public final class ParkingSlot<T> implements Cloneable {

    private final String id;
    private final PredicateWithAlt<T> policy;
    private T car;
    private LocalDateTime incomingDateTime;
    private Boolean usingAlternative;
    private LocalDateTime outgoingDateTime;

    /**
     * Constructor used to make a copy of current ParkingSlot to allow reporting
     * @param id the indicative id of the ParkingSlot
     * @param policy the allocation policy
     * @param car the car in the slot
     * @param incomingDateTime the incoming date time of that car
     * @param usingAlternative true if the car was selected using {@link PredicateWithAlt#testAlt(Object)}
     * @param outgoingDateTime the outgoing date time of the car
     */
    ParkingSlot(final String id, final PredicateWithAlt<T> policy, T car, LocalDateTime incomingDateTime, Boolean usingAlternative, LocalDateTime outgoingDateTime) {
        Objects.requireNonNull(policy, "You have to specify at least one parking slot policy !");
        this.id = id;
        this.policy = policy;
        this.car = car;
        this.incomingDateTime = incomingDateTime;
        this.usingAlternative = usingAlternative;
        this.outgoingDateTime = outgoingDateTime;
    }

    /**
     * Constructor used in {@link Parking<T>}
     * @param id the indicative id of the ParkingSlot
     * @param policy the allocation policy
     */
    ParkingSlot(final String id, final PredicateWithAlt<T> policy) {
        Objects.requireNonNull(policy, "You have to specify at least one parking slot policy !");
        this.id = id;
        this.policy = policy;
    }

    /**
     * @return {@code true} if the slot id empty otherwise {@code false}
     */
    boolean isFree() {
        return car == null;
    }

    /**
     * @param car the car you want to test
     * @return {@code true} if the slot is made for this kind of car
     */
    boolean test(T car) {
        return policy.test(car);
    }

    /**
     * @param car the car you want to test
     * @return {@code true} if the slot is a valid alternative for this kind of car
     */
    boolean testAlt(T car) {
        return policy.testAlt(car);
    }

    /**
     * @param car the car you want to test
     * @return {@code true} if the slot if made for this kind of car or is a valid alternative for this kind of car
     */
    boolean testCompatible(T car) {
        return policy.testCompatible(car);
    }

    /**
     * Compute {@link Duration} based on {@link ParkingSlot#incomingDateTime} and {@link ParkingSlot#outgoingDateTime}
     * @return {@link Duration}
     */
    public Duration computeDuration() {
        if (incomingDateTime == null) return Duration.ZERO;
        LocalDateTime outgoingDateTime = this.outgoingDateTime;
        if (outgoingDateTime == null) outgoingDateTime = LocalDateTime.now();
        return Duration.between(incomingDateTime, outgoingDateTime);
    }

    /**
     * Used by {@link Parking#checkIn(Object)}.
     * Basically allocate the slot to a car (providing incoming date time
     * and if the slot was selected as an alternative or not)
     *
     * @param car the car you want to put in
     * @param incomingDateTime the arrival date time of the car
     * @param usingAlternative {@code true} if the slot if made for this kind of car or is a valid alternative for this kind of car
     * @return a copy of current {@link ParkingSlot} used for reporting
     */
    ParkingSlot<T> allocate(T car, LocalDateTime incomingDateTime, boolean usingAlternative) {
        Objects.requireNonNull(incomingDateTime);
        Objects.requireNonNull(car);
        this.car = car;
        this.incomingDateTime = incomingDateTime;
        this.usingAlternative = usingAlternative;
        return new ParkingSlot<>(id, policy, car, incomingDateTime, usingAlternative, outgoingDateTime);
    }

    /**
     * Used by {@link Parking#checkOut(Object)}.
     * Basically remove the car (providing outgoing date time) from the slot
     *
     * @param outgoingDateTime the departure date time of the car
     * @return a copy of current {@link ParkingSlot} used for reporting
     * @throws IllegalArgumentException if outgoingDateTime is after incomingDateTime
     */
    ParkingSlot<T> free(LocalDateTime outgoingDateTime) {
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

    /**
     * @return the id of the parking slot
     */
    public String getId() {
        return id;
    }

    /**
     * @return the allocation policy
     */
    public PredicateWithAlt<T> getPolicy() {
        return policy;
    }

    /**
     * @return the car in
     */
    public T getCar() {
        return car;
    }

    /**
     * @return the car incoming date time
     */
    public LocalDateTime getIncomingDateTime() {
        return incomingDateTime;
    }

    /**
     * @return {@code true} if the slot was selected as an alternative for this car
     */
    public Boolean getUsingAlternative() {
        return usingAlternative;
    }

    /**
     * @return the car outgoing date time
     */
    public LocalDateTime getOutgoingDateTime() {
        return outgoingDateTime;
    }

}