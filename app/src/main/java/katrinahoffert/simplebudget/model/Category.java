package katrinahoffert.simplebudget.model;

public class Category {
    public int _id;
    public String category;

    @Override
    public String toString() {
        return "{_id: " + _id + ", category: " + category + "}";
    }
}
