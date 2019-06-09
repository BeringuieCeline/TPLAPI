package io.github.jeallasia.tplapi;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ParkingSlotTest extends TestHelper {

    @Test
    public void create() {
        ParkingSlot<TestCar> slot = new ParkingSlot<>("name", c -> true);
        assertTrue(slot.isFree());
    }

    @Test
    public void allocate() {
        TestCar car = e20();
        ParkingSlot<TestCar> slot = new ParkingSlot<>("name", c -> true);
        ParkingSlot<TestCar> allocatedSlot = slot.allocate(car, LocalDateTime.now(), true);
        assertEquals(car, allocatedSlot.getCar());
    }

    @Test
    public void computeDuration() {
        ParkingSlot<TestCar> slot = new ParkingSlot<>("name", c -> true);
        assertEquals(Duration.ZERO, slot.computeDuration());
        slot.allocate(e20(), LocalDateTime.now().minus(Duration.ofMinutes(10)), true);
        assertNotEquals(Duration.ZERO, slot.computeDuration());
        assertNotEquals(Duration.ZERO, slot.free(LocalDateTime.now()).computeDuration());
    }

}
