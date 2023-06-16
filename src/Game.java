import gamestates.GameState;
import iomanagement.IJSONable;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        System.out.println("Welcome to the \"Vodičský, technický, občianský!\"");
        Scanner scanner = new Scanner(System.in);
        Customs customs = mainMenu(scanner);

        while (customs.isGameRunning()) {
            customs.sendNextVehicle();
            gameMenu(scanner, customs);
        }
        System.out.println("Ending the game");
    }

    private static Customs mainMenu(Scanner scanner) {
        while (true) {
            System.out.println(
                    "[1] New game\n" +
                    "[2] Load game\n" +
                    "[0] Exit\n");
            int choice = getChoice(scanner);
            switch (choice) {
                case 1:
                    return new Customs();
                case 2:
                    return loadGame(scanner);
                case 0:
                    System.out.println("Exiting, see you soon");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void gameMenu(Scanner scanner, Customs customs) {
        while ((customs.isGameRunning() || !customs.isEndOfDay()) && customs.getGameState() != GameState.FINISHED) {
            System.out.println(
                    "Choose what to do\n" +
                    "[1] Check vehicle or passenger\n" +
                    "[2] Check documents\n" +
                    "[3] Check weights\n" +
                    "[4] Buy upgrade\n" +
                    "[5] Show budget\n" +
                    "[6] List banned countries\n" +
                    "[7] Send next vehicle\n" +
                    "[8] Select mismatched data\n" +
                    "[9] Pause game\n");
            int choice = getChoice(scanner);

            switch (choice) {
                case 1 -> checkVehicleOrPersonMenu(scanner, customs);
                case 2 -> checkDocumentsMenu(scanner, customs);
                case 3 -> checkWeightMenu(scanner, customs);
                case 4 -> customs.upgradeMenu(scanner);
                case 5 -> customs.showBudget();
                case 6 -> customs.listBannedCountries();
                case 7 -> {
                    return;  //customs.sendNextVehicle();
                }
                case 8 -> selectMismatchMenu(scanner, customs);
                case 9 -> pauseMenu(scanner, customs);
                default -> System.out.println("Invalid choice");
            }
        }

        if (customs.isEndOfDay()) {
            customs.endDay();
        }

        while (customs.isEndOfDay()) {
            System.out.print("Start new day? [Yes/No]: ");
//            scanner.reset();
            String start = scanner.nextLine();
            if (start.equalsIgnoreCase("yes")) {
                customs.setDayDifficulty(customs.getCurrentDay() + 1);
//                customs.setGameState(GameState.RUNNING);
                break;
            } else if (start.equalsIgnoreCase("no")) {
                return;
            }
        }
    }

    private static void checkVehicleOrPersonMenu(Scanner scanner, Customs customs) {
        int choice;
        while (customs.isGameRunning() || !customs.isEndOfDay()) {
            System.out.println("[1] Check vehicle\n" +
                               "[2] Check driver\n" +
                               "[3] Check passengers\n" +
                               "[4] Check specific passenger\n" +
                               "[0] Return\n");

            choice = getChoice(scanner);
            switch (choice) {
                case 1:
                    customs.checkVehicle();
                    break;
                case 2:
                    customs.checkDriver();
                    break;
                case 3:
                    customs.checkPassengers();
                    break;
                case 4:
                    customs.checkPassenger(scanner);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void checkDocumentsMenu(Scanner scanner, Customs customs) {
        int choice;
        while (customs.isGameRunning() || !customs.isEndOfDay()) {
            System.out.println("[1] Check vehicle registration\n" +
                               "[2] Check driver's passport\n" +
                               "[3] Check passengers passports\n" +
                               "[4] Check specific passenger's passport\n" +
                               "[0] Return\n");
            choice = getChoice(scanner);
            switch (choice) {
                case 1:
                    customs.checkVehicleRegistration();
                    break;
                case 2:
                    customs.checkDriverPassport();
                    break;
                case 3:
                    customs.checkPassengersPassports();
                    break;
                case 4:
                    customs.checkPassengersPassport(scanner);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void checkWeightMenu(Scanner scanner, Customs customs) {
        int choice;
        while (customs.isGameRunning() || !customs.isEndOfDay()) {
            System.out.println("[1] Check driver's weight\n" +
                               "[2] Check passenger's weight\n" +
                               "[0] Return\n");
            choice = getChoice(scanner);
            switch (choice) {
                case 1:
                    customs.checkDriversWeight();
                    break;
                case 2:
                    customs.checkPassengersWeight(scanner);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void selectMismatchMenu(Scanner scanner, Customs customs) {
        while (customs.isGameRunning() || !customs.isEndOfDay()) {
            System.out.println("Select passenger\n" +
                               "[1] Driver\n" +
                               "[2] Passenger\n" +
                               "[0] Return\n");
            int personChoice = getChoice(scanner);
            switch (personChoice) {
                case 1:
                    //driver vracia true ak detained aby sa poslalo dalsie auto
                    if (customs.driverMismatchMenu(scanner)) {
                        return;
                    }
                    break;
                case 2:
                    //passenger nic nevracia
                    customs.passengerMismatchMenu(scanner);
                    break;
                case 0:
                    return;
            }
        }
    }

    private static Customs loadGame(Scanner scanner) {
        File folder = new File("saves");
        File[] saves = folder.listFiles();
        Customs customs = new Customs();

        if (saves.length == 0) {
            System.out.println("The saves folder is empty");
            System.out.println("Starting a new game\n");
            return customs;
        }

        for (int i = 0; i < saves.length; i++) {
            String filename = saves[i].getName();
            System.out.printf("[%d] %s\n", i, filename.substring(0, filename.length() - 5));
        }

        int choice;
        while (true) {
            choice = getChoice(scanner);
            if (choice >= 0 && choice < saves.length) {
                break;
            }
            System.out.println("Invalid choice");
        }

        String savename =  saves[choice].getPath();
        File f = new File(savename);
        try {
            if (!f.createNewFile()) {
                String content = Files.readString(Path.of(savename));
                JSONObject json = new JSONObject(content);
                customs.unmarshal(json);
            } else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Unable to locate the save");
        }
        return customs;
    }

    private static void pauseMenu(Scanner scanner, IJSONable saveObject) {
        ((Customs)saveObject).pauseTimer();
        while (true) {
            System.out.println(
                    "Game paused\n" +
                    "[1] Continue\n" +
                    "[2] Save game\n" +
                    "[3] Exit without saving\n");
            int choice = getChoice(scanner);
            switch (choice) {
                case 1:
                    ((Customs)saveObject).unpauseTimer();
                    return;
                case 2:
                    System.out.print("Enter the save name: ");
                    String name = scanner.nextLine();
                    JSONObject json = saveObject.marshal();

                    String savename = String.format("saves/%s.json", name);
                    File f = new File(savename);
                    try {
                        if (f.createNewFile()) {
                            FileWriter fw = new FileWriter(f);
                            fw.write(json.toString(1));
                            fw.close();
                            System.out.println("Successfully saved");
                        } else {
                            System.out.println("Save with such name already exists");
                            return;
                        }
                    } catch (Exception e) {
                        System.out.println("Unable to locate the save");
                    }
                    break;
                case 3:
                    System.out.print("Are you sure? [Yes/No]: ");
                    String exitChoice = scanner.nextLine();
                    if (exitChoice.equalsIgnoreCase("yes")) {
                        ((Customs)saveObject).setGameState(GameState.FINISHED);
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid choice");
            }

        }

    }

    /** Reads input from user
     * @param scanner
     * @return user input
     * @return -1 if user doesn't input valid number*/
    public static int getChoice(Scanner scanner) {
        System.out.print("Choice: ");
        String choice;
        int numChoice;
        try {
            if (scanner.hasNextLine()) {
                choice = scanner.nextLine();
                numChoice = Integer.parseInt(choice);
            } else {
                return 0;
            }
        } catch (NumberFormatException e) {
            return -1;
        }

        System.out.println();
        return numChoice;
    }
}
