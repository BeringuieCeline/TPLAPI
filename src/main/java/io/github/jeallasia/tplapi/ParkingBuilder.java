package io.github.jeallasia.tplapi;

import java.util.function.Predicate;

/**
 * The builder associated with {@link Parking} used to specify parking slot allocation policy and pricing policy.
 * @param <T> the car class you want to use
 */
public class ParkingBuilder<T> {

    private final Parking<T> instance;
    private int cptAutoId = 0;

    ParkingBuilder() {
        this.instance = new Parking<>();
    }

    private int nextId() {
        return cptAutoId++;
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link PredicateWithAlt}
     *
     * @param id of the target {@link ParkingSlot}
     * @param policy the {@link PredicateWithAlt} used by the slot
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlot(String id, PredicateWithAlt<T> policy) {
        this.instance.addSlot(id, policy);
        return this;
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link PredicateWithAlt}
     *
     * @param policy the {@link PredicateWithAlt} used by the slot
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlot(PredicateWithAlt<T> policy) {
        return addSlot(String.valueOf(nextId()), policy);
    }

    /**
     * Create and add a new slots to the parking providing a {@link PredicateWithAlt}
     *
     * @param policy the {@link PredicateWithAlt} of created slots
     * @param nbrSlots the number of new slot to create
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlots(PredicateWithAlt<T> policy, int nbrSlots) {
        for (int i = 0; i < nbrSlots; i++) addSlot(policy);
        return this;
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link Predicate}
     *
     * @param id of the target {@link ParkingSlot}
     * @param policy the {@link Predicate} used by the slot
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlot(String id, Predicate<T> policy) {
        return addSlot(id, PredicateWithAlt.buildAlt(policy));
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link Predicate}
     *
     * @param policy the {@link Predicate} used by the slot
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlot(Predicate<T> policy) {
        return addSlot(PredicateWithAlt.buildAlt(policy));
    }

    /**
     * Create and add a new slots to the parking providing a {@link Predicate}
     *
     * @param policy the {@link Predicate} of created slots
     * @param nbrSlots the number of new slot to create
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlots(Predicate<T> policy, int nbrSlots) {
        return addSlots(PredicateWithAlt.buildAlt(policy), nbrSlots);
    }

    /**
     * Create and add a new slot to the parking providing
     * an id and a main {@link Predicate} and and a alt {@link Predicate}
     *
     * @param id of the target {@link ParkingSlot}
     * @param main the {@link Predicate} used by the slot
     * @param alt the {@link Predicate} used as alternative by the slot
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlot(String id, Predicate<T> main, Predicate<T> alt) {
        return addSlot(id, PredicateWithAlt.buildAlt(main, alt));
    }

    /**
     * Create and add a new slot to the parking providing
     * a main {@link Predicate} and and a alt {@link Predicate}
     *
     * @param main the {@link Predicate} used by the slot
     * @param alt the {@link Predicate} used as alternative by the slot
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlot(Predicate<T> main, Predicate<T> alt) {
        return addSlot(PredicateWithAlt.buildAlt(main, alt));
    }

    /**
     * Create and add a new slots to the parking providing
     * a main {@link Predicate} and and a alt {@link Predicate}
     *
     * @param main the {@link Predicate} used by the slot
     * @param alt the {@link Predicate} used as alternative by the slot
     * @param nbrSlots the number of new slot to create
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> addSlots(Predicate<T> main, Predicate<T> alt, int nbrSlots) {
        return addSlots(PredicateWithAlt.buildAlt(main, alt), nbrSlots);
    }

    /**
     * Set the pricing policy of the {@link Parking}
     * @param pricingPolicy the pricing policy to set
     * @return {@link ParkingBuilder} the builder
     */
    public ParkingBuilder<T> setPricingPolicy(PricingPolicy<T> pricingPolicy) {
        this.instance.setPricingPolicy(pricingPolicy);
        return this;
    }

    /**
     * The final step, that will validate (See {@link Parking#check()}) and return the {@link Parking}
     * @return the corresponding {@link Parking}
     */
    public Parking<T> build() {
        this.instance.check();
        return this.instance;
    }
}
