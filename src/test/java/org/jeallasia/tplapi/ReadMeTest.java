package org.jeallasia.tplapi;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ReadMeTest {

    public enum CarType { GAS, E20KW, E50KW, OTHER }

    public class Car{
        private final CarType carType;
        private boolean chargeNotRequired;

        Car(CarType carType, boolean chargeNotRequired) {
            this.carType = carType;
            this.chargeNotRequired = chargeNotRequired;
        }

        boolean isChargeNotRequired() { return chargeNotRequired; }
        CarType getCarType(){ return carType; }
    }

    @Test
    public void testFromREADME(){
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
                .build();

        Car e20 = new Car(CarType.E20KW, true);
        CheckInResult<Car> checkInResult = parking.checkIn(e20, LocalDateTime.now().minus(Duration.ofMinutes(5)));
        assertTrue(checkInResult.isSuccessful());
        assertEquals(Money.of(5, "EUR"), parking.checkOut(e20).getPrice());
    }

}
