package items;

import org.json.JSONObject;
import people.Passenger;
import java.time.LocalDate;
import generator.Generator;

/** Class representing passenger's passport and can be added to inventory*/
public class Passport extends License {
    private LocalDate birthday;
    private String name;
    private String surname;
    private String gender;
    private String hairColor;
    private String eyeColor;
    private double weight;

    /** Constructor for class Passport
     * @param issueDate issue date
     * @param expirationDate expiration date
     * @param issueCountry issue country
     * @param passenger owner of the passport*/
    public Passport(LocalDate issueDate, LocalDate expirationDate, String issueCountry, Passenger passenger) {
        super(issueDate, expirationDate, issueCountry);
        this.birthday = passenger.getBirthday();
        this.name = passenger.getName();
        this.surname = passenger.getSurname();
        this.gender = passenger.getGender();
        this.hairColor = passenger.getHairColor();
        this.eyeColor = passenger.getEyeColor();
        this.weight = passenger.getWeight();
    }

    /** A default constructor*/
    public Passport() {
        super();
    }

    /** Returns name
     * @return name*/
    public String getName() {
        return this.name;
    }

    /** Returns gender
     * @return gender*/
    public String getGender() {
        return this.gender;
    }

    /** Sets gender
     * @param gender gender*/
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** Returns hair color
     * @return hair color*/
    public String getHairColor() {
        return this.hairColor;
    }

    /** Sets hair color
     * @param hairColor hair color*/
    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    /** Returns eye color
     * @return eye color*/
    public String getEyeColor() {
        return this.eyeColor;
    }

    /** Sets eye color
     * @param eyeColor eye color*/
    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    @Override
    public String getDescription() {
        return String.format(
                "Passport information\n" +
                "[1] Birthday: %s\n" +
                "[2] Name: %s\n" +
                "[3] Surname: %s\n" +
                "[4] Gender: %s\n" +
                "[5] Hair color: %s\n" +
                "[6] Eye color: %s\n" +
                "[7] Weight: %.2f\n" +
                "[8] Issue date: %s\n" +
                "[9] Expiration date: %s\n" +
                "[10] Issue country: %s\n",
                Generator.normalizeDate(this.birthday), this.name, this.surname, this.gender, this.hairColor, this.eyeColor, this.weight,
                Generator.normalizeDate(this.getIssueDate()), Generator.normalizeDate(this.getExpirationDate()), this.getIssueCountry());
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("type", "Passport");
        json.put("birthday", Generator.normalizeDate(this.birthday));
        json.put("name", this.name);
        json.put("surname", this.surname);
        json.put("gender", this.gender);
        json.put("hairColor", this.hairColor);
        json.put("eyeColor", this.eyeColor);
        json.put("weight", this.weight);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.birthday = Generator.unnormalizeDate(json.getString("birthday"));
        this.name = json.getString("name");
        this.surname = json.getString("surname");
        this.gender = json.getString("gender");
        this.hairColor = json.getString("hairColor");
        this.eyeColor = json.getString("eyeColor");
        this.weight = json.getInt("weight");
    }
}
