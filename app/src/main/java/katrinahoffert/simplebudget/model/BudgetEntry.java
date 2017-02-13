package katrinahoffert.simplebudget.model;

public class BudgetEntry {
    public int _id;
    public String category;
    public int category_id;
    public int amount;
    public String date;

    @Override
    public String toString() {
        return "{_id: " + _id + ", category: " + category + ", category_id: " + category_id + ", amount: " + amount + ", date: " + date + "}";
    }
}
