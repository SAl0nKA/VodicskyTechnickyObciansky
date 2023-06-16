import buildings.Building;
import buildings.DetainedVehicles;
import buildings.Gulag;
import countries.Country;
import countries.Relationship;
import gamestates.GameState;
import generator.Generator;
import iomanagement.IJSONable;

import items.Drug;
import items.Gun;
import items.Passport;
import items.VehicleRegistration;
import items.ITem;
import org.json.JSONArray;
import buildings.DogHouse;
import people.Driver;
import people.Passenger;
import vehicles.Bicycle;
import vehicles.Bus;
import vehicles.Car;
import vehicles.Vehicle;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/** Class representing border control. It's purpose is to contain buildings,
 * check current vehicle, find any mismatches in licenses, find contraband and detain passengers if some offense was found/*/
public class Customs implements IJSONable {
    //gametime 10 minut
    private static final long GAME_TIME = 10 * 60;
    private double mismatchChance = 0.1;
    private double contrabandChance = 0.2;
    private final double chanceForSuccessfulSearch = 0.4;

    private ArrayList<Building> buildings;
    private Generator generator;
    private Vehicle currentVehicle;
    private int budget;
    private int currentDay;

    private Timer timer;
    private long timerStartTime;
    private long timeLeft;
    private GameState gameState;

    private ArrayList<Country> countries;

    /** A default constructor*/
    public Customs() {
        this.buildings = new ArrayList<>();
        this.buildings.add(new DetainedVehicles());
        this.buildings.add(new Gulag());
        this.currentDay = 1;
        this.countries = new ArrayList<>();
        try {
            this.generator = new Generator();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.setDayDifficulty(this.currentDay);
    }

    private void startTimer(long time) {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Customs.this.gameState = GameState.END_OF_DAY;
                System.out.println("\nEnd of the day");
            }
        }, this.timeLeft * 1000);
        this.timerStartTime = LocalDate.now().toEpochSecond(LocalTime.now(), ZoneOffset.MIN);
        this.gameState = GameState.RUNNING;
    }

    /** Pauses game timer*/
    public void pauseTimer() {
        this.gameState = GameState.PAUSED;
        this.timer.cancel();
        long timerStopTime = LocalDate.now().toEpochSecond(LocalTime.now(), ZoneOffset.MIN);
        this.timeLeft = GAME_TIME - (timerStopTime - this.timerStartTime);
    }

    /** Unpauses game timer*/
    public void unpauseTimer() {
        this.startTimer(this.timeLeft);
    }

    private Gulag getGulag() {
        for (Building building : this.buildings) {
            if (building instanceof Gulag gulag) {
                return gulag;
            }
        }
        return null;
    }

    private DetainedVehicles getDetainedVehicles() {
        for (Building building : this.buildings) {
            if (building instanceof DetainedVehicles detainedVehicles) {
                return detainedVehicles;
            }
        }
        return null;
    }

    private DogHouse getDogHouse() {
        for (Building building : this.buildings) {
            if (building instanceof DogHouse dogHouse) {
                return dogHouse;
            }
        }
        return null;
    }

    /** Returns current day
     * @return current day*/
    public int getCurrentDay() {
        return this.currentDay;
    }

    /** Display current budget*/
    public void showBudget() {
        System.out.printf("You currently have %d money\n", this.budget);
    }

    /** Returns true if game state is RUNNING
     * @return true if game state is RUNNING*/
    public boolean isGameRunning() {
        return this.gameState.equals(GameState.RUNNING);
    }

    /** Returns true if game state is END_OF_DAY
     * @return true if game state is END_OF_DAY*/
    public boolean isEndOfDay() {
        return this.gameState.equals(GameState.END_OF_DAY);
    }

    /** Returns current game state
     * @return current game state*/
    public GameState getGameState() {
        return this.gameState;
    }

    /** sets game state t o provided value
     * @param gameState game state*/
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /** Shows building upgrade menu
     * @param scanner scanner */
    public void upgradeMenu(Scanner scanner) {
        while (this.isGameRunning()) {
            System.out.printf("What do you want to upgrade?\n" +
                    "[1] Buy dog house\n" +
                    "[2] Upgrade dog house\n" +
                    "[3] Upgrade parking lot\n" +
                    "[4] Upgrade gulag\n" +
                    "[0] Return \n");
            int choice = Game.getChoice(scanner);
            switch (choice) {
                case 1 -> {
                    int dogHousePrice = DogHouse.DOG_HOUSE_PRICE;
                    if (this.getDogHouse() != null) {
                        System.out.println("You already own a dog house");
                        return;
                    }
                    if (this.budget < dogHousePrice) {
                        System.out.printf("You don't have enough money, the dog house costs %d money\n", dogHousePrice);
                        return;
                    }
                    this.budget -= dogHousePrice;
                    DogHouse dogHouse = new DogHouse();
                    this.buildings.add(dogHouse);
                    System.out.printf("You successfully bought the dog house for %d money.\n" +
                            "The daily operational expenses are %d\n", dogHousePrice, dogHouse.getDailyOperationPrice());
                }
                case 2 -> {
                    DogHouse dogHouse = this.getDogHouse();
                    if (dogHouse == null) {
                        System.out.println("You don't own a dog house yet");
                        return;
                    }

                    int upgradePrice = dogHouse.getNextUpgradePrice();
                    if (upgradePrice == 0) {
                        System.out.println("Dog house is already maxed out");
                        return;
                    }
                    if (!dogHouse.upgrade(this.budget)) {
                        return;
                    }
                    this.budget -= upgradePrice;
                }
                case 3 -> {
                    DetainedVehicles detainedVehicles = this.getDetainedVehicles();
                    int upgradePrice = detainedVehicles.getNextUpgradePrice();
                    if (!detainedVehicles.upgrade(this.budget)) {
                        return;
                    }
                    this.budget -= upgradePrice;
                }
                case 4 -> {
                    Gulag gulag = this.getGulag();
                    int upgradePrice = gulag.getNextUpgradePrice();
                    if (upgradePrice == 0) {
                        System.out.println("Gulag is already maxed out");
                        return;
                    }
                    if (!gulag.upgrade(this.budget)) {
                        return;
                    }
                    this.budget -= upgradePrice;
                }
                case 0 -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid choice");
                }
            }
        }
    }

    /** Sends next vehicle*/
    public void sendNextVehicle() {
        System.out.println("Here comes the next vehicle");
        this.currentVehicle = this.generator.generateVehicle(this.mismatchChance, this.contrabandChance);
    }

    /** Shows description of a current vehicle*/
    public void checkVehicle() {
        System.out.println("You look at the vehicle");
        //polymorfizmus
        System.out.println(this.currentVehicle.getVehicleDescription());
    }

    /** Shows description of a current driver*/
    public void checkDriver() {
        System.out.println("You look at the driver");
        System.out.println(this.currentVehicle.getDriver().getDescription());
    }

    /** Shows information about vehicle registration*/
    public void checkVehicleRegistration() {
        VehicleRegistration registration = this.currentVehicle.getDriver().getVehicleRegistration();
        if (registration == null) {
            System.out.println("Driver doesn't have any vehicle registration");
        } else {
            System.out.println(registration.getDescription());
        }
    }

    /** Shows information about driver;s passport*/
    public void checkDriverPassport() {
        Driver driver = this.currentVehicle.getDriver();
        Passport passport = this.searchForPassport(driver);
        if (passport == null) {
            System.out.println("Driver doesn't have a passport");
        } else {
            System.out.println("Driver's passport");
            System.out.println(passport.getDescription());
        }
    }

    private Passport searchForPassport(Passenger passenger) {
        for (ITem item : passenger.getInventory()) {
            if (item instanceof Passport passport) {
                return passport;
            }
        }
        return null;
    }

    /** Shows descriptions of all passengers*/
    public void checkPassengers() {
        if (this.currentVehicle.getPassengers().size() == 0) {
            System.out.println("There are no passengers");
            return;
        }
        System.out.println("You look at the passengers");
        for (int i = 0; i < this.currentVehicle.getPassengers().size(); i++) {
            System.out.printf("   [%d] ", i);
            System.out.println(this.currentVehicle.getPassengers().get(i).getDescription());
        }
    }

    private Passenger getPassenger(Scanner scanner) {
        int index;
        System.out.print("Passengers number: ");
        try {
            String choice = scanner.nextLine();
            index = Integer.parseInt(choice);
        } catch (NumberFormatException e) {
            index = -1;
        }

        if (index < 0 || index >= this.currentVehicle.getPassengers().size()) {
            System.out.println("There is no such passenger");
            return null;
        }
        return this.currentVehicle.getPassengers().get(index);
    }

    /** Shows description of selected passenger
     * @param scanner scanner*/
    public void checkPassenger(Scanner scanner) {
        Passenger passenger = this.getPassenger(scanner);
        if (passenger == null) {
            return;
        }
        System.out.println("You look at the passenger");
        System.out.println(passenger.getDescription());
    }

    /** Shows information about all passports passengers have*/
    public void checkPassengersPassports() {
        if (this.currentVehicle.getPassengers().size() == 0) {
            System.out.println("There are no passengers");
            return;
        }

        System.out.println("Passengers passports");
        ArrayList<Passenger> passengers = this.currentVehicle.getPassengers();
        for (int i = 0; i < passengers.size(); i++) {
            System.out.printf("   [%d] ", i);
            Passport passport = this.searchForPassport(passengers.get(i));
            System.out.println(passport == null ? "Passenger doesn't have a passport" : passport.getDescription());
        }
    }

    /** Shows driver's weight including inventory*/
    public void checkDriversWeight() {
        double weight = this.getWeightOfPersonsInventory(this.currentVehicle.getDriver());
        System.out.printf("Driver's weight: %.2f kgs\n", weight);
    }

    /** Shows selected passenger's weight including inventory
     * @param scanner scanner*/
    public void checkPassengersWeight(Scanner scanner) {
        if (this.currentVehicle.getPassengers().size() == 0) {
            System.out.println("There are no passengers");
            return;
        }
        Passenger passenger = this.getPassenger(scanner);
        if (passenger == null) {
            return;
        }
        double weight = this.getWeightOfPersonsInventory(passenger);
        System.out.printf("Passenger's weight: %.2f kgs\n", weight);
    }

    private double getWeightOfPersonsInventory(Passenger passenger) {
        //polymorfizmus
        double weight = passenger.getWeight();
        for (ITem item : passenger.getInventory()) {
            weight += item.getWeight();
        }
        return weight;
    }

    /** Shows information about selected passenger's passport
     * @param scanner scanner*/
    public void checkPassengersPassport(Scanner scanner) {
        Passenger passenger = this.getPassenger(scanner);
        if (passenger == null) {
            return;
        }
        Passport passport = this.searchForPassport(passenger);
        if (passport == null) {
            System.out.println("Passenger doesn't have a passport");
        } else {
            System.out.println(passport.getDescription());
        }
    }

    /** Shows menu for selecting mismatched data for a driver
     * @param scanner scanner*/
    public boolean driverMismatchMenu(Scanner scanner) {
        Driver driver = this.currentVehicle.getDriver();
        if (driver == null) {
            System.out.println("Driver is no longer in the vehicle");
            return false;
        }
        while (this.isGameRunning()) {
            System.out.println("Choose what to check\n" +
                               "[1] Passport\n" +
                               "[2] Vehicle registration\n" +
                               "[3] Inventory\n" +
                               "[0] Return;\n");
            int checkChoice = Game.getChoice(scanner);
            switch (checkChoice) {
                //[1] Passport
                case 1:
                    if (this.passportMismatchMenu(driver, this.searchForPassport(driver), scanner)) {
                        return true;
                    }
                    break;
                //[2] Vehicle registratio
                case 2:
                    if (this.vehicleRegistrationMismatchMenu(scanner)) {
                        return true;
                    }
                    break;
                //[3] Inventory
                case 3:
                    ITem item = this.checkPassengerInventory(driver);
                    if (item == null) {
                        System.out.println("You didn't find anything");
                        break;
                    }
                    //polymorfizmus
                    System.out.printf("You found %s\n", item.getDescription());
                    this.getDogHouse().addItem(item);
                    driver.setAccused(true);
                    this.detainMenu(scanner);
                    return true;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice");
            }
        }
        return false;
    }

    private boolean vehicleRegistrationMismatchMenu(Scanner scanner) {
        Driver driver = this.currentVehicle.getDriver();
        VehicleRegistration vehicleRegistration = driver.getVehicleRegistration();
        System.out.println("Select what is wrong\n" +
                "[1] Date expiration\n" +
                "[2] Banned country\n" +
                "[3] Wrong registration data\n" +
                "[4] Passengers over capacity\n" +
                "[0] Return\n");
        int mismatchOption = Game.getChoice(scanner);
        switch (mismatchOption) {
            //[1] Date expiration
            case 1:
                if (!vehicleRegistration.getExpirationDate().isBefore(LocalDate.now())) {
                    System.out.println("The vehicle registration is still valid");
                    return false;
                }
                break;
            //[2] Banned country
            case 2:
                for (Country country : this.countries) {
                    if (country.getName().equals(vehicleRegistration.getIssueCountry()) && country.getRelationship() == Relationship.ENEMY) {
                        driver.setAccused(true);
                        return this.detainMenu(scanner);
                    }
                }
                System.out.println("Country doesn't have denied entry");
                return false;
            //[3] Wrong license data
            case 3:
                if (!this.selectIncorrectRegistrationDataMenu(scanner, vehicleRegistration)) {
                    return false;
                }
                break;
            //[4] Passengers over capacity
            case 4:
                if (this.currentVehicle.getPassengers().size() + 1 <= this.currentVehicle.getDriver().getVehicleRegistration().getMaxNumberOfPassengers()) {
                    System.out.println("The number of passengers haven't exceeded the limit");
                    return false;
                }
                break;
            case 0:
                return false;
            default:
                System.out.println("Invalid choice");
                return false;
        }
        driver.setAccused(true);
        return this.detainMenu(scanner);
    }

    /** Shows menu for selecting mismatched data for a passenger
     * @param scanner scanner*/
    public void passengerMismatchMenu(Scanner scanner) {
        Passenger passenger = this.getPassenger(scanner);
        if (passenger == null) {
            return;
        }

        while (this.isGameRunning()) {
            System.out.println("Choose what to check\n" +
                    "[1] Passport\n" +
                    "[2] Inventory\n" +
                    "[0] Return;\n");
            int checkChoice = Game.getChoice(scanner);
            switch (checkChoice) {
                //[1] Passport
                case 1:
                    this.passportMismatchMenu(passenger, this.searchForPassport(passenger), scanner);
                    break;
                //[2] Inventory
                case 2:
                    ITem item = this.checkPassengerInventory(passenger);
                    if (item == null) {
                        System.out.println("You didn't find anything");
                        break;
                    }
                    //polymorfizmus
                    System.out.printf("You found %s\n", item.getDescription());
                    this.getDogHouse().addItem(item);
                    passenger.setAccused(true);
                    this.detainMenu(scanner);
                    return;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private boolean passportMismatchMenu(Passenger passenger, Passport passport, Scanner scanner) {
        System.out.println("Select what is wrong\n" +
                           "[1] Date expiration\n" +
                           "[2] Banned country\n" +
                           "[3] Wrong license data\n" +
                           "[4] Too young\n" +
                           "[5] Missing passport\n" +
                           "[0] Return\n");
        int mismatchOption = Game.getChoice(scanner);
        switch (mismatchOption) {
            //[1] Date expiration
            case 1:
                if (passport == null)  {
                    System.out.println("Passenger doesn't have a passport");
                    return false;
                }
                if (!passport.getExpirationDate().isBefore(LocalDate.now())) {
                    System.out.println("The passport is still valid");
                    return false;
                }
                break;
            //[2] Banned country
            case 2:
                if (passport == null)  {
                    System.out.println("Passenger doesn't have a passport");
                    return false;
                }
                for (Country country : this.countries) {
                    if (country.getName().equals(passport.getIssueCountry()) && country.getRelationship() == Relationship.ENEMY) {
                        passenger.setAccused(true);
                        this.detainMenu(scanner);
                        return true;
                    }
                }
                System.out.println("Country doesn't have denied entry");
                return false;
            //[3] Wrong passport data
            case 3:
                if (passport == null)  {
                    System.out.println("Passenger doesn't have a passport");
                    return false;
                }
                if (!this.selectIncorrectPassportDataMenu(scanner, passport, passenger)) {
                    return false;
                }
                break;
            //[4] Too young
            case 4:
                if (!(passenger instanceof Driver)) {
                    System.out.println("The person isn't a driver");
                    return false;
                }
                if (passenger.getAge() > 18) {
                    System.out.println("The person is old enough to drive");
                    return false;
                }
                break;
            //[5] Missing passport
            case 5:
                if (passport != null) {
                    System.out.println("The person does have a passport");
                    return false;
                }
                break;
            case 0:
                return false;
            default:
                System.out.println("Not applicable");
                return false;
        }
        passenger.setAccused(true);
        return this.detainMenu(scanner);
    }

    private ITem checkPassengerInventory(Passenger passenger) {
        for (int i = 0; i < passenger.getInventory().size(); i++) {
            ITem item = passenger.getInventory().get(i);
            if (item instanceof Gun || item instanceof Drug) {
                double successfulSearch = this.chanceForSuccessfulSearch;
                for (Building building : this.buildings) {
                    if (building instanceof DogHouse dogHouse) {
                        successfulSearch += dogHouse.getChanceForSuccessfulSearch();
                    }
                }

                //ak sa search podari odoberie sa item
                if (this.generator.generateChance(successfulSearch)) {
                    return passenger.removeFromInventory(i);
                }
            }
        }
        return null;
    }

    private boolean selectIncorrectPassportDataMenu(Scanner scanner, Passport passport, Passenger passenger) {
        System.out.println("Select which data is incorrect\n" +
                           "[1] Gender\n" +
                           "[2] Hair color\n" +
                           "[3] Eye color\n" +
                           "[0] Return");

        int choice = Game.getChoice(scanner);
        switch (choice) {
            //[1] Gender
            case 1:
                if (passport.getGender().equals(passport.getGender())) {
                    System.out.println("Gender is correct");
                    return false;
                }
                break;
            //[2] Hair color
            case 2:
                if (passport.getHairColor().equals(passport.getHairColor())) {
                    System.out.println("Hair color is correct");
                    return false;
                }
                break;
            //[3] Eye color
            case 3:
                if (passport.getEyeColor().equals(passport.getEyeColor())) {
                    System.out.println("Eye color is correct");
                    return false;
                }
                break;
            case 0:
                return false;
            default:
                System.out.println("Invalid choice");
                return false;
        }
        return true;
    }

    private boolean selectIncorrectRegistrationDataMenu(Scanner scanner, VehicleRegistration vehicleRegistration) {
        System.out.println("Select which data is incorrect\n" +
                           "[1] Regisration number\n" +
                           "[2] Brand\n" +
                           "[3] Color\n" +
                           "[4] Number of wheels\n" +
                           "[0] Return");
        int choice = Game.getChoice(scanner);

        switch (choice) {
            //[1] Regisration number
            case 1:
                if (vehicleRegistration.getRegistrationNumber().equals(this.currentVehicle.getRegistrationNumber())) {
                    System.out.println("Registration number is correct");
                    return false;
                }
                break;
            //[2] Brand
            case 2:
                if (vehicleRegistration.getBrand().equals(this.currentVehicle.getBrand())) {
                    System.out.println("Vehicle brand is correct");
                    return false;
                }
                break;
            //[3] Color
            case 3:
                if (vehicleRegistration.getColor().equals(this.currentVehicle.getColor())) {
                    System.out.println("Vehicle color is correct");
                    return false;
                }
                break;
            //[4] Number of wheels
            case 4:
                if (vehicleRegistration.getNumberOfWheels() == this.currentVehicle.getNumberOfWheels()) {
                    System.out.println("Number of wheels is correct");
                    return false;
                }
                break;
            case 0:
                return false;
            default:
                System.out.println("Invalid choice");
                return false;
        }
        return true;
    }

    private boolean detainMenu(Scanner scanner) {
        this.budget += 50;
        System.out.println("You found a mismatch and get a reward. What to do with the illegal entrant");
        while (true) {
            System.out.println(
                    "[1] Detain driver\n" +
                    "[2] Detain passenger\n" +
                    "[3] Detain vehicle\n" +
                    "[0] Return\n");
            int choice = Game.getChoice(scanner);
            switch (choice) {
                case 1:
                    this.detainDriver();
                    break;
                case 2:
                    this.detainPassenger(scanner);
                    break;
                case 3:
                    if (this.detainVehicle()) {
                        this.sendNextVehicle();
                        return true;
                    }
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private boolean detainDriver() {
        Driver driver = this.currentVehicle.getDriver();
        if (!driver.isAccused()) {
            System.out.println("Driver wasn't accused of anything");
            return false;
        }
        if (!this.getGulag().addPrisoner(driver)) {
            System.out.println("The gulag is full");
            return false;
        }
        this.currentVehicle.removeDriver();
        System.out.println("The driver has been successfully detained");
        System.out.println("You can now detain the vehicle");
        return true;
    }

    private boolean detainPassenger(Scanner scanner) {
        Passenger passenger = this.getPassenger(scanner);
        if (passenger == null) {
            return false;
        }
        if (!passenger.isAccused()) {
            System.out.println("Passenger wasn't accused of anything");
            return false;
        }
        if (!this.getGulag().addPrisoner(passenger)) {
            System.out.println("The gulag is full");
            return false;
        }

        if (!this.currentVehicle.removePassenger(passenger)) {
            System.out.println("Passenger isn't in the vehicle anymore");
            return false;
        }

        System.out.println("The passenger has been successfully detained");
        return true;
    }

    private boolean detainVehicle() {
        Vehicle vehicle = this.currentVehicle;

        if (this.currentVehicle.getDriver() != null) {
            if (!vehicle.getDriver().isAccused()) {
                System.out.println("Driver wasn't accused of anything, you can't detain the car");
                return false;
            }
        }
        if (!this.getDetainedVehicles().addVehicle(vehicle)) {
            System.out.println("Parking lot for detained vehicles is full");
            return false;
        }

        if (vehicle.getDriver() != null) {
            System.out.println("Driver is still in the vehicle");
            return false;
        }
        this.currentVehicle = null;
        return true;
    }

    /** Lists banned countries*/
    public void listBannedCountries() {
        int i = 1;
        for (Country country : this.countries) {
            if (country.getRelationship() != Relationship.ENEMY) {
                continue;
            }
            System.out.printf("[%d] %s\n", i, country.getName());
            i++;
        }
        if (i == 1) {
            System.out.println("There are no banned countries");
        }
    }

    /** Ends day and sells contents of buildings*/
    public void endDay() {
        int sum = 0;
        int operationCosts = 0;
        for (Building building : this.buildings) {
            sum += building.sellContent();
            operationCosts += building.getDailyOperationPrice();
        }
        System.out.printf("Today you've earned %d money\n", sum);
        System.out.printf("%d money will be substracted from your budget for building operational costs\n", operationCosts);
        this.budget -= operationCosts;
        System.out.printf("Your budget is %d\n", this.budget);
        System.out.println("Слава Фричке");
    }

    /** Generates new banned countries and increases chance for mismatch and contraband generation
     * @param day next day*/
    public void setDayDifficulty(int day) {
        this.currentDay = day;
        System.out.printf("Day %d\n", this.currentDay);
        this.countries.clear();
        for (String countryName : this.generator.getCountries()) {
            Country country = new Country(countryName);
            country.setRelationship(Relationship.NEUTRAL);
            this.countries.add(country);
        }

        switch (day) {
            case 1 -> {
                //all good
            }
            case 2 -> {
                for (Country country : this.countries) {
                    if (this.generator.generateChance(0.2)) {
                        country.setRelationship(Relationship.ENEMY);
                    }
                }
                this.mismatchChance = 0.2;
                this.contrabandChance = 0.3;
            }
            case 3 -> {
                for (Country country : this.countries) {
                    if (this.generator.generateChance(0.3)) {
                        country.setRelationship(Relationship.ENEMY);
                    }
                }
                this.mismatchChance = 0.35;
                this.contrabandChance = 0.40;
            }
            case 4 -> {
                for (Country country : this.countries) {
                    if (this.generator.generateChance(0.45)) {
                        country.setRelationship(Relationship.ENEMY);
                    }
                }
                this.mismatchChance = 0.40;
                this.contrabandChance = 0.45;
            }
            case 5 -> {
                for (Country country : this.countries) {
                    if (this.generator.generateChance(0.6)) {
                        country.setRelationship(Relationship.ENEMY);
                    }
                }
                this.mismatchChance = 0.5;
                this.contrabandChance = 0.55;
            }
            default -> {
                this.gameState = GameState.FINISHED;
                return;
            }
        }
        this.timeLeft = GAME_TIME;
        this.startTimer(this.timeLeft);
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        JSONArray buildingArr = new JSONArray();
        //polymorfizmus
        for (Building building : this.buildings) {
            buildingArr.put(building.marshal());
        }
        json.put("buildings", buildingArr);
        json.put("currentVehicle", this.currentVehicle.marshal());
        json.put("budget", this.budget);
        json.put("currentDay", this.currentDay);
        json.put("timerStartTime", this.timerStartTime);
        json.put("timeLeft", this.timeLeft);
        json.put("gameState", this.gameState.name());

        JSONArray countryArr = new JSONArray();
        for (Country country : this.countries) {
            countryArr.put(country.marshal());
        }
        json.put("countries", countryArr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.buildings = new ArrayList<>();
        for (Object obj : json.getJSONArray("buildings")) {
            JSONObject jsonBuilding = (JSONObject)obj;
            switch (jsonBuilding.getString("type")) {
                case "Gulag":
                    Gulag gulag = new Gulag();
                    gulag.unmarshal(jsonBuilding);
                    this.buildings.add(gulag);
                    break;
                case "DetainedVehicles":
                    DetainedVehicles detainedVehicles = new DetainedVehicles();
                    detainedVehicles.unmarshal(jsonBuilding);
                    this.buildings.add(detainedVehicles);
                    break;
                case "DogHouse":
                    DogHouse dogHouse = new DogHouse();
                    dogHouse.unmarshal(jsonBuilding);
                    this.buildings.add(dogHouse);
                    break;
            }
        }

        JSONObject jsonVehicle = json.getJSONObject("currentVehicle");
        switch (jsonVehicle.getString("type")) {
            case "Car" -> {
                Car car = new Car();
                car.unmarshal(jsonVehicle);
                this.currentVehicle = car;
            }
            case "Bus" -> {
                Bus bus = new Bus();
                bus.unmarshal(jsonVehicle);
                this.currentVehicle = bus;
            }
            case "Bicycle" -> {
                Bicycle bicycle = new Bicycle();
                bicycle.unmarshal(jsonVehicle);
                this.currentVehicle = bicycle;
            }
        }
        this.budget = json.getInt("budget");
        this.currentDay = json.getInt("currentDay");
        this.timerStartTime = json.getLong("timerStartTime");
        this.timeLeft = json.getLong("timeLeft");
        this.gameState = GameState.valueOf(json.getString("gameState"));

        this.countries = new ArrayList<>();
        for (Object obj : json.getJSONArray("countries")) {
            Country country = new Country();
            country.unmarshal((JSONObject)obj);
            this.countries.add(country);
        }

        this.setDayDifficulty(this.currentDay);
        this.startTimer(this.timeLeft);
    }
}
