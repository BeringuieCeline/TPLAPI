package oi.github.jeallasia.tplapi.util;

import io.github.jeallasia.tplapi.util.MonetaryTools;
import oi.github.jeallasia.tplapi.TestHelper;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.time.Duration;

import static org.junit.Assert.*;

public class MonetaryToolsTest extends TestHelper {

    @Test
    public void computePerHour() {
        Duration duration = Duration.ofHours(2);
        MonetaryAmount fiveEuros=Money.of(5, "EUR");
        assertEquals(
                fiveEuros.multiply(2),
                MonetaryTools.computePerHour(duration, fiveEuros, true));
        assertEquals(
                fiveEuros.multiply(2),
                MonetaryTools.computePerHour(duration, fiveEuros,false));
        duration = duration.plus(Duration.ofMinutes(1));
        assertEquals(
                fiveEuros.multiply(3),
                MonetaryTools.computePerHour(duration, fiveEuros, true));
        assertEquals(
                fiveEuros.multiply(2),
                MonetaryTools.computePerHour(duration, fiveEuros,false));
    }

}
