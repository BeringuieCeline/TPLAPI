package io.github.jeallasia.tplapi;
import io.github.jeallasia.tplapi.util.PredicateWithAlt;

import java.util.function.Predicate;

public class ParkingBuilder<T> {

    private final Parking<T> instance;
    private int cptAutoId = 0;

    ParkingBuilder() {
        this.instance = new Parking<>();
    }

    private int nextId(){
        return cptAutoId++;
    }

    public ParkingBuilder<T> addSlot(String id, PredicateWithAlt<T> policy) {
        this.instance.addSlot(id, policy);
        return this;
    }
    public ParkingBuilder<T> addSlot(PredicateWithAlt<T> policy) {
        return addSlot(String.valueOf(nextId()), policy);
    }
    public ParkingBuilder<T> addSlots(PredicateWithAlt<T> policy, int nbrSlots) {
        for (int i = 0; i < nbrSlots; i++) addSlot(policy);
        return this;
    }
    public ParkingBuilder<T> addSlot(String id, Predicate<T> policy) { return addSlot(id, PredicateWithAlt.buildAlt(policy)); }
    public ParkingBuilder<T> addSlot(Predicate<T> policy) { return addSlot(PredicateWithAlt.buildAlt(policy)); }
    public ParkingBuilder<T> addSlots(Predicate<T> policy, int nbrSlots) { return addSlots(PredicateWithAlt.buildAlt(policy), nbrSlots); }
    public ParkingBuilder<T> addSlot(String id, Predicate<T> main, Predicate<T> alt) { return addSlot(id, PredicateWithAlt.buildAlt(main, alt)); }
    public ParkingBuilder<T> addSlot(Predicate<T> main, Predicate<T> alt) { return addSlot(PredicateWithAlt.buildAlt(main, alt)); }
    public ParkingBuilder<T> addSlots(Predicate<T> main, Predicate<T> alt, int nbrSlots) { return addSlots(PredicateWithAlt.buildAlt(main, alt), nbrSlots); }

    public ParkingBuilder<T> setPricingPolicy(PricingPolicy<T> pricingPolicy) {
        this.instance.setPricingPolicy(pricingPolicy);
        return this;
    }

    public Parking<T> build() {
        this.instance.check();
        return this.instance;
    }
}
