package items;

import generator.Generator;
import org.json.JSONObject;

import java.time.LocalDate;

/** Class acting as a base for other licenses which can act as an item in inventory*/
public abstract class License implements ITem {
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private String issueCountry;

    /** Constructor for class License
     * @param issueDate date when the license was issued
     * @param expirationDate date when the license will expire
     * @param issueCountry name of a country the license was issued in*/
    public License(LocalDate issueDate, LocalDate expirationDate, String issueCountry) {
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.issueCountry = issueCountry;
    }

    /** A default constructor*/
    public License() {
        super();
    }

    /** Returns issue date
     * @return issue date*/
    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    /** Returns expiration date
     * @return expiration date*/
    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    /** Returns issue country
     * @return issue country*/
    public String getIssueCountry() {
        return this.issueCountry;
    }

    @Override
    public double getWeight() {
        return 0.1;
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("issueCountry", this.issueCountry);
        json.put("issueDate", Generator.normalizeDate(this.issueDate));
        json.put("expirationDate", Generator.normalizeDate(this.expirationDate));
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.issueCountry = json.getString("issueCountry");
        String[] date = json.getString("issueDate").split("\\.");
        this.issueDate = LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
        date = json.getString("expirationDate").split("\\.");
        this.expirationDate = LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
    }
}
