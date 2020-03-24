![square pegs in round holes](https://raw.githubusercontent.com/shadow-invoke/assets/master/shoehorn-java/shoehorn-logo-120.png)
# Shoehorn

![Java CI](https://github.com/shoehorn-adapter/shoehorn-java/workflows/Java%20CI/badge.svg) [![codecov](https://codecov.io/gh/shoehorn-adapter/shoehorn-java/branch/master/graph/badge.svg)](https://codecov.io/gh/shoehorn-adapter/shoehorn-java)

Ad-hoc, fluent adapters for Java. Shoehorn any instance into any other interface or class, regardless of inheritance and with no 
custom code other than argument converters (which [Mapstruct](https://mapstruct.org/) can do for you).

## Installation

Using Maven and [JitPack](https://jitpack.io/)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
...
    <dependency>
      <groupId>com.github.shadow-invoke</groupId>
      <artifactId>shoehorn-java</artifactId>
      <version>0.1.0</version>
    </dependency>
```

## Usage

```java
GasOven gasOven = new GasOven();
WoodOven woodOven = shoehorn(gasOven)
        .into(WoodOven.class)
        .routing(
                method("cook")
                        .to("bake")
                        .consuming(
                                convert(Dough.class)
                                        .to(DoughDTO.class)
                                        .with(DoughConverter.INSTANCE),
                                convert(Topping[].class)
                                        .to(String[].class)
                                        .with(ToppingsConverter.INSTANCE)
                        )
                        .producing(
                                convert(PizzaDTO.class)
                                        .to(Pizza.class)
                                        .with(PizzaDTOConverter.INSTANCE)
                        )
        )
        .build();
PizzaDTO baked = gasOven.bake(new DoughDTO("LARGE"), new String[]{"PEPPERONI"});
Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
System.out.println(expected.equals(cooked)); // true
```

See [this](./src/test/java/org/shoehorn/TestFluently.java) unit test for the complete example.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)