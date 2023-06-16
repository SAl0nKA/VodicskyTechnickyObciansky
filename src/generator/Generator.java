package generator;

import iomanagement.IJSONable;
import items.Drug;
import items.Gun;
import items.ITem;
import items.Passport;
import items.VehicleRegistration;
import people.Driver;
import people.Passenger;
import vehicles.Bicycle;
import vehicles.Bus;
import vehicles.Car;
import vehicles.Vehicle;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** Class for generating random values from loaded file and generating random vehicles with drivers, passengers,
 * passports and contraband*/
public class Generator implements IJSONable {
    private Random rand;
    private Map<String, ArrayList<String>> data;

    /** A default constructor
     * @throws IOException if source file was not found*/
    public Generator() throws IOException {
        this.rand = new Random(System.currentTimeMillis());
        this.loadData();
    }

    /** Loads data from a file
     * @throws IOException if source file was not found*/
    public void loadData() throws IOException {
        File f = new File("resources/data.json");
        if (!f.createNewFile()) {
            String content = Files.readString(Path.of("resources/data.json"));
            JSONObject json = new JSONObject(content);
            this.unmarshal(json);
        }
    }

    /** Saves data into a file*/
    public void saveData() {
        JSONObject json = this.marshal();
        try {
            FileWriter writer = new FileWriter("resources/data.json");
            writer.write(json.toString(3));
            writer.close();
        } catch (IOException e) {
            System.out.println("Saving failed");
        }
    }

    /** Returns a string representation of a date in format DD.MM.YYYY
     * @return string representation of a date in format DD.MM.YYYY
     * @param date date to be formatted into a string*/
    public static String normalizeDate(LocalDate date) {
        String[] splitDate = date.toString().split("-");
        String[] reverseDate = new String[]{splitDate[2], splitDate[1], splitDate[0]};
        return String.join(".", reverseDate);
    }

    /** Returns a date from a string
     * @return date from a string
     * @param date string representation of a date in format DD.MM.YYYY*/
    public static LocalDate unnormalizeDate(String date) {
        String[] splitDate = date.split("\\.");
        return LocalDate.of(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[0]));
    }

    /** Returns new vehicle with driver and passengers
     * @return new vehicle with driver and passengers
     * @param mismatchChance chance to generete wrong data
     * @param contrabandChance chance to generate contraband*/
    public Vehicle generateVehicle(double mismatchChance, double contrabandChance) {
        //rovnaka issue country
        String issueCountry = this.getRandomCountry();
        Driver driver = this.generateDriver(issueCountry, mismatchChance);
        Vehicle vehicle = null;

        String registrationNumber = this.generateRandomRegistrationNumber();
        String color = this.getRandomVehicleColor();
        int numOfPassengers = 0;

        double vehicleChance = this.rand.nextDouble();
        //5% sanca ze pride autobus
        if (vehicleChance < 0.05) {
            String brand = this.getRandomCarBrand();
            vehicle = new Bus(registrationNumber, color, brand, this.rand.nextBoolean() ? 4 : 6, 40);
            numOfPassengers = this.rand.nextInt(vehicle.getMaxNumberOfPassengers() - 30);
        //65% sanca ze pride auto
        } else if (vehicleChance < 0.7) {
            String brand = this.getRandomCarBrand();
            vehicle = new Car(registrationNumber, color, brand, 4, 4);
            numOfPassengers = this.rand.nextInt(vehicle.getMaxNumberOfPassengers() + 3);
        //30% sanca ze pride bicykel
        } else {
            String brand = this.getRandomBicycleBrand();
            vehicle = new Bicycle(registrationNumber, color, brand, 2, this.rand.nextInt(2) + 1);
            numOfPassengers = this.rand.nextInt(vehicle.getMaxNumberOfPassengers() + 2);
        }

        //do vozidla sa vlozi vodic
        vehicle.setDriver(driver);
        for (int i = 0; i < numOfPassengers; i++) {
            vehicle.addPassenger(this.generatePassenger(issueCountry, mismatchChance));
        }
        //vygeneruju sa datumy
        LocalDate[] dates = this.generateIssueAndExpirationDates();
        VehicleRegistration registration = new VehicleRegistration(dates[0], dates[1], issueCountry, vehicle);
        //sanca ze bude nieco zle vo vehicle registration
        driver.setVehicleRegistration(this.generateVehicleRegistrationMismatch(registration, mismatchChance));

        //sanca ze sa vygeneruje contraband v kufri vozidla
        if (this.generateChance(contrabandChance)) {
            if (vehicle instanceof Car car) {
                car.addItemToInventory(this.generateContraband());
            } else if (vehicle instanceof Bus bus) {
                bus.addItemToInventory(this.generateContraband());
            }
        }

        return vehicle;
    }

    private Driver generateDriver(String issueCountry, double mismatchChance) {
        return (Driver)this.generatePerson("driver", issueCountry, mismatchChance);
    }

    private Passenger generatePassenger(String issueCountry, double mismatchChance) {
        return this.generatePerson("passenger", issueCountry, mismatchChance);
    }

    private Passenger generatePerson(String type, String issueCountry, double mismatchChance) {
        LocalDate birthday = this.generateRandomBirthday();
        //odreze sa pohlavie
        String genderName = this.getRandomName();
        String name = genderName.substring(1);
        String surname = this.getRandomSurname();
        String gender = this.getGenderFromName(genderName);
        String hairColor = this.getRandomHairColor();
        String eyeColor = this.getRandomEyeColor();
        int weight = this.rand.nextInt(70) + 50;
        Passenger passenger;
        if (type.equals("driver")) {
            passenger = new Driver(birthday, name, surname, gender, hairColor, eyeColor, weight);
        } else {
            passenger = new Passenger(birthday, name, surname, gender, hairColor, eyeColor, weight);
        }
        LocalDate[] dates = this.generateIssueAndExpirationDates();
        //sanca ze nedostane passport
        if (!this.generateChance(mismatchChance)) {
            Passport passport = new Passport(dates[0], dates[1], issueCountry, passenger);
            //sanca ze passport bude mat v sebe nieco zle
            passenger.addToInventory(this.generatePassportMismatch(passport, mismatchChance));
        }

        return passenger;
    }

    private Passport generatePassportMismatch(Passport passport, double mismatchChance) {
        //ziadna zmena
        if (!this.generateChance(mismatchChance)) {
            return passport;
        }
        double split = this.rand.nextDouble();
        if (split < 0.33) {
            //reverses gender
            passport.setGender(passport.getGender().equals("MALE") ? "FEMALE" : "MALE");
        } else if (split < 0.67) {
            passport.setHairColor(this.getRandomHairColor());
        } else {
            passport.setEyeColor(this.getRandomEyeColor());
        }
        return passport;
    }

    private VehicleRegistration generateVehicleRegistrationMismatch(VehicleRegistration registration, double mismatchChance) {
        //ziadna zmena
        if (!this.generateChance(mismatchChance)) {
            return registration;
        }
        double split = this.rand.nextDouble();
        if (split <= 0.25) {
            //reverses gender
            registration.setRegistrationNumber(this.generateRandomRegistrationNumber());
        } else if (split <= 0.5) {
            registration.setBrand(this.getRandomCarBrand());
        } else if (split <= 0.75) {
            registration.setColor(this.getRandomVehicleColor());
        } else {
            registration.setNumberOfWheels(this.rand.nextInt(3) + 5);
        }
        return registration;
    }

    //generuje issue a expiration date
    /** First issue date, second expiration date */
    private LocalDate[] generateIssueAndExpirationDates() {
        //generovat sa bude od dnesneho datumu 6 rokov dozadu
        LocalDate startDate = LocalDate.now().minusYears(6);
        //end date pre issue = localdate.now
        long issueDateEpoch = this.rand.nextLong(startDate.toEpochDay(), LocalDate.now().toEpochDay());
        LocalDate issueDate = LocalDate.ofEpochDay(issueDateEpoch);
        //expiration date je 4 roky od issue date
        LocalDate expirationDate = issueDate.plusYears(4);
        return new LocalDate[]{issueDate, expirationDate};
    }

    private LocalDate generateRandomBirthday() {
        //minimalne 16 rokov, maximalne 50
        LocalDate startDate = LocalDate.now().minusYears(50);
        LocalDate endDate = LocalDate.now().minusYears(16);

        long birthdayEpoch = this.rand.nextLong(startDate.toEpochDay(), endDate.toEpochDay());
        return LocalDate.ofEpochDay(birthdayEpoch);
    }

    private String generateRandomRegistrationNumber() {
        return Long.toHexString(this.rand.nextLong(0xEFFFFFFFL) + 0x10000000L);
    }

    /** Returns true if generated value is less or equal to provided number
     * @return true if generated value is less or equal to provided number
     * @param chance number in range (0,1> to determine percentual chance*/
    public boolean generateChance(double chance) {
        return this.rand.nextDouble() <= chance;
    }

    private ITem generateContraband() {
        if (this.generateChance(0.5)) {
            int ammo = this.rand.nextInt(200);
            return new Gun(this.getRandomGunType(), ammo);
        } else {
            double ammount = this.rand.nextDouble(10) + 1;
            return new Drug(this.getRandomDrug(), ammount);
        }
    }

    /** Returns random first name
     * @return random first name*/
    public String getRandomName() {
        return this.data.get("names").get(this.rand.nextInt(this.data.get("names").size()));
    }

    private String getGenderFromName(String name) {
        return name.toCharArray()[0] == 'M' ? "MALE" : "FEMALE";
    }

    /** Returns random last name
     * @return random last name*/
    public String getRandomSurname() {
        return this.data.get("surnames").get(this.rand.nextInt(this.data.get("surnames").size()));
    }

    /** Returns random hair color
     * @return random hair color*/
    public String getRandomHairColor() {
        return this.data.get("hairColors").get(this.rand.nextInt(this.data.get("hairColors").size()));
    }

    /** Returns random eye color
     * @return random eye color*/
    public String getRandomEyeColor() {
        return this.data.get("eyeColors").get(this.rand.nextInt(this.data.get("eyeColors").size()));
    }

    /** Returns random car brand
     * @return random car brand*/
    public String getRandomCarBrand() {
        return this.data.get("carBrands").get(this.rand.nextInt(this.data.get("carBrands").size()));
    }

    /** Returns random gun type
     * @return random gun type*/
    public String getRandomGunType() {
        return this.data.get("guns").get(this.rand.nextInt(this.data.get("guns").size()));
    }

    /** Returns random drug name
     * @return random drug name*/
    public String getRandomDrug() {
        return this.data.get("drugs").get(this.rand.nextInt(this.data.get("drugs").size()));
    }

    /** Returns random drug name
     * @return random drug name*/
    public String getRandomVehicleColor() {
        return this.data.get("vehicleColors").get(this.rand.nextInt(this.data.get("vehicleColors").size()));
    }

    /** Returns random bicycle brand
     * @return random bicycle brand*/
    public String getRandomBicycleBrand() {
        return this.data.get("bicycleBrands").get(this.rand.nextInt(this.data.get("bicycleBrands").size()));
    }

    /** Returns random country name
     * @return random country name*/
    public String getRandomCountry() {
        return this.data.get("countries").get(this.rand.nextInt(this.data.get("countries").size()));
    }

    /** Returns list of country names
     * @return list of country names*/
    public ArrayList<String> getCountries() {
        return new ArrayList<>(this.data.get("countries"));
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        for (String key : this.data.keySet()) {
            json.put(key, this.data.get(key));
        }
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.data = new HashMap<>();
        for (String key : json.keySet()) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray(key).length(); i++) {
                list.add(json.getJSONArray(key).getString(i));
            }
            this.data.put(key, list);
        }
    }
}
