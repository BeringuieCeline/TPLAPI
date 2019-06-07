package io.github.jeallasia.tplapi;

import io.github.jeallasia.tplapi.exception.ParkingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Parking<T> {

    private final List<ParkingSlot<T>> slots = new ArrayList<>();
    private PricingPolicy<T> pricingPolicy;

    /**
     * Constructor, used from the Builder.
     */
    Parking() {}

    /**
     * @param <K> the car class you want to use
     * @return the builder
     */
    public static <K> ParkingBuilder<K> builder() {
        return new ParkingBuilder<>();
    }

    void setPricingPolicy(PricingPolicy<T> pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    /**
     * Add a new slot, providing id and policy
     *
     * @param id the id for the parking slot
     * @param policy the policy that will be used for that slot
     */
    void addSlot(String id, PredicateWithAlt<T> policy) {
        slots.add(new ParkingSlot<>(id, policy));
    }

    /**
     * Get the size of parking (total number of slots)
     *
     * @return the size of the parking.
     */
    public long getSize() {
        return slots.size();
    }

    /**
     * @return stream on free {@link ParkingSlot}
     */
    private Stream<ParkingSlot<T>> getAllAvailable() {
        return slots.stream().filter(ParkingSlot::isFree);
    }

    /**
     * Will return a stream containing all slots that are compatible see {@link PredicateWithAlt} for a given car
     *
     * @param car the car you want to test
     * @return stream on free and compatible {@link ParkingSlot#testCompatible(T)}
     */
    private Stream<ParkingSlot<T>> getAvailableCompatibleFor(T car) {
        return getAllAvailable().filter(s -> s.testCompatible(car));
    }

    /**
     * Will return a stream containing all slots that are a valid alternative see {@link PredicateWithAlt} for a given car
     *
     * @param car the car you want to test
     * @return stream {@link ParkingSlot} on free and alternative slot {@link ParkingSlot#getPolicy()}
     */
    private Stream<ParkingSlot<T>> getAvailableAltFor(T car) {
        return getAllAvailable().filter(s -> s.testAlt(car));
    }

    /**
     * Will return a stream containing all slots that are a valid alternative see {@link PredicateWithAlt} for a given car
     *
     * @param car the car you want to test
     * @return stream on free and preferred slot {@link ParkingSlot}
     */
    private Stream<ParkingSlot<T>> getAvailableFor(T car) {
        return getAllAvailable().filter(s -> s.test(car));
    }
    /**
     * Will test all
     *
     * @param car The car you want to test
     *
     * @return the size of the parking.
     */
    public long getSizeCompatibleFor(T car) {
        return slots.stream().filter(s -> s.testCompatible(car)).count();
    }

    public long getSizeAltFor(T car) {
        return slots.stream().filter(s -> s.testAlt(car)).count();
    }

    public long getSizeFor(T car) {
        return slots.stream().filter(s -> s.test(car)).count();
    }

    public long getAvailableSizeCompatibleFor(T car) {
        return getAvailableCompatibleFor(car).count();
    }

    public long getAvailableSizeAltFor(T car) {
        return getAvailableAltFor(car).count();
    }

    public long getAvailableSizeFor(T car) {
        return getAvailableFor(car).count();
    }

    public synchronized CheckInResult<T> checkIn(T car, LocalDateTime incomingDateTime) {
        CheckInResult<T> result = new CheckInResult<>();
        getAvailableFor(car).findFirst().ifPresentOrElse(
                p -> result.setSlot(p.allocate(car, incomingDateTime, false)),
                () -> getAvailableAltFor(car).findFirst().ifPresent(
                        alt -> result.setSlot(alt.allocate(car, incomingDateTime, true))
                )
        );
        return result;
    }

    public synchronized CheckInResult<T> checkIn(T car) {
        return checkIn(car, LocalDateTime.now());
    }

    public synchronized CheckOutResult<T> checkOut(T car, LocalDateTime outgoingDateTime) {
        ParkingSlot<T> usage = slots.stream().filter(s -> s.getCar() == car).findFirst().orElseThrow(
                () -> new ParkingException("Car " + car + " not found !")).free(outgoingDateTime);
        return new CheckOutResult<>(usage, pricingPolicy.computePrice(usage));
    }

    public synchronized CheckOutResult<T> checkOut(T car) {
        return checkOut(car, LocalDateTime.now());
    }

    void check() {
        if (this.pricingPolicy == null) {
            throw new ParkingException("You have to specify a Pricing strategy using setPricingPolicy(...) !");
        }
        if (this.slots.isEmpty()) {
            throw new ParkingException("Your parking does not contains any parking slots, add some using addSlot(...) !");
        }
    }

}
