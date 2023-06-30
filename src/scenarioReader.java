// java imports
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

// javafx imports
import javafx.scene.control.Alert;
import javafx.stage.Stage;


     /** Handling scenario description error (file doesn't contain exactly 4 lines)*/
     class InvalidDescriptionException extends Exception {
         /**
          * The method handling scenario description error (file doesn't contain exactly 4 lines),
          * prints a message to the console. In the scenario reader, an informative pop-up also pops
          * up.
          * */
        public InvalidDescriptionException(String message) {
            super(message);
        }
     }

    /**Handling scenario value error (one of the four values isn't within the permitted range)*/
    class InvalidValueException extends Exception {
        /**
         * The method handling scenario value error (one of the four values isn't within the permitted range),
         * prints a message to the console. In the scenario reader, an informative pop-up also pops
         * up.
         * */
        public InvalidValueException(String message) {
            super(message);
        }
    }

/**
 * Reads scenario and handles io, file reading, value and description errors in the scenario. <br>
 * The user just writes the scenario ID in the text field of a little window. <br>
 * Field 1: Scenario ID
 * */
public class scenarioReader {
    /** Difficulty Level, 1 for easy, 2 for hard */
    public static int firstLine;
    /** Total number of bombs, for difficulty level 1 -> [9-11], for difficulty level 2 -> [35-45].*/
    public static int secondLine;
    /** Max available time, for difficulty level 1 -> [120-180], for difficulty level 2 -> [240-360].*/
    public static int thirdLine;
    /** Super bomb, 0 for super bomb exclusion, 1 for super bomb inclusion*/
    public static boolean fourthLine;
    /** Grid dimensions, for difficulty level 1 -> 9x9, for difficulty level 2 -> 16x16.*/
    public static int fifthValueGrid;

    // Note: Permitted value ranges can easily be modified.


    /**
     * Reads a user-given scenario in a txt file. <br>
     * Line 1: Difficulty Level <br>
     * Line 2: Total number of bombs <br>
     * Line 3: Max available time <br>
     * Line 4: Super bomb <br>
     * */
    public static void readScenarioFile(int scenarioID) throws IOException, InvalidDescriptionException, InvalidValueException {
        try {
            // Read the file
            Main.scenarioReaderUsed = true; // if file is successfully read we use its contents
            File file = new File("./scenarios/SCENARIO-"+ scenarioID +".txt"); // file to read
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                // Parse the contents of the file
                firstLine = Integer.parseInt(reader.readLine());
                secondLine = Integer.parseInt(reader.readLine());
                thirdLine = Integer.parseInt(reader.readLine());
                fourthLine = Boolean.parseBoolean(reader.readLine());

                // Check if there are more lines in the file
                if (reader.readLine() != null) {
                    throw new InvalidDescriptionException("Extra lines in scenario file.");
                }

                // Validate the contents of the file
                if (firstLine == 1) {
                    fifthValueGrid = 9;
                    if (!(secondLine >= 9 && secondLine <= 11) || !(thirdLine >= 120 && thirdLine <= 180)) {
                        Main.scenarioReaderUsed = false;
                        throw new InvalidValueException("Invalid value in scenario file.");
                    }
                } else if (firstLine == 2) {
                    fifthValueGrid = 16;
                    if (!(secondLine >= 35 && secondLine <= 45) || !(thirdLine >= 240 && thirdLine <= 360)) {
                        Main.scenarioReaderUsed = false;
                        throw new InvalidValueException("Invalid value in scenario file.");
                    }
                } else {
                    Main.scenarioReaderUsed = false;
                    throw new InvalidValueException("Invalid value in scenario file.");
                }

                // print validated values to the console
                System.out.println("ID: " + scenarioID);
                System.out.println("First line: " + firstLine);
                System.out.println("Second line: " + secondLine);
                System.out.println("Third line: " + thirdLine);
                System.out.println("Fourth line: " + fourthLine);
            }
            else{
                // Troubleshooted the case of trying to read a non-existent file, without having previously used LOAD->PLAY with existent files.
                // I observed that the scenarioReader, when used first with existent files, handles the exception of non-existent files as
                // expected. But if a non-existent value is requested first, then an infinite loop of timeouts
                // commences. Thus, I put this else block to handle the exception and reload the game with its previous attributes.
                Main.scenarioReaderUsed = false; // Reload with previously selected game attributes

                // Show informative pop-up and wait for it to close before reloading
                Alert loadError = new Alert(Alert.AlertType.ERROR);
                ((Stage) loadError.getDialogPane().getScene().getWindow()).getIcons();
                loadError.setTitle("Load Error");
                loadError.setHeaderText("Scenario Reading Failed");
                loadError.setContentText("Non-existent file, the game will reload with its previously selected attributes.");
                loadError.showAndWait();
            }
        } catch (IOException e) {
            // Handle file reading errors
            Main.scenarioReaderUsed = false; // Reload with previously selected game attributes

            // Show informative pop-up about the error and wait for it to close before reloading
            Alert loadError = new Alert(Alert.AlertType.ERROR);
            ((Stage) loadError.getDialogPane().getScene().getWindow()).getIcons();
            loadError.setTitle("Load Error");
            loadError.setHeaderText("Scenario Reading Failed");
            loadError.setContentText("File Reading Error");
            loadError.showAndWait();

            e.printStackTrace(); // print a stack trace to the console

        } catch (InvalidValueException e) {
            // Handle invalid file contents
            Main.scenarioReaderUsed = false; // Reload with previously selected game attributes

            // Show informative pop-up about the error  and wait for it to close before reloading
            Alert loadError = new Alert(Alert.AlertType.ERROR);
            ((Stage) loadError.getDialogPane().getScene().getWindow()).getIcons();
            loadError.setTitle("Load Error");
            loadError.setHeaderText("Scenario Reading Failed");
            loadError.setContentText("Invalid Value Exception");
            loadError.showAndWait(); // print a stack trace to the console

            e.printStackTrace(); // print a stack trace to the console

        } catch (InvalidDescriptionException e) {
            // Handle invalid file description
            Main.scenarioReaderUsed = false; // Reload with previously selected game attributes

            // Show informative pop-up about the error  and wait for it to close before reloading
            Alert loadError = new Alert(Alert.AlertType.ERROR);
            ((Stage) loadError.getDialogPane().getScene().getWindow()).getIcons();
            loadError.setTitle("Load Error");
            loadError.setHeaderText("Scenario Reading Failed");
            loadError.setContentText("Invalid Description Exception");
            loadError.showAndWait();

            e.printStackTrace(); // print a stack trace to the console

        } catch (Exception e) {
            // Handle all other errors
            Main.scenarioReaderUsed = false; // Reload with previously selected game attributes

            // Show informative pop-up about the error  and wait for it to close before reloading
            Alert loadError = new Alert(Alert.AlertType.ERROR);
            ((Stage) loadError.getDialogPane().getScene().getWindow()).getIcons();
            loadError.setTitle("Load Error");
            loadError.setHeaderText("Scenario Reading Failed");
            loadError.setContentText("Unknown Error");
            loadError.showAndWait();

            e.printStackTrace(); // print a stack trace to the console
        }

    }
}
