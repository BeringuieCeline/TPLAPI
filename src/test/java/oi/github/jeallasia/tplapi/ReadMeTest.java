package oi.github.jeallasia.tplapi;
import io.github.jeallasia.tplapi.CheckInResult;
import io.github.jeallasia.tplapi.Parking;
import io.github.jeallasia.tplapi.PricingPolicy;
import org.javamoney.moneta.Money;
import org.junit.Test;

public class ReadMeTest {

    public enum CarType { GAS, E20KW, E50KW, OTHER }

    public class Car{
        private final CarType carType;
        private boolean chargeNotRequired;

        Car(CarType carType, boolean chargeNotRequired) {
            this.carType = carType;
            this.chargeNotRequired = chargeNotRequired;
        }

        public boolean isChargeNotRequired() { return chargeNotRequired; }
        public CarType getCarType(){ return carType; }
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
        CheckInResult<Car> checkInResult = parking.checkIn(e20);
    }

}
