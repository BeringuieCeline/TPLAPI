package org.jeallasia.tplapi;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TestHelper {

    private static Predicate<TestCar> isType(CarType type){
        return c -> c.carType == type;
    }
    // You can build the predicate like this
    static final Predicate<TestCar> isGAS = isType(CarType.GASOLINE);
    static final Predicate<TestCar> isE20 = isType(CarType.ELECTRIC_20KW);
    // Or also like directly this
    static final Predicate<TestCar> isE50 = c -> c.carType == CarType.ELECTRIC_50KW;
    static final Predicate<TestCar> isChargeNotRequired = TestCar::isChargeNotRequired;

    static TestCar gas(){return new TestCar(CarType.GASOLINE);}
    static TestCar e20(){return new TestCar(CarType.ELECTRIC_20KW);}
    static TestCar e50(){return new TestCar(CarType.ELECTRIC_50KW);}
    static TestCar other(){return new TestCar(CarType.OTHER);}
    static TestCar e20ChargeNotRequired(){return new TestCar(CarType.ELECTRIC_20KW, true);}
    static TestCar e50ChargeNotRequired(){return new TestCar(CarType.ELECTRIC_50KW, true);}

    
    private static List<TestCar> buildList(CarType carType, boolean chargeNotRequired, int number){
        return IntStream.range(0, number).mapToObj(i -> new TestCar(carType, chargeNotRequired)).collect(Collectors.toList());
    }
    static List<TestCar> e20List(int number){ return buildList(CarType.ELECTRIC_20KW, false, number); }
    static List<TestCar> e50List(int number){ return buildList(CarType.ELECTRIC_50KW, false, number); }
    static List<TestCar> gasList(int number){ return buildList(CarType.GASOLINE, false, number); }
    static List<TestCar> e20ChargeNotRequiredList(int number){ return buildList(CarType.ELECTRIC_20KW, true, number); }
    static List<TestCar> e50ChargeNotRequiredList(int number){ return buildList(CarType.ELECTRIC_50KW, true, number); }


    static LocalDateTime localDateTime(int hour, int minute){
        return LocalDateTime.of(2019, 6, 4, hour, minute, 0);
    }
    static final LocalDateTime dateTime6h5min = localDateTime(6, 5);
    static final Duration duration50min = Duration.ofMinutes(50);
    static final LocalDateTime dateTime6h55min = dateTime6h5min.plus(duration50min);


    static MonetaryAmount euros(int number){
        return Money.of(number, "EUR");
    }
    final static MonetaryAmount FIVE=euros(5);
    final static MonetaryAmount ONE=euros(1);
    final static PricingPolicy<TestCar> FIVE_PER_HOUR_STARTED_ONE_FIXED =PricingPolicy.PER_STARTED_HOUR_AND_FIXED(FIVE, ONE);



}
