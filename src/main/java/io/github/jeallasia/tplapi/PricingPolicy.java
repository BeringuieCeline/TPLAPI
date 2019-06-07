package io.github.jeallasia.tplapi;
import io.github.jeallasia.tplapi.util.MonetaryTools;
import javax.money.MonetaryAmount;


public interface PricingPolicy<T> {

    MonetaryAmount computePrice(ParkingSlotUsage<T> usage);

    static <T> PricingPolicy<T> AND(PricingPolicy<T> policy1, PricingPolicy<T> policy2){
        return usage -> policy1.computePrice(usage).add(policy2.computePrice(usage));
    }
    static <T> PricingPolicy<T> PER_HOUR(MonetaryAmount pricePerHour, boolean countHourStarted){
        return usage -> MonetaryTools.computePerHour(usage.computeDuration(), pricePerHour, countHourStarted);
    }
    static <T> PricingPolicy<T> FIXED(MonetaryAmount fixedPrice){
        return usage -> fixedPrice;
    }

    // Convenient shortcuts
    static <T> PricingPolicy<T> PER_STARTED_HOUR(MonetaryAmount pricePerHour){
        return PER_HOUR(pricePerHour, true);
    }
    static <T> PricingPolicy<T> PER_FINISHED_HOUR(MonetaryAmount pricePerHour){
        return PER_HOUR(pricePerHour, false);
    }
    static <T> PricingPolicy<T> PER_FINISHED_HOUR_AND_FIXED(MonetaryAmount pricePerHour, MonetaryAmount fixedPrice){
        return AND(PER_FINISHED_HOUR(pricePerHour), FIXED(fixedPrice));
    }
    static <T> PricingPolicy<T> PER_STARTED_HOUR_AND_FIXED(MonetaryAmount pricePerHour, MonetaryAmount fixedPrice){
        return AND(PER_STARTED_HOUR(pricePerHour), FIXED(fixedPrice));
    }

}
