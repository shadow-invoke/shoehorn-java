package io.shadowstack;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.shadowstack.Fluently.shoehorn;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestOneToManyArgs {
    @AllArgsConstructor
    public static class Confirmation {
        String ticker;
        Double units;
        Double price;
        String id;
    }
    public static class Order {
        String ticker;
        Double spend;
        Double strike;
    }
    public static class Ticker2Order implements ArgumentConverter<String, Order> {
        public static ArgumentConverter<String, Order> INSTANCE = new Ticker2Order();
        @Override
        public Order convert(String from) throws AdapterException {
            Order order = new Order();
            order.ticker = from;
            return order;
        }
        @Override
        public void convert(String from, Order to) throws AdapterException {
            to.ticker = from;
        }
    }
    public static class Spend2Order implements ArgumentConverter<Double, Order> {
        public static ArgumentConverter<Double, Order> INSTANCE = new Spend2Order();
        @Override
        public Order convert(Double from) throws AdapterException {
            Order order = new Order();
            order.spend = from;
            order.strike = order.spend / 10.0D; // default is that we try to buy 10 units with our spend
            return order;
        }
        @Override
        public void convert(Double from, Order to) throws AdapterException {
            to.spend = from;
            to.strike = to.spend / 10.0D;
        }
    }
    public static class Confirmation2Units implements ArgumentConverter<Confirmation, Double> {
        public static ArgumentConverter<Confirmation, Double> INSTANCE = new Confirmation2Units();
        @Override
        public Double convert(Confirmation from) throws AdapterException {
            return from.units;
        }
        @Override
        public void convert(Confirmation from, Double to) throws AdapterException {
            throw new AdapterException("not implemented");
        }
    }
    public static interface SimpleStockBuyer {
        Double purchase(String ticker, Double spend);
    }
    public static class OurStockBuyer {
        @Mimic(type = SimpleStockBuyer.class, method = "purchase")
        @Out(to = Double.class, with = Confirmation2Units.class)
        public Confirmation fulfill(@In(from = {String.class, Double.class}, with = {Ticker2Order.class, Spend2Order.class}) Order order) {
            Double bought = order.spend/order.strike;
            String orderId = UUID.randomUUID().toString();
            return new Confirmation(order.ticker, bought, order.strike, orderId);
        }
    }

    @Test
    public void testManyToOneArgs() throws AdapterException {
        SimpleStockBuyer simpleBuyer = shoehorn(new OurStockBuyer()).into(SimpleStockBuyer.class).build();
        assertEquals(10.0D, simpleBuyer.purchase("FOO", 100.0D));
    }
}
