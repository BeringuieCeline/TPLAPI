package org.jeallasia.tplapi;

import java.util.function.Predicate;

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
     * Create and add a new slot to the parking providing an id and a {@link PredicateWithAlt<T>}
     *
     * @param id of the target {@link ParkingSlot<T>}
     * @param policy the {@link PredicateWithAlt<T>} used by the slot
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlot(String id, PredicateWithAlt<T> policy) {
        this.instance.addSlot(id, policy);
        return this;
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link PredicateWithAlt<T>}
     *
     * @param policy the {@link PredicateWithAlt<T>} used by the slot
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlot(PredicateWithAlt<T> policy) {
        return addSlot(String.valueOf(nextId()), policy);
    }

    /**
     * Create and add a new slots to the parking providing a {@link PredicateWithAlt<T>}
     *
     * @param policy the {@link PredicateWithAlt<T>} of created slots
     * @param nbrSlots the number of new slot to create
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlots(PredicateWithAlt<T> policy, int nbrSlots) {
        for (int i = 0; i < nbrSlots; i++) addSlot(policy);
        return this;
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link Predicate<T>}
     *
     * @param id of the target {@link ParkingSlot<T>}
     * @param policy the {@link Predicate<T>} used by the slot
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlot(String id, Predicate<T> policy) {
        return addSlot(id, PredicateWithAlt.buildAlt(policy));
    }

    /**
     * Create and add a new slot to the parking providing an id and a {@link Predicate<T>}
     *
     * @param policy the {@link Predicate<T>} used by the slot
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlot(Predicate<T> policy) {
        return addSlot(PredicateWithAlt.buildAlt(policy));
    }

    /**
     * Create and add a new slots to the parking providing a {@link Predicate<T>}
     *
     * @param policy the {@link Predicate<T>} of created slots
     * @param nbrSlots the number of new slot to create
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlots(Predicate<T> policy, int nbrSlots) {
        return addSlots(PredicateWithAlt.buildAlt(policy), nbrSlots);
    }

    /**
     * Create and add a new slot to the parking providing
     * an id and a main {@link Predicate<T>} and and a alt {@link Predicate<T>}
     *
     * @param id of the target {@link ParkingSlot<T>}
     * @param main the {@link Predicate<T>} used by the slot
     * @param alt the {@link Predicate<T>} used as alternative by the slot
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlot(String id, Predicate<T> main, Predicate<T> alt) {
        return addSlot(id, PredicateWithAlt.buildAlt(main, alt));
    }

    /**
     * Create and add a new slot to the parking providing
     * a main {@link Predicate<T>} and and a alt {@link Predicate<T>}
     *
     * @param main the {@link Predicate<T>} used by the slot
     * @param alt the {@link Predicate<T>} used as alternative by the slot
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlot(Predicate<T> main, Predicate<T> alt) {
        return addSlot(PredicateWithAlt.buildAlt(main, alt));
    }

    /**
     * Create and add a new slots to the parking providing
     * a main {@link Predicate<T>} and and a alt {@link Predicate<T>}
     *
     * @param main the {@link Predicate<T>} used by the slot
     * @param alt the {@link Predicate<T>} used as alternative by the slot
     * @param nbrSlots the number of new slot to create
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> addSlots(Predicate<T> main, Predicate<T> alt, int nbrSlots) {
        return addSlots(PredicateWithAlt.buildAlt(main, alt), nbrSlots);
    }

    /**
     * Set the pricing policy of the {@link Parking}
     * @param pricingPolicy the pricing policy to set
     * @return {@link ParkingBuilder<T>} the builder
     */
    public ParkingBuilder<T> setPricingPolicy(PricingPolicy<T> pricingPolicy) {
        this.instance.setPricingPolicy(pricingPolicy);
        return this;
    }

    /**
     * The final step, that will validate (See {@link Parking<T>#check()}) and return the {@link Parking<T>}
     * @return the corresponding {@link Parking<T>}
     */
    public Parking<T> build() {
        this.instance.check();
        return this.instance;
    }
}
