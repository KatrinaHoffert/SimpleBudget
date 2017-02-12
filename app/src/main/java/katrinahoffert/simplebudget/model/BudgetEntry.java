package katrinahoffert.simplebudget.model;

public class BudgetEntry {
    public String category;
    public int amount;
    public String date;

    @Override
    public String toString() {
        return "{category: " + category + ", amount: " + amount + ", date: " + date + "}";
    }
}
