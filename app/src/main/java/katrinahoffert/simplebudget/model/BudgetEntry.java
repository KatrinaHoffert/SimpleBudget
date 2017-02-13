package katrinahoffert.simplebudget.model;

public class BudgetEntry {
    public int _id;
    public String category;
    public int amount;
    public String date;

    @Override
    public String toString() {
        return "{_id: " + _id + ", category: " + category + ", amount: " + amount + ", date: " + date + "}";
    }
}
