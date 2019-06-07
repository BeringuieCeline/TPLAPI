package oi.github.jeallasia.tplapi;
import io.github.jeallasia.tplapi.*;
import io.github.jeallasia.tplapi.exception.ParkingException;
import io.github.jeallasia.tplapi.PredicateWithAlt;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingBuilderTest extends TestHelper{

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void noPolicyDefine() {
        expectedEx.expect(ParkingException.class);
        expectedEx.expectMessage("You have to specify a Pricing strategy using setPricingPolicy(...) !");
        Parking.<TestCar>builder().addSlot(isE20).build();
    }

    @Test
    public void noSlotsDefine() {
        expectedEx.expect(ParkingException.class);
        expectedEx.expectMessage("Your parking does not contains any parking slots, add some using addSlot(...) !");
        Parking.<TestCar>builder().setPricingPolicy(PricingPolicy.PER_STARTED_HOUR(euros(42))).build();
    }

    @Test
    public void addSlot() {
        Assert.assertEquals(Parking.<TestCar>builder().setPricingPolicy(PricingPolicy.PER_STARTED_HOUR(euros(5)))
                .addSlot(isE20)
                .addSlot("20", isE20)
                .addSlot(isE20.or(isE50))
                .addSlot("20_OR_50", isE20.or(isE50))
                .addSlot(isGAS, isChargeNotRequired)
                .addSlot("GAS__ALT_chargeNotRequired", isGAS, isChargeNotRequired)
                .addSlot(PredicateWithAlt.buildAlt(isGAS, isChargeNotRequired))
                .addSlot("PredicateWithAlt", PredicateWithAlt.buildAlt(isGAS, isChargeNotRequired))
                .build().getSize(), 8);
    }

    @Test
    public void addSlots() {
        ParkingBuilder<TestCar> builder = Parking.builder();
        Assert.assertEquals(builder.setPricingPolicy(PricingPolicy.PER_STARTED_HOUR(euros(5)))
                .addSlots(isE20, 10)
                .addSlots(isE20.or(isE50), 10)
                .addSlots(isGAS, isChargeNotRequired, 10)
                .addSlots(PredicateWithAlt.buildAlt(isGAS, isChargeNotRequired), 10)
                .build().getSize(), 40);
    }

    @Test
    public void setCustomPricingPolicy() {
        // With custom policy that gives 0 if it's an electric car ! And classic for other cars
        Parking<TestCar> parking = Parking.<TestCar>builder()
                .setPricingPolicy(
                    usage -> isE20.or(isE50).test(usage.getCar()) ? euros(0): FIVE_PER_HOUR_STARTED_ONE_FIXED.computePrice(usage))
                .addSlot(isE20)
                .addSlot(isGAS)
                .build();

        LocalDateTime incomingDateTime = localDateTime(6, 5);
        LocalDateTime outgoingDateTime = incomingDateTime.plus(Duration.ofMinutes(50));

        TestCar electricCar = e20();
        parking.checkIn(electricCar, incomingDateTime);
        Assert.assertEquals(euros(0), parking.checkOut(electricCar, outgoingDateTime).getPrice());

        TestCar gasCar = gas();
        parking.checkIn(gasCar, incomingDateTime);
        Assert.assertEquals(FIVE.add(ONE), parking.checkOut(gasCar, outgoingDateTime).getPrice());

    }
}
