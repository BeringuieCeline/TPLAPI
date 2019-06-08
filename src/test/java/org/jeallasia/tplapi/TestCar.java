package org.jeallasia.tplapi;

public class TestCar{

    private final int id;
    private final boolean chargeNotRequired;
    final CarType carType;
    private static int simpleCptForId=0;

    TestCar(CarType carType, boolean chargeNotRequired) {
        this.id = simpleCptForId++;
        this.carType = carType;
        this.chargeNotRequired = chargeNotRequired;
    }

    TestCar(CarType carType) {
        this(carType, false);
    }

    boolean isChargeNotRequired() {
        return chargeNotRequired;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", carType, id);
    }

}
