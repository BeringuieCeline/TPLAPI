package org.jeallasia.tplapi;

import javax.money.MonetaryAmount;

public final class CheckOutResult<T>{

    private final ParkingSlot<T> slot;
    private final MonetaryAmount price;

    CheckOutResult(ParkingSlot<T> slot, MonetaryAmount price){
        this.slot = slot;
        this.price = price;
    }

    public ParkingSlot<T> getSlot() {
        return slot;
    }
    public MonetaryAmount getPrice() {
        return price;
    }
}
