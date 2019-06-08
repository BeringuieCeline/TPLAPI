package org.jeallasia.tplapi;

public final class CheckInResult<T> {

    private ParkingSlot<T> slot;

    void setSlot(ParkingSlot<T> slot) {
        this.slot = slot;
    }

    public boolean isSuccessful(){
        return slot != null;
    }

    public ParkingSlot<T> geSlot() {
        return slot;
    }

}
