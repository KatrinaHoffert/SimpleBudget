package katrinahoffert.simplebudget.util;

/** Simple functional programming style command pattern classes. Classes created as needed. */
public class Functional {
    /** Represents a function that takes in nothing and returns nothing. */
    public static abstract class Action {
        public abstract void action();
    }

    /** Represents a function that takes in a single parameter and returns nothing. */
    public static abstract class Action1<T> {
        public abstract void action(T string);
    }
}
