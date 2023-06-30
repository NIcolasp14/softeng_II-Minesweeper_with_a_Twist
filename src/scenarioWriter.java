// java imports
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// javafx imports
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;

/**
 * Class that contains the object scenarioWriter, which
 * writes a txt file with the given data.
 * */
public class scenarioWriter {


    /**
     * Writes a user-given scenario in a txt file. <br>
     * Field 1: file ID <br>
     * Field 2: Difficulty Level:: [1] or [2] <br>
     * Field 3: Number of Mines:: [1]->[9-11], 2->[35-45] <br>
     * Field 4: Available Time:: [1]->[120-180], [2]->[240-360] <br>
     * Field 5: Superbomb:: [1]->[0](==No), [2]->[1](==Yes)
     * */
  public static void writeScenarioFile() throws Exception {

        // Create a grid pane to hold the input fields
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create the four input fields
        TextField field0 = new TextField();
        TextField field1 = new TextField();
        TextField field2 = new TextField();
        TextField field3 = new TextField();
        TextField field4 = new TextField();

        // Add the input fields to the grid pane
        gridPane.add(new Label("Scenario File ID:"), 0, 0);
        gridPane.add(field0, 1, 0);
        gridPane.add(new Label("Level (1 or 2):"), 0, 1);
        gridPane.add(field1, 1, 1);
        gridPane.add(new Label("No of Mines (1->[9-11], 2->[35-45]):"), 0, 2);
        gridPane.add(field2, 1, 2);
        gridPane.add(new Label("Available Time (1->[120-180], 2->[240-360]):"), 0, 3);
        gridPane.add(field3, 1, 3);
        gridPane.add(new Label("Superbomb (1->0==No, 2->1==Yes):"), 0, 4);
        gridPane.add(field4, 1, 4);

        // Create the OK and Cancel buttons
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Enter Five Game Attributes");
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
        dialog.getDialogPane().setContent(gridPane);

        // Wait for the user to enter values and click OK or Cancel
        // If OK, we write the given values in a file.
        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButton) {
                // Get the values entered by the user
                String value0 = field0.getText();
                String value1 = field1.getText();
                String value2 = field2.getText();
                String value3 = field3.getText();
                String value4 = field4.getText();

                // Monitor the values
                System.out.println("Value 0: " + value0);
                System.out.println("Value 1: " + value1);
                System.out.println("Value 2: " + value2);
                System.out.println("Value 3: " + value3);
                System.out.println("Value 4: " + value4);

                try {
                    FileWriter fw = new FileWriter("./scenarios/SCENARIO-" + value0 + ".txt");
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(value1);
                    bw.newLine();
                    bw.write(value2);
                    bw.newLine();
                    bw.write(value3);
                    bw.newLine();
                    bw.write(value4);

                    bw.close();
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return buttonType;

        });

        // Show the dialog box
        dialog.showAndWait();

    }
}
