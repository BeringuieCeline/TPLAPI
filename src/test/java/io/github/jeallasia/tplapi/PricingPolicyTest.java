package org.jeallasia.tplapi;

import org.junit.Test;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class PricingPolicyTest extends TestHelper {
    /**
     * Helper to test policies
     *
     * @param expectedNbrEuro   nbr of euro expected for the result
     * @param policy            the policy to test
     * @param durationInMinutes the duration of usage
     */
    private static void assertPriceEqual(int expectedNbrEuro, PricingPolicy<TestCar> policy, int durationInMinutes) {
        LocalDateTime start = LocalDateTime.now();
        assertEquals(euros(expectedNbrEuro),
                policy.computePrice(
                        new ParkingSlot<>("dummy", t -> true, e20(),
                                start, false, start.plus(Duration.ofMinutes(durationInMinutes))
                        )));
    }

    @Test
    public void PER_HOUR() {
        assertPriceEqual(5,
                PricingPolicy.PER_HOUR(euros(5), true), 30);
        assertPriceEqual(0,
                PricingPolicy.PER_HOUR(euros(5), false), 30);
    }

    @Test
    public void PER_FINISHED_HOUR() {
        PricingPolicy<TestCar> perFinishedHour5euros = PricingPolicy.PER_FINISHED_HOUR(euros(5));
        assertPriceEqual(0, perFinishedHour5euros, 30);
        assertPriceEqual(5, perFinishedHour5euros, 60);
        assertPriceEqual(5, perFinishedHour5euros, 119);
    }

    @Test
    public void PER_STARTED_HOUR() {
        PricingPolicy<TestCar> perStartedHour5euros = PricingPolicy.PER_STARTED_HOUR(euros(5));
        assertPriceEqual(5, perStartedHour5euros, 30);
        assertPriceEqual(5, perStartedHour5euros, 60);
        assertPriceEqual(10, perStartedHour5euros, 119);
    }

    @Test
    public void FIXED() {
        PricingPolicy<TestCar> fixed5euros = PricingPolicy.FIXED(euros(5));
        assertPriceEqual(5, fixed5euros, 30);
        assertPriceEqual(5, fixed5euros, 60);
        assertPriceEqual(5, fixed5euros, 119);
    }

    @Test
    public void PER_FINISHED_HOUR_AND_FIXED() {
        PricingPolicy<TestCar> perFinishedHour5eurosFixed1euro = PricingPolicy.PER_FINISHED_HOUR_AND_FIXED(
                euros(5), euros(1));
        assertPriceEqual(1, perFinishedHour5eurosFixed1euro, 30);
        assertPriceEqual(6, perFinishedHour5eurosFixed1euro, 60);
        assertPriceEqual(6, perFinishedHour5eurosFixed1euro, 119);
    }

    @Test
    public void PER_STARTED_HOUR_AND_FIXED() {
        PricingPolicy<TestCar> perStartedHour5eurosFixed1euro = PricingPolicy.PER_STARTED_HOUR_AND_FIXED(
                euros(5), euros(1));
        assertPriceEqual(6, perStartedHour5eurosFixed1euro, 30);
        assertPriceEqual(6, perStartedHour5eurosFixed1euro, 60);
        assertPriceEqual(11, perStartedHour5eurosFixed1euro, 119);
    }

    @Test
    public void AND() {
        PricingPolicy<TestCar> and = PricingPolicy.AND(
                PricingPolicy.FIXED(euros(1)),
                PricingPolicy.PER_FINISHED_HOUR(euros(5))
        );
        assertPriceEqual(1, and, 30);
        assertPriceEqual(6, and, 60);
        assertPriceEqual(6, and, 119);
    }

    @Test
    public void computePerHour() {
        Duration duration = Duration.ofHours(2);
        MonetaryAmount fiveEuros = euros(5);
        assertEquals(
                fiveEuros.multiply(2),
                PricingPolicy.computePerHour(duration, fiveEuros, true));
        assertEquals(
                fiveEuros.multiply(2),
                PricingPolicy.computePerHour(duration, fiveEuros, false));
        duration = duration.plus(Duration.ofMinutes(1));
        assertEquals(
                fiveEuros.multiply(3),
                PricingPolicy.computePerHour(duration, fiveEuros, true));
        assertEquals(
                fiveEuros.multiply(2),
                PricingPolicy.computePerHour(duration, fiveEuros, false));
    }

    @Test
    public void testCustom() {
        PricingPolicy<TestCar> freeForElectric = slot -> {
            if ((slot.getCar().carType == CarType.ELECTRIC_20KW) || (slot.getCar().carType == CarType.ELECTRIC_50KW)) {
                return euros(0);
            }
            return PricingPolicy.<TestCar>PER_STARTED_HOUR(euros(10)).computePrice(slot);
        };
        assertPriceEqual(0, freeForElectric, 5000);
    }
}
