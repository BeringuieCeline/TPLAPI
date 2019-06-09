package io.github.jeallasia.tplapi;

/**
 * This class is used to expose the result of {@link Parking#checkIn(Object)}
 *
 * @param <T> The Car class you want to use
 */
public final class CheckInResult<T> {

    private ParkingSlot<T> slot;

    /**
     * Created during {@link Parking#checkIn(Object)}
     */
    CheckInResult() {

    }

    /**
     * Protected used by Parking during checkIn
     * It will be set with a copy of the {@link ParkingSlot}
     *
     * @param slot the allocated parking slot
     */
    void setSlot(ParkingSlot<T> slot) {
        this.slot = slot;
    }

    /**
     * Return {@code true} if a slot was found.
     *
     * @return {@code true} if a slot was found during checkIn phase
     */
    public boolean isSuccessful() {
        return slot != null;
    }

    /**
     * Return the slot that was allocated for this car.
     *
     * @return a copy of the allocated slot during checkIn phase
     */
    public ParkingSlot<T> geSlot() {
        return slot;
    }

}
