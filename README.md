![square pegs in round holes](https://raw.githubusercontent.com/shadow-invoke/assets/master/shoehorn-java/shoehorn-logo-120.png)
# Shoehorn

![Java CI](https://github.com/shadow-invoke/shoehorn-java/workflows/Java%20CI/badge.svg) [![codecov](https://codecov.io/gh/shadow-invoke/shoehorn-java/branch/master/graph/badge.svg)](https://codecov.io/gh/shadow-invoke/shoehorn-java) [![Maven Central](https://img.shields.io/maven-central/v/io.shadowstack/shoehorn-java.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.shadowstack%22%20AND%20a:%22shoehorn-java%22)

Shoehorn lets you implement an [adapter](https://en.wikipedia.org/wiki/Adapter_pattern) from one interface or class to 
another with minimal code, even if both classes are defined in external libraries. Aside from a few well-placed 
annotations (or an invocation of the fluent API if the classes can't be modified), the only custom code required are 
converters for input and output types, which a library like [Mapstruct](https://mapstruct.org/) can do for you.

### Installation

Maven Central:

```xml
<dependency>
  <groupId>io.shadowstack</groupId>
  <artifactId>shoehorn-java</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Usage

```java
ExternalInterface theirs = shoehorn(new AdaptedClass()).into(ExternalInterface.class).build();
```

### For The Impatient

Start a new project, pull in the library, then copy and run [this](#full-example-code) class.

### The problem

Suppose we want to use an external library which consumes an implementation of its interface for purchasing stocks 
(using [Lombok](https://projectlombok.org/) for brevity).

```java
public interface TheirStockBuyer {
    PurchaseResponse purchase(PurchaseRequest request);
}
```
```java
@AllArgsConstructor @Data @ToString @NoArgsConstructor
public class PurchaseResponse {
    private Double units;
    private Double price;
}
```
```java
@AllArgsConstructor @Data
public class PurchaseRequest {
    private String ticker;
    private Double spend;
}
```

We already have our own implementation of a stock purchaser which contains complex, well-tested logic 
(imagine with me here).

```java
public class OurStockBuyer {
    public Confirmation fulfill(Order order) {
        Double bought = order.getSpend()/order.getStrike();
        String orderId = UUID.randomUUID().toString();
        return new Confirmation(order.getTicker(), bought, order.getStrike(), orderId);
    }
}
```
```java
@AllArgsConstructor @Data
public class Confirmation {
    private String ticker;
    private Double units;
    private Double price;
    private String id;
}
```
```java
@AllArgsConstructor @Data @NoArgsConstructor
public class Order {
    private String ticker;
    private Double spend;
    private Double strike;
}
```

We'd like to use our home-grown stock buyer with this external library, and that calls for an Adapter. 

### The Solution

To create an Adapter from our exposed interface, *TheirStockBuyer*, to our adapted class, *OurStockBuyer*, Shoehorn 
first needs two converters. We can use a combination of Shoehorn's *ArgumentConverter* interface and Mapstruct to 
quickly wire up both. First we convert from the inputs of the exposed interface, *TheirStockBuyer*, to the inputs of 
the adapted class, *OurStockBuyer*.

```java
@Mapper
public interface PurchaseRequest2Order extends ArgumentConverter<PurchaseRequest, Order> {
    public static ArgumentConverter<PurchaseRequest, Order> INSTANCE = Mappers.getMapper(PurchaseRequest2Order.class);
    @Mapping(target = "strike", expression = "java(from.getSpend()/10.0D)") // gives us 10 units every time
    Order convert(PurchaseRequest from) throws AdapterException;
    @Mapping(target = "strike", expression = "java(from.getSpend()/10.0D)")
    void convert(PurchaseRequest from, @MappingTarget Order to) throws AdapterException;
}
```

Next we convert from the output type of the adapted class, *OurStockBuyer*, to the output type of the exposed interface, 
*TheirStockBuyer*. Notice that we're going in the opposite direction with this conversion. That's because the actual 
result will come from the adapted class, whereas the actual arguments came from the exposed interface.

```java
@Mapper
public interface Confirmation2PurchaseResponse extends ArgumentConverter<Confirmation, PurchaseResponse> {
    public static ArgumentConverter<Confirmation, PurchaseResponse> INSTANCE = Mappers.getMapper(Confirmation2PurchaseResponse.class);
    PurchaseResponse convert(Confirmation from) throws AdapterException;
    void convert(Confirmation from, @MappingTarget PurchaseResponse to) throws AdapterException;
}
```

With these converters in place, adapting a method in *OurStockBuyer* to mimic one in *TheirStockBuyer* is very 
straightforward. We just add a couple of annotations to our class and we're done!

```java
public static class OurStockBuyer {
    @Mimic(type = TheirStockBuyer.class, method = "purchase")
    @Out(to = PurchaseResponse.class, with = Confirmation2PurchaseResponse.class)
    public Confirmation fulfill(@In(from = PurchaseRequest.class, with = PurchaseRequest2Order.class) Order order) {
        Double bought = order.getSpend()/order.getStrike();
        String orderId = UUID.randomUUID().toString();
        return new Confirmation(order.getTicker(), bought, order.getStrike(), orderId);
    }
}
```

Now whenever we need an instance of *TheirStockBuyer*, backed by an instance of *OurStockBuyer*, we construct and use it 
like so:

```java
TheirStockBuyer theirBuyer = shoehorn(new OurStockBuyer()).into(TheirStockBuyer.class).build();
System.out.println(theirBuyer.purchase(new PurchaseRequest("FOO", 100.0D)));
```

Can't modify *OurStockBuyer*? No problem! Just use the fluent API instead of annotations:

```java
theirBuyer = shoehorn(new OurStockBuyer())
                .into(TheirStockBuyer.class)
                .routing(
                    method("purchase")
                    .to("fulfill")
                    .consuming(
                        convert(PurchaseRequest.class)
                            .to(Order.class)
                            .using(PurchaseRequest2Order.INSTANCE)
                    )
                    .producing(
                        convert(Confirmation.class)
                            .to(PurchaseResponse.class)
                            .using(Confirmation2PurchaseResponse.INSTANCE)
                    )
                )
                .build();
```

See the [unit tests](./src/test/java/io/shadowstack/) for more examples.

### Full Example Code

```java
public class Example {
    @AllArgsConstructor @Data @ToString @NoArgsConstructor
    public static class PurchaseResponse {
        private Double units;
        private Double price;
    }
    @AllArgsConstructor @Data
    public static class PurchaseRequest {
        private String ticker;
        private Double spend;
    }
    public static interface TheirStockBuyer {
        PurchaseResponse purchase(PurchaseRequest request);
    }
    public static class OurStockBuyer {
        @Mimic(type = TheirStockBuyer.class, method = "purchase")
        @Out(to = PurchaseResponse.class, with = Confirmation2PurchaseResponse.class)
        public Confirmation fulfill(@In(from = PurchaseRequest.class, with = PurchaseRequest2Order.class) Order order) {
            Double bought = order.getSpend()/order.getStrike();
            String orderId = UUID.randomUUID().toString();
            return new Confirmation(order.getTicker(), bought, order.getStrike(), orderId);
        }
    }
    @AllArgsConstructor @Data
    public static class Confirmation {
        private String ticker;
        private Double units;
        private Double price;
        private String id;
    }
    @AllArgsConstructor @Data @NoArgsConstructor
    public static class Order {
        private String ticker;
        private Double spend;
        private Double strike;
    }
    @Mapper
    public static interface PurchaseRequest2Order extends ArgumentConverter<PurchaseRequest, Order> {
        public static ArgumentConverter<PurchaseRequest, Order> INSTANCE = Mappers.getMapper(PurchaseRequest2Order.class);
        @Mapping(target = "strike", expression = "java(from.getSpend()/10.0D)")
        Order convert(PurchaseRequest from) throws AdapterException;
        @Mapping(target = "strike", expression = "java(from.getSpend()/10.0D)")
        void convert(PurchaseRequest from, @MappingTarget Order to) throws AdapterException;
    }
    @Mapper
    public static interface Confirmation2PurchaseResponse extends ArgumentConverter<Confirmation, PurchaseResponse> {
        public static ArgumentConverter<Confirmation, PurchaseResponse> INSTANCE = Mappers.getMapper(Confirmation2PurchaseResponse.class);
        PurchaseResponse convert(Confirmation from) throws AdapterException;
        void convert(Confirmation from, @MappingTarget PurchaseResponse to) throws AdapterException;
    }

    public static void main(String[] args) {
        try {
            TheirStockBuyer theirBuyer = shoehorn(new OurStockBuyer()).into(TheirStockBuyer.class).build();
            System.out.println(theirBuyer.purchase(new PurchaseRequest("FOO", 100.0D)));

            theirBuyer = shoehorn(new OurStockBuyer())
                            .into(TheirStockBuyer.class)
                            .routing(
                                method("purchase")
                                .to("fulfill")
                                .consuming(
                                    convert(PurchaseRequest.class)
                                        .to(Order.class)
                                        .using(PurchaseRequest2Order.INSTANCE)
                                )
                                .producing(
                                    convert(Confirmation.class)
                                        .to(PurchaseResponse.class)
                                        .using(Confirmation2PurchaseResponse.INSTANCE)
                                )
                            )
                            .build();
            System.out.println(theirBuyer.purchase(new PurchaseRequest("FOO", 1000.0D)));
        } catch (AdapterException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
```

### Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

[MIT](https://choosealicense.com/licenses/mit/)