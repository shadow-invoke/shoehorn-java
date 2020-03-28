![square pegs in round holes](https://raw.githubusercontent.com/shadow-invoke/assets/master/shoehorn-java/shoehorn-logo-120.png)
# Shoehorn

![Java CI](https://github.com/shadow-invoke/shoehorn-java/workflows/Java%20CI/badge.svg) [![codecov](https://codecov.io/gh/shadow-invoke/shoehorn-java/branch/master/graph/badge.svg)](https://codecov.io/gh/shoehorn-adapter/shoehorn-java)

Ad-hoc, fluent adapters for Java. Shoehorn any instance into any other interface or class, regardless of inheritance and with no 
custom code other than argument converters (which [Mapstruct](https://mapstruct.org/) can do for you).

## Installation

Maven Central:

```xml
<dependency>
  <groupId>io.shadowstack</groupId>
  <artifactId>shoehorn-java</artifactId>
  <version>0.2.0</version>
</dependency>
```

## Usage

```java
public class Shoehorn {
    public static class Kitten {}
    public static class Mouse {}
    public static class Cat {
        public Kitten feedAndBreedLikeACat(Mouse food) {
            System.out.println("Feed on a mouse");
            return new Kitten();
        }
    }
    public static class Egg {}
    public static class Worm {}
    public static class Bird {
        public Egg feedAndBreedLikeABird(Worm food) {
            System.out.println("Feed on a worm");
            return new Egg();
        }
    }
    public static void main(String[] args) throws AdapterException, NoSuchMethodException {
        Cat inDisguise = new Cat();
        Bird butNotReally =
            shoehorn(inDisguise)
                .into(Bird.class)
                .routing(
                    method("feedAndBreedLikeABird")
                        .to("feedAndBreedLikeACat")
                        .before((inputs, instance, result) -> {
                            System.out.println("Pre-call hook");
                            return null;
                        })
                        .consuming(
                            convert(Worm.class)
                                .to(Mouse.class)
                                .with(
                                    new ArgumentConverter<Worm, Mouse>() {
                                        @Override
                                        public Mouse convert(Worm from) throws AdapterException {
                                            return new Mouse();
                                        }

                                        @Override
                                        public void convert(Worm from, Mouse to) throws AdapterException {}
                                    }
                                )
                        )
                        .producing(
                            convert(Kitten.class)
                                .to(Egg.class)
                                .with(
                                    new ArgumentConverter<Kitten, Egg>() {
                                        @Override
                                        public Egg convert(Kitten from) throws AdapterException {
                                            return new Egg();
                                        }

                                        @Override
                                        public void convert(Kitten from, Egg to) throws AdapterException {}
                                    }
                                )
                        )
                        .after((inputs, instance, result) -> {
                            System.out.println("Post-call hook");
                            return null;
                        })
                )
                .build();
        Egg fromAKittenWat = butNotReally.feedAndBreedLikeABird(new Worm());
        System.out.println("Breed an " + fromAKittenWat.getClass().getSimpleName());
    }
}
```

See [this](./src/test/java/org/shoehorn/TestFluently.java) unit test for a more interesting example.

## Why in the world would I want this?

[Adapters](https://en.wikipedia.org/wiki/Adapter_pattern) are a frequently used construct, often requiring an inordinate 
amount of custom, throw-away code. This library arose out of my need to generate these on the fly for classes over which 
I had little, if any, control. The canonical use case is an external library A which consumes an implementation of class 
or interface B. External library C exposes a class D which does not implement B but does logically the same thing. We 
would like to inject an instance of D into A, but D has different inputs and outputs than B. Here we need an adapter 
from B to D, perhaps requiring complex type conversions. A library like [Mapstruct](https://mapstruct.org/) can do the 
type conversions for us, but what about "converting" the interfaces themselves? This is where Shoehorn comes in, which 
aims to be like Mapstruct for interfaces.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)