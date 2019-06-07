[![Build Status](https://travis-ci.com/Jallasia/TPLAPI.svg?branch=master)](https://travis-ci.com/Jallasia/TPLAPI)
[![Code Coverage](https://codecov.io/gh/Jallasia/TPLAPI/coverage.svg)](https://codecov.io/gh/Jallasia/TPLAPI)

# TPLAPI
Toll Parking Library Java API


## Usage

You can use any type of Class as input for the parking.
Let's take a simple example:

```java
public enum CarType {
    GASOLINE,
    ELECTRIC_20KW,
    ELECTRIC_50KW,
    OTHER
}

public class Car{
    private final CarType carType;
    private boolean chargeNotRequired;

    Car(CarType carType, boolean chargeNotRequired) {
        this.carType = carType;
        this.chargeNotRequired = chargeNotRequired;
    }

    public boolean isChargeNotRequired() {
        return chargeNotRequired;
    }
}
```

### Parking creation

Using your own Car definition you can now create your Parking using the corresponding builder.

#### The builder

```java
Parking<Car> parking = Parking.<Car>builder()
    // Set the pricing policy to 5 euros per hour started
    .setPricingPolicy(PricingPolicy.PER_STARTED_HOUR(Money.of(5, "EUR")))
    // Add 10 slot that will only accept E20KW cars
    .addSlots(car -> car.getCarType() == CarType.E20KW, 10)
    // Add 10 slot that will only accept E50KW cars
    .addSlots(car -> car.getCarType() == CarType.E50KW, 10)
    // Add 40 slot that will only accept GAS cars but also
    // alternatively accepting cars that don't have to be charged
    .addSlots(car -> car.getCarType() == CarType.GAS, Car::isChargeNotRequired, 40)
    // Finally call build to get the resulting parking.
    .build();
```

#### Pricing policies

Multiple pricing policies builder are already available, but you can also define your own.

```java
PricingPolicy<Car> fivePerStartedHour = PricingPolicy.PER_STARTED_HOUR(Money.of(5, "EUR"));
PricingPolicy<Car> fivePerFinishedHour = PricingPolicy.PER_FINISHED_HOUR(Money.of(5, "EUR"));
PricingPolicy<Car> twoFixed = PricingPolicy.FIXED(Money.of(2, "EUR"));
PricingPolicy<Car> fivePerStartedHourAndTwoFixed = PricingPolicy.PER_STARTED_HOUR_AND_FIXED(Money.of(5, "EUR"), Money.of(2, "EUR"));
PricingPolicy<Car> fivePerFinishedHourAndTwoFixed = PricingPolicy.PER_FINISHED_HOUR_AND_FIXED(Money.of(5, "EUR"), Money.of(2, "EUR"));

# To define you own policy you have to define:
# MonetaryAmount computePrice(ParkingSlot<T> slot);
# For example using lambda

PricingPolicy<TestCar> freeForElectric = slot -> {
    # If it's an electric car
    if ((slot.getCar().carType == CarType.ELECTRIC_20KW) || (slot.getCar().carType == CarType.ELECTRIC_50KW)){
        return Money.of(0, "EUR");
    }
    return fivePerFinishedHourAndTwoFixed.computePrice(slot);
};
```
