package io.shadowstack.shoehorn;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static io.shadowstack.shoehorn.Fluently.shoehorn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestCustomConverterAccessors {
    @AllArgsConstructor
    public static class IntegerResult {
        public int result;
    }
    @AllArgsConstructor
    public static class IntegerRequest {
        public int lhs;
        public int rhs;
    }
    public interface IntegerAdder {
        IntegerResult add (IntegerRequest request);
    }
    @AllArgsConstructor
    public static class DoubleResult {
        public double result;
    }
    @AllArgsConstructor
    public static class DoubleRequest {
        public double lhs;
        public double rhs;
    }
    public static class IntegerRequest2DoubleRequest1 implements ArgumentConverter<IntegerRequest, DoubleRequest> {
        public static ArgumentConverter<IntegerRequest, DoubleRequest> CONVERTER = new IntegerRequest2DoubleRequest1();
        @Override
        public DoubleRequest convert(IntegerRequest from) throws AdapterException {
            return new DoubleRequest((double)from.lhs, (double)from.rhs);
        }
        @Override
        public void convert(IntegerRequest from, DoubleRequest to) throws AdapterException {
            to.lhs = (double)from.lhs;
            to.rhs = (double)from.rhs;
        }
    }
    public static class IntegerRequest2DoubleRequest2 implements ArgumentConverter<IntegerRequest, DoubleRequest> {
        @Override
        public DoubleRequest convert(IntegerRequest from) throws AdapterException {
            return new DoubleRequest((double)from.lhs, (double)from.rhs);
        }
        @Override
        public void convert(IntegerRequest from, DoubleRequest to) throws AdapterException {}
    }
    public static class DoubleResult2IntegerResult1 implements ArgumentConverter<DoubleResult, IntegerResult> {
        @Override
        public IntegerResult convert(DoubleResult from) throws AdapterException {
            return new IntegerResult((int)from.result);
        }
        @Override
        public void convert(DoubleResult from, IntegerResult to) throws AdapterException {
            to.result = (int)from.result;
        }
        public static ArgumentConverter<DoubleResult, IntegerResult> getInstance() {
            return new DoubleResult2IntegerResult1();
        }
    }
    public static class DoubleResult2IntegerResult2 implements ArgumentConverter<DoubleResult, IntegerResult> {
        @Override
        public IntegerResult convert(DoubleResult from) throws AdapterException {
            return new IntegerResult((int)from.result);
        }
        @Override
        public void convert(DoubleResult from, IntegerResult to) throws AdapterException {
            to.result = (int)from.result;
        }
        public static ArgumentConverter<DoubleResult, IntegerResult> instance() {
            return new DoubleResult2IntegerResult2();
        }
    }
    public class DoubleAdder1 {
        @Mimic(type = IntegerAdder.class, method = "add")
        @Out(to = IntegerResult.class, with = DoubleResult2IntegerResult1.class)
        public DoubleResult add(@In(from = IntegerRequest.class, with = IntegerRequest2DoubleRequest1.class, singletonMembers = "CONVERTER") DoubleRequest request) {
            return new DoubleResult(request.lhs + request.rhs);
        }
    }
    public class DoubleAdder2 {
        @Mimic(type = IntegerAdder.class, method = "add")
        @Out(to = IntegerResult.class, with = DoubleResult2IntegerResult2.class)
        public DoubleResult add(@In(from = IntegerRequest.class, with = IntegerRequest2DoubleRequest1.class, singletonMembers = "CONVERTER") DoubleRequest request) {
            return new DoubleResult(request.lhs + request.rhs);
        }
    }
    public class DoubleAdder3 {
        @Mimic(type = IntegerAdder.class, method = "add")
        @Out(to = IntegerResult.class, with = DoubleResult2IntegerResult2.class)
        public DoubleResult add(@In(from = IntegerRequest.class, with = IntegerRequest2DoubleRequest2.class) DoubleRequest request) {
            return new DoubleResult(request.lhs + request.rhs);
        }
    }
    public class DoubleAdder4 {
        @Mimic(type = IntegerAdder.class, method = "add")
        @Out(to = IntegerResult.class, with = DoubleResult2IntegerResult2.class)
        public DoubleResult add(@In(from = IntegerRequest.class, with = IntegerRequest2DoubleRequest1.class, singletonMembers = "NOPE") DoubleRequest request) {
            return new DoubleResult(request.lhs + request.rhs);
        }
    }

    @Test
    public void testGood() throws AdapterException {
        IntegerAdder integerAdder = shoehorn(new DoubleAdder1()).into(IntegerAdder.class).build();
        IntegerResult integerResult = integerAdder.add(new IntegerRequest(2, 3));
        assertEquals(5, integerResult.result);
        integerAdder = shoehorn(new DoubleAdder2()).into(IntegerAdder.class).build();
        integerResult = integerAdder.add(new IntegerRequest(25, 32));
        assertEquals(57, integerResult.result);
    }

    @Test
    public void testBad() {
        assertThrows(AdapterException.class, () -> shoehorn(new DoubleAdder3()).into(IntegerAdder.class).build());
        assertThrows(AdapterException.class, () -> shoehorn(new DoubleAdder4()).into(IntegerAdder.class).build());
    }
}
