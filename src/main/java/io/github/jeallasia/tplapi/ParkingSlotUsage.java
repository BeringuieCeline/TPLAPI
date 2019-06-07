package io.github.jeallasia.tplapi;

import java.time.Duration;
import java.time.LocalDateTime;

public interface ParkingSlotUsage<T> {

    ParkingSlot<T> getSlot();
    T getCar();
    LocalDateTime getIncomingDateTime();
    LocalDateTime getOutgoingDateTime();
    boolean isUsingAlternative();

    default Duration computeDuration() {
        return Duration.between(
                getIncomingDateTime(),
                getOutgoingDateTime() != null ? getOutgoingDateTime() : LocalDateTime.now());
    }




}
