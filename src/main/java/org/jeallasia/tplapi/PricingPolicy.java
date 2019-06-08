package org.jeallasia.tplapi;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.temporal.ChronoUnit;


public interface PricingPolicy<T> {

    MonetaryAmount computePrice(ParkingSlot<T> slot);

    static <T> PricingPolicy<T> AND(PricingPolicy<T> policy1, PricingPolicy<T> policy2) {
        return slot -> policy1.computePrice(slot).add(policy2.computePrice(slot));
    }

    static <T> PricingPolicy<T> PER_HOUR(MonetaryAmount pricePerHour, boolean countHourStarted) {
        return slot -> computePerHour(slot.computeDuration(), pricePerHour, countHourStarted);
    }

    static <T> PricingPolicy<T> FIXED(MonetaryAmount fixedPrice) {
        return usage -> fixedPrice;
    }

    // Convenient shortcuts
    static <T> PricingPolicy<T> PER_STARTED_HOUR(MonetaryAmount pricePerHour) {
        return PER_HOUR(pricePerHour, true);
    }

    static <T> PricingPolicy<T> PER_FINISHED_HOUR(MonetaryAmount pricePerHour) {
        return PER_HOUR(pricePerHour, false);
    }

    static <T> PricingPolicy<T> PER_FINISHED_HOUR_AND_FIXED(MonetaryAmount pricePerHour, MonetaryAmount fixedPrice) {
        return AND(PER_FINISHED_HOUR(pricePerHour), FIXED(fixedPrice));
    }

    static <T> PricingPolicy<T> PER_STARTED_HOUR_AND_FIXED(MonetaryAmount pricePerHour, MonetaryAmount fixedPrice) {
        return AND(PER_STARTED_HOUR(pricePerHour), FIXED(fixedPrice));
    }

    static MonetaryAmount computePerHour(Duration duration, MonetaryAmount pricePerHour, boolean countHourStarted) {
        MonetaryAmount price = pricePerHour.multiply(duration.toHours());
        if (countHourStarted && (duration.truncatedTo(ChronoUnit.HOURS) != duration)) {
            price = price.add(pricePerHour);
        }
        return price;
    }

}
