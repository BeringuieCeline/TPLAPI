package io.github.jeallasia.tplapi;

import io.github.jeallasia.tplapi.exception.ParkingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Parking<T> {

    private static final class ParkingSlotWithUsage<T> {

        private static final class ParkingSlotUsageImpl<T> implements ParkingSlotUsage<T>{

            private final ParkingSlot<T> slot;
            private final T car;
            private final boolean usingAlternative;
            private final LocalDateTime incomingDateTime;
            private LocalDateTime outgoingDateTime;

            ParkingSlotUsageImpl(ParkingSlot<T> slot, T car, boolean usingAlternative, LocalDateTime incomingDateTime) {
                Objects.requireNonNull(incomingDateTime);
                this.slot = slot;
                this.car = car;
                this.incomingDateTime = incomingDateTime;
                this.usingAlternative = usingAlternative;
            }

            public ParkingSlot<T> getSlot() {
                return slot;
            }
            @Override
            public T getCar() {
                return car;
            }

            @Override
            public LocalDateTime getIncomingDateTime() {
                return incomingDateTime;
            }

            @Override
            public LocalDateTime getOutgoingDateTime() {
                return outgoingDateTime;
            }

            @Override
            public boolean isUsingAlternative() {
                return usingAlternative;
            }

            void setOutgoingDateTime(LocalDateTime outgoingDateTime) {
                Objects.requireNonNull(outgoingDateTime);
                if (incomingDateTime.isAfter(outgoingDateTime)) {
                    throw new IllegalArgumentException("Outgoing date should be after incoming date !");
                }
                this.outgoingDateTime = outgoingDateTime;
            }
        }

        final ParkingSlot<T> slot;
        ParkingSlotUsageImpl<T> usage;

        ParkingSlotWithUsage(ParkingSlot<T> slot) {
            this.slot = slot;
        }
        /**
         * This will tel you if the slot is occupied or not
         *
         * @return {@code true} if the slot is free
         */
        boolean isFree() {
            return usage == null;
        }

        /**
         * @param car the car you want to test
         * @return {@code true} if the slot is made for this kind of car
         */
        boolean test(T car){
            return slot.getPolicy().test(car);
        }

        /**
         * @param car the car you want to test
         * @return {@code true} if the slot is a valid alternative for this kind of car
         */
        boolean testAlt(T car){
            return slot.getPolicy().testAlt(car);
        }

        /**
         * @param car the car you want to test
         * @return {@code true} if the slot if made for this kind of car or is a valid alternative for this kind of car
         */
        boolean testCompatible(T car){
            return slot.getPolicy().testCompatible(car);
        }

        /**
         * Allow you to allocate the slot to a given can, make sure to indicate if it was selected as an alternative slot of not
         *
         * @param car the car you want to allocate the slot for
         * @param usingAlternative {@code true} if this slot was selected as an alternative slot
         * @param incomingDateTime the date of allocation
         * @return the corresponding {@link ParkingSlotUsageImpl} instance.
         */
        private ParkingSlotUsage<T> allocate(T car, boolean usingAlternative, LocalDateTime incomingDateTime) {
            usage = new ParkingSlotUsageImpl<>(slot, car, usingAlternative, incomingDateTime);
            return usage;
        }

        /**
         * Allow you to free the slot, and will make sure to set the {@link ParkingSlotUsageImpl#setOutgoingDateTime}
         *
         * @param outgoingDateTime the outgoing date
         * @return the corresponding {@link ParkingSlotUsageImpl} instance.
         */
        private ParkingSlotUsage<T> free(LocalDateTime outgoingDateTime) {
            ParkingSlotUsageImpl<T> result = usage;
            result.setOutgoingDateTime(outgoingDateTime);
            usage = null;
            return result;
        }
    }


    private final List<ParkingSlotWithUsage<T>> slotWithUsages = new ArrayList<>();
    private PricingPolicy<T> pricingPolicy;

    /**
     * Constructor, used from the Builder.
     */
    Parking() {
    }

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
        slotWithUsages.add(new ParkingSlotWithUsage<>(new ParkingSlot<>(id, policy)));
    }

    /**
     * Get the size of parking (total number of slots)
     *
     * @return the size of the parking.
     */
    public long getSize() {
        return slotWithUsages.size();
    }

    /**
     * @return stream on free {@link ParkingSlotWithUsage}
     */
    private Stream<ParkingSlotWithUsage<T>> getAllAvailable() {
        return slotWithUsages.stream().filter(ParkingSlotWithUsage::isFree);
    }

    /**
     * Will return a stream containing all slots that are compatible see {@link PredicateWithAlt} for a given car
     *
     * @param car the car you want to test
     * @return stream on free and compatible {@link ParkingSlotWithUsage#testCompatible(T)}
     */
    private Stream<ParkingSlotWithUsage<T>> getAvailableCompatibleFor(T car) {
        return getAllAvailable().filter(s -> s.testCompatible(car));
    }

    /**
     * Will return a stream containing all slots that are a valid alternative see {@link PredicateWithAlt} for a given car
     *
     * @param car the car you want to test
     * @return stream {@link ParkingSlotWithUsage} on free and alternative slot {@link ParkingSlot#getPolicy()}
     */
    private Stream<ParkingSlotWithUsage<T>> getAvailableAltFor(T car) {
        return getAllAvailable().filter(s -> s.testAlt(car));
    }

    /**
     * Will return a stream containing all slots that are a valid alternative see {@link PredicateWithAlt} for a given car
     *
     * @param car the car you want to test
     * @return stream on free and preferred slot {@link ParkingSlot}
     */
    private Stream<ParkingSlotWithUsage<T>> getAvailableFor(T car) {
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
        return slotWithUsages.stream().filter(s -> s.slot.getPolicy().testCompatible(car)).count();
    }

    public long getSizeAltFor(T car) {
        return slotWithUsages.stream().filter(s -> s.slot.getPolicy().testAlt(car)).count();
    }

    public long getSizeFor(T car) {
        return slotWithUsages.stream().filter(s -> s.slot.getPolicy().test(car)).count();
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
                p -> result.setUsage(p.allocate(car, false, incomingDateTime)),
                () -> getAvailableAltFor(car).findFirst().ifPresent(
                        alt -> result.setUsage(alt.allocate(car, true, incomingDateTime))
                )
        );
        return result;
    }

    public synchronized CheckInResult<T> checkIn(T car) {
        return checkIn(car, LocalDateTime.now());
    }

    public synchronized CheckOutResult<T> checkOut(T car, LocalDateTime outgoingDateTime) {
        ParkingSlotUsage<T> usage = slotWithUsages.stream().filter(s -> !s.isFree() && s.usage.getCar() == car).findFirst().orElseThrow(
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
        if (this.slotWithUsages.isEmpty()) {
            throw new ParkingException("Your parking does not contains any parking slots, add some using addSlot(...) !");
        }
    }

}
