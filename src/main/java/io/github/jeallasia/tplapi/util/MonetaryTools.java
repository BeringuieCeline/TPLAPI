package io.github.jeallasia.tplapi.util;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MonetaryTools {

    public static MonetaryAmount computePerHour(Duration duration, MonetaryAmount pricePerHour, boolean countHourStarted){
        MonetaryAmount price = pricePerHour.multiply(duration.toHours());
        if (countHourStarted && (duration.truncatedTo(ChronoUnit.HOURS) != duration)){
            price = price.add(pricePerHour);
        }
        return price;
    }

}
