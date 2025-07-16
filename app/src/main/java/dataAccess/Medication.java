package dataAccess;

public class Medication extends UserData {
    private String name;
    private String amount;
    private int dailyFrequency;

    public Medication() {}

    public Medication(String title, String description, String name, String amount, int dailyFrequency) {
        super(title, description);
        this.name = name;
        this.amount = amount;
        this.dailyFrequency = dailyFrequency;
    }

    public Medication(String title, String description, String filePath, String name, String amount, int dailyFrequency) {
        super(title, description, filePath);
        this.name = name;
        this.amount = amount;
        this.dailyFrequency = dailyFrequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }
}
