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
        LocalDateTime outgoingDateTime = getOutgoingDateTime();
        if(outgoingDateTime==null){
            outgoingDateTime = LocalDateTime.now();
        }
        return Duration.between(getIncomingDateTime(), outgoingDateTime);
    }




}
