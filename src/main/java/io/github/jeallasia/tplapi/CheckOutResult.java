package io.github.jeallasia.tplapi;

import javax.money.MonetaryAmount;

public final class CheckOutResult<T>{

    private final ParkingSlotUsage<T> usage;
    private final MonetaryAmount price;

    CheckOutResult(ParkingSlotUsage<T> usage, MonetaryAmount price){
        this.usage = usage;
        this.price = price;
    }

    public ParkingSlotUsage<T> getUsage() {
        return usage;
    }

    public MonetaryAmount getPrice() {
        return price;
    }
}
