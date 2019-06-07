package io.github.jeallasia.tplapi.util;
import java.util.Objects;
import java.util.function.Predicate;

public interface PredicateWithAlt<T> extends Predicate<T> {

    default boolean testAlt(T t){
        return false;
    }

    default boolean testCompatible(T t){
        return test(t) || testAlt(t);
    }

    static <T> PredicateWithAlt<T> buildAlt(Predicate<T>  main) {
        Objects.requireNonNull(main);
        return main::test;
    }

    static <T> PredicateWithAlt<T> buildAlt(Predicate<T>  main, Predicate<T> alt) {
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
