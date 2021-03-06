package io.github.jeallasia.tplapi;

import javax.money.MonetaryAmount;

/**
 * This class is used to expose the result of {@link Parking#checkOut(Object)}
 *
 * @param <T> The Car class you want to use
 */
public final class CheckOutResult<T> {

    private final ParkingSlot<T> slot;
    private final MonetaryAmount price;

    /**
     * Created during {@link Parking#checkOut(Object)}
     */
    CheckOutResult(ParkingSlot<T> slot, MonetaryAmount price) {
        this.slot = slot;
        this.price = price;
    }

    /**
     * Return a copy of the slot that was allocated to this car
     *
     * @return a copy of the slot that was allocated to this car
     */
    public ParkingSlot<T> getSlot() {
        return slot;
    }

    /**
     * Return the computed price
     *
     * @return the computed price
     */
    public MonetaryAmount getPrice() {
        return price;
    }
}
