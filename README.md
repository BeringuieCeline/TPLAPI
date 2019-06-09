[![Build Status](https://travis-ci.com/Jallasia/TPLAPI.svg?branch=master)](https://travis-ci.com/Jallasia/TPLAPI)
[![Code Coverage](https://codecov.io/gh/Jallasia/TPLAPI/coverage.svg)](https://codecov.io/gh/Jallasia/TPLAPI)

# TPLAPI
Toll Parking Library Java API compatible with your own Car class allow you to define your parking (specifying parking slot allocation policy and pricing policy) and use it to check in check out cars.

## Build from source

```
git clone git@github.com:Jallasia/TPLAPI.git
cd TPLAPI
./gradlew build
```

The JAR will then be available into build/libs folder.

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

### Parking usage

Let's define some cars:

```java
Car e20ChargeNotRequired = new Car(CarType.E20KW, true);

```

#### CheckIn

When you checkIn a car, the parking will return a CheckInResult:
- The CheckInResult if successful contains a copy of the ParkingSlot<Car> that was allocated for the car.
- The slot allocated will be the first one that is available and that accept this kind of car, if there are no more slots available using the primary criteria, then the alternative one will be used.

```java
Car e20 = new Car(CarType.E20KW, false);
CheckInResult<Car> checkInResult = parking.checkIn(e20);
if(checkInResult.isSuccessful()){
    ParkingSlot<Car> theAllocatedSlot = checkInResult.getSlot();
}
```

#### CheckOut

When you checkOut a car, the parking will return a CheckOutResult:
- The CheckOutResult contains a copy of the ParkingSlot<Car> that was allocated for the car (the one in the parking will of course be flagged as free).
- And the computed Price.

```java
CheckOutResult<Car> checkOutResult = parking.checkOut(e20);
MonetaryAmount price = checkOutResult.Price();
```