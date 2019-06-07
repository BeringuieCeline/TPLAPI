package oi.github.jeallasia.tplapi;

import io.github.jeallasia.tplapi.ParkingSlot;
import io.github.jeallasia.tplapi.ParkingSlotUsage;
import org.junit.Test;

import java.time.LocalDateTime;

public class ParkingSlotUsageTest {

    private static class SimpleParkingSlotUsage implements ParkingSlotUsage<TestCar> {
        final LocalDateTime in;
        final LocalDateTime out;

        SimpleParkingSlotUsage(LocalDateTime in, LocalDateTime out) {
            this.in = in;
            this.out = out;
        }
        SimpleParkingSlotUsage(LocalDateTime in) {
            this.in = in;
            this.out = null;
        }
        @Override
        public ParkingSlot<TestCar> getSlot() {
            return null;
        }

        @Override
        public TestCar getCar() {
            return null;
        }

        @Override
        public LocalDateTime getIncomingDateTime() {
            return in;
        }

        @Override
        public LocalDateTime getOutgoingDateTime() {
            return out;
        }

        @Override
        public boolean isUsingAlternative() {
            return false;
        }
    }

    @Test
    public void testDuration(){
        new SimpleParkingSlotUsage(LocalDateTime.now()).computeDuration();
    }
}
