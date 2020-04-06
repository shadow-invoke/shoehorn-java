package io.shadowstack;

import org.junit.jupiter.api.Test;

import static io.shadowstack.Fluently.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSimple {
    public static class Kitten {}
    public static class Mouse {}
    public static class Cat {
        @SuppressWarnings({"UnusedReturnValue", "unused"})
        public Kitten feedAndBreedLikeACat(Mouse food) {
            System.out.println("Feed on a mouse");
            return new Kitten();
        }
    }
    public static class Egg {}
    public static class Worm {}
    public static class Bird {
        @SuppressWarnings("unused")
        public Egg feedAndBreedLikeABird(Worm food) {
            System.out.println("Feed on a worm");
            return new Egg();
        }
    }

    @Test
    public void TestFeedAndBreed() throws AdapterException, NoSuchMethodException {
        Cat inDisguise = new Cat();
        Bird butNotReally =
                shoehorn(inDisguise)
                    .into(Bird.class)
                    .routing(
                        method(
                            reference(Bird.class)
                                .from(
                                    (bird -> bird.feedAndBreedLikeABird(null)) // pass whatever
                                )
                        )
                        .to(
                            reference(Cat.class)
                                .from(
                                    (cat -> cat.feedAndBreedLikeACat(null)) // pass whatever
                                )
                        )
                        .consuming(
                            convert(Worm.class)
                                .to(Mouse.class)
                                .using(
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
                                .using(
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
                    )
                    .build();
        Egg fromAKittenWat = butNotReally.feedAndBreedLikeABird(new Worm());
        assertNotNull(fromAKittenWat);
    }
}
