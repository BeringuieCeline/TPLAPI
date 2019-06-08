package org.jeallasia.tplapi;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.temporal.ChronoUnit;


/**
 * Represents a pricing policy that can compute a price from a given {@link ParkingSlot}
 *
 * @param <T> the type of the ParkingSlot input (The car class you are using)
 */
public interface PricingPolicy<T> {

    /**
     * The method that provide a pricing from the provided ParkingSlot data
     *
     * @param slot the {@link ParkingSlot<T>} that requires pricing
     * @return the price in {@link MonetaryAmount}
     */
    MonetaryAmount computePrice(ParkingSlot<T> slot);

    /**
     * Create a new pricing policy from 2 others (sum of both results)
     *
     * @param policy1 the first policy
     * @param policy2 the second one
     * @param <T> the Class you use for your cars
     * @return resulting {@link PricingPolicy<T>}
     */
    static <T> PricingPolicy<T> AND(PricingPolicy<T> policy1, PricingPolicy<T> policy2) {
        return slot -> policy1.computePrice(slot).add(policy2.computePrice(slot));
    }

    /**
     * Build pricing policy based on a price per hour
     *
     * @param pricePerHour the price per hour
     * @param countHourStarted if true, count hour started (only hour finished if false)
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy<T>} based on a price per hour
     */
    static <T> PricingPolicy<T> PER_HOUR(MonetaryAmount pricePerHour, boolean countHourStarted) {
        return slot -> computePerHour(slot.computeDuration(), pricePerHour, countHourStarted);
    }

    /**
     * Build pricing policy based on a fixed price (return this fixed price regardless of duration)
     *
     * @param fixedPrice fixed price
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy<T>} based on a price per hour
     */
    static <T> PricingPolicy<T> FIXED(MonetaryAmount fixedPrice) {
        return usage -> fixedPrice;
    }

    /**
     * Build pricing policy based on a price per started hour
     *
     * @param pricePerHour the price per hour
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy<T>} based on a price per started hour
     */
    static <T> PricingPolicy<T> PER_STARTED_HOUR(MonetaryAmount pricePerHour) {
        return PER_HOUR(pricePerHour, true);
    }

    /**
     * Build pricing policy based on a price per finished hour
     *
     * @param pricePerHour the price per hour
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy<T>} based on a price per finished hour
     */
    static <T> PricingPolicy<T> PER_FINISHED_HOUR(MonetaryAmount pricePerHour) {
        return PER_HOUR(pricePerHour, false);
    }

    /**
     * Build pricing policy based on a price per finished hour and a fixed price
     *
     * @param pricePerHour the price per hour
     * @param fixedPrice fixed price
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy<T>} based on a price per finished hour and a fixed price
     */
    static <T> PricingPolicy<T> PER_FINISHED_HOUR_AND_FIXED(MonetaryAmount pricePerHour, MonetaryAmount fixedPrice) {
        return AND(PER_FINISHED_HOUR(pricePerHour), FIXED(fixedPrice));
    }

    /**
     * Build pricing policy based on a price per started hour and a fixed price
     *
     * @param pricePerHour the price per hour
     * @param fixedPrice fixed price
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy<T>} based on a price per started hour and a fixed price
     */
    static <T> PricingPolicy<T> PER_STARTED_HOUR_AND_FIXED(MonetaryAmount pricePerHour, MonetaryAmount fixedPrice) {
        return AND(PER_STARTED_HOUR(pricePerHour), FIXED(fixedPrice));
    }

    /**
     * Method to compute price per hour from {@link Duration}
     *
     * @param duration the duration to price
     * @param pricePerHour the price per hour
     * @param countHourStarted if true, count hour started (only hour finished if false)
     * @return the price in {@link MonetaryAmount}
     */
    static MonetaryAmount computePerHour(Duration duration, MonetaryAmount pricePerHour, boolean countHourStarted) {
        MonetaryAmount price = pricePerHour.multiply(duration.toHours());
        if (countHourStarted && (duration.truncatedTo(ChronoUnit.HOURS) != duration)) {
            price = price.add(pricePerHour);
        }
        return price;
    }

}
