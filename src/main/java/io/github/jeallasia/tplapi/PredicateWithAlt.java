package org.jeallasia.tplapi;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Classic {@link Predicate} with an alternative test, this allows {@link Parking} to select {@link ParkingSlot}
 * using first {@link PredicateWithAlt#test(Object)} and if not found to use {@link PredicateWithAlt#testAlt(Object)}
 * to find a potential alternative slot.
 *
 * @param <T> the type of the input to the predicate (The car class you are using)
 */
public interface PredicateWithAlt<T> extends Predicate<T> {

    /**
     * Evaluates the alternative predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the alternative predicate,
     * otherwise {@code false}
     */
    default boolean testAlt(T t) {
        return false;
    }

    /**
     * Evaluates the standard (See {@link Predicate#test} and the alternative predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate or the alternative predicate,
     * otherwise {@code false}
     */
    default boolean testCompatible(T t) {
        return test(t) || testAlt(t);
    }

    /**
     * Used to build a PredicateWithAlt (without any alternative predicate) from a classic Predicate
     * @param main the main predicate
     * @param <T> the type of the input to the predicate
     * @return a PredicateWithAlt (without any alternative predicate)
     */
    static <T> PredicateWithAlt<T> buildAlt(Predicate<T> main) {
        Objects.requireNonNull(main);
        return main::test;
    }

    /**
     * Used to build a PredicateWithAlt from two classic Predicate
     * @param main the main predicate
     * @param alt the alternative predicate
     * @param <T> the type of the input to the predicate
     * @return a PredicateWithAlt using the main one for test and the alt one for testAlt
     */
    static <T> PredicateWithAlt<T> buildAlt(Predicate<T> main, Predicate<T> alt) {
        Objects.requireNonNull(main);
        Objects.requireNonNull(alt);
        return new PredicateWithAlt<>() {
            @Override
            public boolean test(T t) {
                return main.test(t);
            }

            @Override
            public boolean testAlt(T t) {
                return alt.test(t);
            }
        };
    }

}
