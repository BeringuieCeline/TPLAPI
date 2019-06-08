package org.jeallasia.tplapi;

import org.jeallasia.tplapi.exception.ParkingException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Parking<T> {

    /**
     * All slots in the parking
     */
    private final List<ParkingSlot<T>> slots = new ArrayList<>();
    /**
     * The pricing policy used during {@link Parking#checkOut(Object)}
     */
    private PricingPolicy<T> pricingPolicy;

    /**
     * Constructor, used from the Builder.
     */
    Parking() {
    }

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
     * @param id     the id for the parking slot
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
     * @return {@link ParkingSlot} stream on free slots.
     */
    private Stream<ParkingSlot<T>> getAllAvailable() {
        return slots.stream().filter(ParkingSlot::isFree);
    }

    /**
     * Return a stream containing all compatible slots see {@link PredicateWithAlt} for this car
     *
     * @param car the car you want to test
     * @return stream on free and compatible {@link ParkingSlot#testCompatible(T)}
     */
    private Stream<ParkingSlot<T>> getAvailableCompatibleFor(T car) {
        return getAllAvailable().filter(s -> s.testCompatible(car));
    }

    /**
     * Return a stream containing all alternative slots see {@link PredicateWithAlt} for this car
     *
     * @param car the car you want to test
     * @return {@link ParkingSlot} stream on free and alternative slot for this car.
     */
    private Stream<ParkingSlot<T>> getAvailableAltFor(T car) {
        return getAllAvailable().filter(s -> s.testAlt(car));
    }

    /**
     * Return a stream containing all preferred slots see {@link PredicateWithAlt} for this car
     *
     * @param car the car you want to test
     * @return stream on free and preferred slot {@link ParkingSlot} for this car.
     */
    private Stream<ParkingSlot<T>> getAvailableFor(T car) {
        return getAllAvailable().filter(s -> s.test(car));
    }

    /**
     * @param car The car you want to test
     * @return number of compatible (preferred or alternative) slots for this car.
     */
    public long getSizeCompatibleFor(T car) {
        return slots.stream().filter(s -> s.testCompatible(car)).count();
    }

    /**
     * @param car The car you want to test
     * @return number of alternative slots for this car.
     */
    public long getSizeAltFor(T car) {
        return slots.stream().filter(s -> s.testAlt(car)).count();
    }

    /**
     * @param car The car you want to test
     * @return number of preferred slots for this car.
     */
    public long getSizeFor(T car) {
        return slots.stream().filter(s -> s.test(car)).count();
    }

    /**
     * @param car The car you want to test
     * @return number of free and compatible (preferred or alternative) slots for this car.
     */
    public long getAvailableSizeCompatibleFor(T car) {
        return getAvailableCompatibleFor(car).count();
    }

    /**
     * @param car The car you want to test
     * @return number of free and alternative slots for this car.
     */
    public long getAvailableSizeAltFor(T car) {
        return getAvailableAltFor(car).count();
    }

    /**
     * @param car The car you want to test
     * @return number of free and preferred slots for this car.
     */
    public long getAvailableSizeFor(T car) {
        return getAvailableFor(car).count();
    }

    /**
     * Allow you to check in a car.
     * It will find the first free available preferred slot, or an alternative one if no preferred slot found.
     * It will then return {@link CheckInResult} containing the corresponding allocated slot (copy)
     *
     * @param car              The car you want to check in.
     * @param incomingDateTime The incoming date and time.
     * @return {@link CheckInResult} containing the corresponding slot (or not containing any if no slot where found)
     */
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

    /**
     * Check in the car with {@link LocalDateTime#now()}
     *
     * @param car The car you want to check in.
     * @return {@link CheckInResult} containing the corresponding slot (or not containing any if no slot where found)
     */
    public synchronized CheckInResult<T> checkIn(T car) {
        return checkIn(car, LocalDateTime.now());
    }

    /**
     * Allow you to check out a car.
     * It will find the slot containing the car.
     * It will then return {@link CheckOutResult} containing the allocated slot (copy) and corresponding price.
     *
     * @param car              The car you want to check in.
     * @param outgoingDateTime The outgoing date and time.
     * @return {@link CheckInResult} containing the corresponding slot and price.
     */
    public synchronized CheckOutResult<T> checkOut(T car, LocalDateTime outgoingDateTime) {
        ParkingSlot<T> usage = slots.stream().filter(s -> s.getCar() == car).findFirst().orElseThrow(
                () -> new ParkingException("Car " + car + " not found !")).free(outgoingDateTime);
        return new CheckOutResult<>(usage, pricingPolicy.computePrice(usage));
    }

    /**
     * Check out the car with {@link LocalDateTime#now()}
     *
     * @param car The car you want to check in.
     * @return {@link CheckInResult} containing the corresponding slot (or not containing any if no slot where found)
     */
    public synchronized CheckOutResult<T> checkOut(T car) {
        return checkOut(car, LocalDateTime.now());
    }

    /**
     * Used by the builder to validate the parking
     */
    void check() {
        if (this.pricingPolicy == null) {
            throw new ParkingException("You have to specify a Pricing strategy using setPricingPolicy(...) !");
        }
        if (this.slots.isEmpty()) {
            throw new ParkingException("Your parking does not contains any parking slots, add some using addSlot(...) !");
        }
    }

}
