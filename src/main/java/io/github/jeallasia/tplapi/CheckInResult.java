package io.github.jeallasia.tplapi;

public final class CheckInResult<T> {

    private ParkingSlotUsage<T> usage;

    void setUsage(ParkingSlotUsage<T> usage) {
        this.usage = usage;
    }

    public boolean isSuccessful(){
        return usage != null;
    }

    public ParkingSlotUsage<T> getUsage() {
        return usage;
    }

}
