// java imports
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// javafx imports
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;


/**
 * Main class launches and runs the application.
 * It controls the main window and also the flow
 * of the application: <br>
 * 1) Pause, Unpause <br>
 * 2) Reload <br>
 * 3) GameOver Due to Timeout <br>
 * 4) GameOver Due to Bomb <br>
 * 5) GameOver Due to Request of Solution <br>
 * 6) Win
 * */
public class Main extends Application {
    private static Label countdownLabel = new Label();
    private static Label bombsLeft = new Label();
    public static Label totalFlags = new Label();
    private static int bombPercent = 10; // default
    public static int remainingSeconds = 121; // default
    private static int remainingSecondsReload = 121; // default, this represents the total seconds of a game with the attr selected
    public static int gridSize = 10;
    public static boolean pressed = false;
    private static int availableTime = 120; // not used
    public static Tile[][] grid;
    private static Stage main;
    public static Scene scene;

    public static int scID;

    // for the Details Menu item
    static public List<Integer> totalBombas = new ArrayList<Integer>();
    static public List<Integer> totalLeftClicks = new ArrayList<Integer>();
    static public List<Integer> totalPlayingTime = new ArrayList<Integer>();
    static public List<String> gameWinner = new ArrayList<String>();

    private static VBox vbox = new VBox();

    static int numBombs, foundBombs, totalBombs = 10;

    private static int secondsPassed;

    public static SimpleIntegerProperty numberProperty;

    public static Timer timer;

    public static boolean gameOver = false;

    public static int y_super_bomb;

    public static int x_super_bomb;

    public static boolean super_bomb_flag = true;

    public static boolean s_b_flag = true;

    static Image mine = new Image("./images/mine.png");

    static Image submarine = new Image("./images/submarine.png");

    static Image seaMine = new Image("./images/sea_mine.png");

    static Image surrender = new Image("./images/surrender.png");

    Optional<String> scenarioID;

    static boolean sound = true;
    public static int [] xs;
    public static int [] ys;

    public static int [][] xys;

    public static int gamesPlayed = 0;

    static boolean lvl = true;

    static boolean size = true;

    static boolean time = true;

    public static boolean scenarioReaderUsed = false;

    public static boolean paused = false;

    public static Timeline timeline = new Timeline();

    public static String text="";

    public static String allRounds="";

    public static Button pauseButton;



    @Override
    public void start(Stage stage) {

        grid = new Tile[gridSize][gridSize];

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            }
        };

        timer = new Timer();

        timer.scheduleAtFixedRate(task, 1000, 1000);

        main = stage;

        main.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        main.getIcons().add(surrender);
        main.setTitle("Minesweeper with a Twist! - NP");

        MenuBar menuBar = new MenuBar();


        Menu menuFile = new Menu("App");
        MenuItem startButton = new MenuItem("Start");
        startButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white;");
        startButton.setOnAction(event -> {
            // Perform the necessary actions when the start button is pressed
            System.out.println("Start button pressed");
            try {
                scenarioReader.readScenarioFile(scID);
            } catch (IOException e) {
                System.out.println("Error reading scenario file: " + e.getMessage());
            } catch (InvalidDescriptionException | InvalidValueException e) {
                System.out.println("Error in scenario file: " + e.getMessage());
            }
            reload();
        });
        MenuItem Create = new MenuItem("Create");
        Create.setStyle("-fx-background-color: #6DD0E0; -fx-text-fill: white;");
        Create.setOnAction(e -> {
            try {
                scenarioWriter.writeScenarioFile();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        MenuItem Load = new MenuItem("Load");
        Load.setStyle("-fx-background-color: #E0A86D; -fx-text-fill: white;");
        Load.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Load Game Scenario");
            dialog.setHeaderText("Enter Game Scanerio ID:");
            dialog.setContentText("ID:");

            scenarioID = dialog.showAndWait();

            if (scenarioID.isPresent()) {
                int defaultValue = 0;
                scID = scenarioID.map(Integer::parseInt).orElse(defaultValue);
                //String ID = scenarioID.get();
                //int scID = scenarioID.map(Integer::parseInt);

            } else {
                // User cancelled the dialog

            }
        });


        MenuItem quit = new MenuItem("Exit");
        quit.setOnAction(e -> {
            Platform.exit();
        });
        menuFile.getItems().addAll(Create, Load, startButton, quit);

        Menu menuSize = new Menu("Size");
        MenuItem ten = new MenuItem("10x10");
        ten.setOnAction(e -> {
            scenarioReaderUsed = false;
            gridSize = 10;
            reload();
        });
        MenuItem fifteen = new MenuItem("15x15");
        fifteen.setOnAction(e -> {
            scenarioReaderUsed = false;
            gridSize = 15;
            reload();
        });
        MenuItem twenty = new MenuItem("20x20");
        twenty.setOnAction(e -> {
            scenarioReaderUsed = false;
            gridSize = 20;
            reload();
        });
        menuSize.getItems().addAll(ten, fifteen, twenty);

        Menu menuDifficulty = new Menu("Levels");
        MenuItem easy = new MenuItem("Easy - 10% Bombs");
        easy.setOnAction(e -> {
            scenarioReaderUsed = false;
            bombPercent = 10;
            reload();
        });
        MenuItem medium = new MenuItem("Medium - 15% Bombs");
        medium.setOnAction(e -> {
            scenarioReaderUsed = false;
            bombPercent = 15;
            reload();
        });
        MenuItem hard = new MenuItem("Hard - 20% Bombs");
        hard.setOnAction(e -> {
            scenarioReaderUsed = false;
            bombPercent = 20;
            reload();
        });
        menuDifficulty.getItems().addAll(easy, medium, hard);


        Menu Time = new Menu("Time");
        MenuItem secs_120 = new MenuItem("120 seconds");
        secs_120.setOnAction(e -> {
            scenarioReaderUsed = false;
            remainingSeconds = 121;
            remainingSecondsReload = 121;
            reload();
        });
        MenuItem secs_180 = new MenuItem("180 seconds");
        secs_180.setOnAction(e -> {
            scenarioReaderUsed = false;
            remainingSeconds = 181;
            remainingSecondsReload = 181;
            reload();
        });
        Time.getItems().addAll(secs_120, secs_180);

        Menu menuSound = new Menu("Sound");
        RadioMenuItem soundOn = new RadioMenuItem("On");
        soundOn.setOnAction(e -> {
            sound = true;
        });
        RadioMenuItem soundOff = new RadioMenuItem("Off");
        soundOff.setOnAction(e -> {
            sound = false;
        });
        ToggleGroup soundToggle = new ToggleGroup();
        soundToggle.getToggles().addAll(soundOn, soundOff);
        soundToggle.selectToggle(soundOn);

        menuSound.getItems().addAll(soundOn, soundOff);


        Menu Details = new Menu("Details");
        MenuItem Rounds = new MenuItem("Rounds");

        Rounds.setOnAction(e -> {
            Alert roundsLast5 = new Alert(AlertType.INFORMATION);
            ((Stage) roundsLast5.getDialogPane().getScene().getWindow()).getIcons();
            roundsLast5.setTitle("Rounds");
            roundsLast5.setHeaderText("Latest Rounds");
            roundsLast5.setContentText(text);
            roundsLast5.show();

        });
        MenuItem Solution = new MenuItem("Solution");
        Solution.setOnAction(e -> {
            gameOver3();
        });
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Created by Nicolas Pigadas. \n" + "nicolaspigadas14@gmail.com \n" + "version 1.0", ButtonType.CLOSE);
            aboutAlert.setTitle("About");
            aboutAlert.setHeaderText("Minesweeper");

            ((Stage) aboutAlert.getDialogPane().getScene().getWindow()).getIcons().add(mine);

            aboutAlert.showAndWait();
        });
        MenuItem rules = new MenuItem("Rules");
        rules.setOnAction(e -> {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Welcome to Minesweeper with a Twist!!\n\n"
                            + "It is like the classical game we all know and love, but with a twist! \n"
                            + "You can add the mode of a super bomb, a bomb that when flagged within "
                            + "the first four tries, its row and column are revealed. Oh, and there is a countdown.  \n"
                            + "Apart from the quick play menu items, you can also customize the game yourself: \n"
                            + "1) Use 'Create' option in 'App' \n"
                            + "   a) Add scenario's ID \n"
                            + "   b) Add level difficulty: 1 for easy, 2 for difficult \n"
                            + "   c) Choose the number of sea mines: between 9-11 for difficulty 1 and 35-45 for difficulty 2 \n"
                            + "   d) Choose the available time provided: between 120-180 for difficulty 1 and 240-360 for difficulty 2 \n"
                            + "   e) Select the existence of a superbomb: 0 for difficulty 1 (non-existent) and 1 for difficulty 2 (existent) \n"
                            + "2) Use 'Load' option in 'App' \n"
                            + "   a) Add scenario's ID \n"
                            + "3) Use 'Start' option in 'App' \n"
                            + "4) Enjoy your Game! Also, look for future updates, we will make the game even more customizable! \n"

            );
            helpAlert.setTitle("Rules");
            helpAlert.setHeaderText("Rules of the Game");
            helpAlert.showAndWait();
        });
        MenuItem help = new MenuItem("Information");
        help.setOnAction(e -> {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Welcome to Minesweeper with a Twist!!\n\n"
                            + "It is like the classical game we all know and love, but with some differences in the scenario and the rules. \n\n"
                            + "Scenario\n"
                            + "In this game, you are in a submarine in the enemy's territory! \n\n"
                            + "Welp! The sea mine radar doesn't work anymore... Thankfully, we are equipped with a radar that shows the sea mines around each square (kilometer). \n\n"
                            + "You have to locate every sea mine and flag it in order to help our submarine devise its course and maneuver to safety! \n\n"
                            + "But beware... The enemy submarines are onto you! They are on your tail and about to catch you, be fast! \n\n"
                            + "Rules\n"
                            + "You win if the only tiles left are sea mines, not if you flag all the sea mines. \n\n"
            );
            helpAlert.setTitle("Info");
            helpAlert.setHeaderText("The Game");
            helpAlert.showAndWait();
        });

        Details.getItems().addAll(Rounds, Solution, about, rules, help);


        timeline.stop();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    remainingSeconds--;
                    if (remainingSeconds < 0) {
                        remainingSeconds = 0;
                        timeLimit();

                        //timeline.stop(); //took me 2h to find this bug omg
                        //remainingSeconds = remainingSecondsReload; // not working
                    } else {
                        countdownLabel.setText("Time left: " + remainingSeconds + " seconds");
                    }
                })
        );
        timeline.play();

        pauseButton = new Button(" ▶ ⫿⫿ "); // ▷, ▶
        pauseButton.setStyle("-fx-background-color: #F4F4F4; -fx-text-fill: #E0A86D;"); //#CECBC9, #AAA8A7
        pauseButton.setOnAction(e -> {
            //System.out.println("I am not from earth");
            if(paused != true) {
                //System.out.println("I am not from earth");
                paused = true;
                pauseButton.setStyle("-fx-background-color: #DABEF6; -fx-text-fill: #F4F4F4;");
                pause();
                timeline.stop();

            }
            else{
                paused = false;
                pauseButton.setStyle("-fx-background-color: #F4F4F4; -fx-text-fill: #E0A86D;");
                unpause();
                timeline.play();
            }
        });


        menuBar.getMenus().addAll(menuFile, menuSize, menuDifficulty, Time, menuSound, Details);

        countdownLabel.setText("Time left: " + timeline + " seconds");

        bombsLeft.setText("Number of Sea Mines: " + totalBombs);

        totalFlags.setText("Flagged Positions: " + Tile.count_flags);


        vbox.getChildren().addAll(menuBar, createContent(), pauseButton, countdownLabel, bombsLeft, totalFlags);
        VBox.setMargin(pauseButton, new Insets(0, 0, 0, 155));


        scene = new Scene(vbox);

        scene.getStylesheets().add("style.css");
        main.setScene(scene);
        main.setResizable(false);
        main.sizeToScene();
        main.show();
    }

    public static void reload() {

        /////////////////////////// Reset Parameters ///////////////////////////
        Tile.uncoveredTiles = 0;
        if(scenarioReaderUsed == true){
            gridSize = scenarioReader.fifthValueGrid;
            totalBombs = scenarioReader.secondLine;
            remainingSeconds = scenarioReader.thirdLine;
            remainingSecondsReload = scenarioReader.thirdLine;
            super_bomb_flag = scenarioReader.fourthLine;
        }
        else{
            super_bomb_flag = s_b_flag;
            totalBombs = gridSize * gridSize / bombPercent;
        }

        grid = new Tile[gridSize][gridSize];

        secondsPassed = 0;
        pressed=false;
        Tile.leftClickSucesses=0;
        Tile.flagged_count = 0;
        Tile.count_flags = 0;
        paused = false;
        totalFlags.setText("Flagged Positions: " + Tile.count_flags);


        Tile.uncoveredTiles = 0;
        gameOver = false;

        ////////////////////////////////////////////////////////////////////////

        /////////////////////////// Update Last 5 Rounds ///////////////////////////

        if (gamesPlayed > 0 && pressed==false) {
            String Round="";
            pressed=true;
            text="";
            Round = "Game " + (gamesPlayed-1) + "\n";
            Round += "Total Bombs: " + totalBombas.get(gamesPlayed - 1) + "\n";
            Round += "Successful Left Clicks: " + totalLeftClicks.get(gamesPlayed - 1) + "\n";
            Round += "Total Playing Time: " + totalPlayingTime.get(gamesPlayed - 1) + "\n";
            Round += "Winner: " + gameWinner.get(gamesPlayed - 1) + "\n";
            allRounds = Round + allRounds ;

            if(gamesPlayed<6) {
                String[] lines = allRounds.split("\n");
                for (int i = 0; i < gamesPlayed * 5; i++) { //Math.min(gamesPlayed * 5, lines.length * gamesPlayed) //gamesPlayed
                    text += lines[i] + "\n";
                    System.out.println(lines[i]);
                }
            }
            else {
                String[] lines = allRounds.split("\n");
                for (int i = 0; i < 5 * 5; i++) { //Math.min(gamesPlayed * 5, lines.length * gamesPlayed) //gamesPlayed
                    text += lines[i] + "\n";
                    System.out.println(lines[i]);
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////

        /////////////////////////// Reset Timer ///////////////////////////

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            }

            ;
        };

        timer.cancel();
        timer = new Timer();
        timer.schedule(task, 1000, 1000);

        Timeline timeline = new Timeline();

        remainingSeconds = remainingSecondsReload;

        ////////////////////////////////////////////////////////////////////////

        /////////////////////////// Reuse Game's CSS style ///////////////////////////
        scene.getStylesheets().add("style.css");
        ////////////////////////////////////////////////////////////////////////

        /////////////////////////// Reload Game ///////////////////////////

        vbox.getChildren().remove(1, 6);

        vbox.getChildren().addAll(createContent(), pauseButton, countdownLabel, bombsLeft, totalFlags);
        main.sizeToScene();

        ////////////////////////////////////////////////////////////////////////
    }


    /**
     * create tiles, assign bombs and super bomb if it exists
     *
     * @return root - The playing field
     */
    private static Parent createContent() {

        // Additional resets. They happen for the first game, as well. Thus, they are here.
        numBombs = 0;
        foundBombs = 0;

        Pane root = new Pane();


        try {
             FileWriter fw = new FileWriter("./current game mines/mines.txt");
             BufferedWriter bw = new BufferedWriter(fw);

             root.setPrefSize(gridSize * 35, gridSize * 35);

        // create all tile and buttons on the grid
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {

                Tile tile = new Tile(x, y, false);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        // assign bombs randomly to tiles
        for (int i = 0; i < totalBombs; i++) {

            Random rand = new Random();

            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);

            // write the positions of the mines in a txt
            bw.write(y + "," + x + "," + Boolean.compare(super_bomb_flag, false));
            bw.newLine();

            // choose the first randomly selected bomb to be the superbomb, if superbomb mode is selected
            if (super_bomb_flag == true) {
                grid[x][y].isSuperBomb = true;
                super_bomb_flag = false;
                y_super_bomb = y;
                x_super_bomb = x;
            }

            // if you randomly select a tile which already has a bomb, reselect tile
            if (grid[x][y].hasBomb) {
                if (i == 0) {
                    i = 0;
                } else {
                    i--;
                }
            } else {
                grid[x][y].hasBomb = true;
                numBombs++;
            }
        }

        // add values to the tiles and set their colours accordingly.
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {

                int numNeighboursBomb = 0;

                ArrayList<Tile> neighbours = new ArrayList<Tile>();

                int[] neighboursLocs = new int[]{-1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1};

                for (int i = 0; i < neighboursLocs.length; i++) {
                    int dx = neighboursLocs[i];
                    int dy = neighboursLocs[++i];

                    int newX = x + dx;
                    int newY = y + dy;

                    if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
                        neighbours.add(grid[newX][newY]);
                            if (grid[newX][newY].hasBomb) {
                                numNeighboursBomb++;
                            }
                         }
                     }

                     grid[x][y].numBombs = numNeighboursBomb;
                     grid[x][y].neighbours = neighbours;

                     Color[] colors = {null, Color.BLUE, Color.GREEN, Color.RED, Color.DARKBLUE, Color.DARKRED, Color.CYAN,
                             Color.BLACK, Color.DARKGRAY};

                     grid[x][y].color = colors[grid[x][y].numBombs];

                }
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("An error occurred while writing to the file or creating content: " + e.getMessage());
        }
        catch (Exception e) {
            //e.printStackTrace();
            System.err.println("An error occurred while writing to the file or creating content: " + e.getMessage());
        }

        return root;
    }

    /**
     * GameOver when bomb is left clicked.
     */
    public static void gameOver() {

        gamesPlayed++;
        timeline.stop();

        if (sound) {
            AudioClip explosion = new AudioClip(Main.class.getResource("./sounds/explosion.wav").toString());
            explosion.play();
        }

        // reveal all tiles' contents
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (grid[x][y].hasBomb) {
                    grid[x][y].btn.setGraphic(new ImageView(mine));
                    grid[x][y].btn.setDisable(true);
                }
            }
        }

        // save the game's details
        totalBombas.add(totalBombs);
        totalLeftClicks.add(Tile.leftClickSucesses);
        totalPlayingTime.add(remainingSecondsReload-remainingSeconds);
        gameWinner.add("Computer, due to sea mine.");

        // show results window
        Alert gameOver = new Alert(AlertType.INFORMATION);
        ((Stage) gameOver.getDialogPane().getScene().getWindow()).getIcons().add(seaMine);
        gameOver.setTitle("Game Over!");
        gameOver.setGraphic(new ImageView(seaMine));
        gameOver.setHeaderText("Sea mine exploded!");
        gameOver.setContentText(
                "Oh no! Your submarine run into a sea mine and caused all the sea mines to explode!");
        gameOver.showAndWait();
        timeline.play();
        reload();

    }

    /**
     * GameOver when time runs out.
     */
    public static void timeLimit() {

        gamesPlayed++;

        if (!gameOver && sound) {
            AudioClip timeoutSound = new AudioClip(Main.class.getResource("./sounds/timeout.mp3").toString());
            timeoutSound.play();
        }

        // save the game's details
        totalBombas.add(totalBombs);
        totalLeftClicks.add(Tile.leftClickSucesses);
        totalPlayingTime.add(remainingSecondsReload-remainingSeconds);
        gameWinner.add("Computer, due to timeout.");

        if (!gameOver && Main.remainingSeconds == 0) {
            gameOver = true;
            // timeline.stop();
            for (int y = 0; y < Main.gridSize; y++) {
                for (int x = 0; x < Main.gridSize; x++) {
                    if (Main.grid[x][y].hasBomb) {
                        grid[x][y].btn.setGraphic(new ImageView(Main.mine));
                    }
                    grid[x][y].btn.setDisable(true);
                }
            }

            // show results window
            Alert gameOver = new Alert(AlertType.INFORMATION);
            ((Stage) gameOver.getDialogPane().getScene().getWindow()).getIcons().add(submarine);
            gameOver.setTitle("Game Over!");
            gameOver.setGraphic(new ImageView(submarine));
            gameOver.setHeaderText("Caught by enemy submarines!");
            gameOver.setContentText(
                    "Oh no! The enemy submarines caught up on you!"
            );

            Platform.runLater(() -> gameOver.showAndWait());
            // gameOver.showAndWait(); //produces error because animations aren't loaded on time
            //timeline.play();
            reload();
        }
    }

    /**
     * GameOver when user requests solution.
     */
    public static void gameOver3() {
        gamesPlayed++;
        if (!gameOver && sound) {
            AudioClip timeoutSound = new AudioClip(Main.class.getResource("/sounds/timeout.mp3").toString());
            timeoutSound.play();
        }
        timeline.stop();

        // reveal all tiles' contents
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (grid[x][y].hasBomb) {
                    grid[x][y].btn.setGraphic(new ImageView(mine));
                    grid[x][y].btn.setDisable(true);
                }
            }
        }

        // save the game's details
        totalBombas.add(totalBombs);
        totalLeftClicks.add(Tile.leftClickSucesses);
        totalPlayingTime.add(remainingSecondsReload-remainingSeconds);
        gameWinner.add("Computer, due to surrender.");

        ImageView Surrender = new ImageView(surrender);
        Surrender.setSmooth(true);
        Surrender.setPreserveRatio(true);
        Surrender.setFitHeight(135);

        // show results window
        Alert gameOver = new Alert(AlertType.INFORMATION);
        ((Stage) gameOver.getDialogPane().getScene().getWindow()).getIcons().add(surrender);
        gameOver.setTitle("Game Over!");
        gameOver.setGraphic(Surrender);
        gameOver.setHeaderText("Surrender!");
        gameOver.setContentText("Your submarine surrendered to the enemy.");
        gameOver.showAndWait();
        timeline.play();
        reload();
    }

    /**
     * Player wins.
     */
    public static void win() {

        gamesPlayed++;
        timeline.stop();

        if (sound) {
            AudioClip winSound = new AudioClip(Main.class.getResource("/sounds/win.wav").toString());
            winSound.play();
        }

        Image winTrophy = new Image("./images/winTrophy.jpeg");
        ImageView winTrophyView = new ImageView(winTrophy);
        winTrophyView.setSmooth(true);
        winTrophyView.setPreserveRatio(true);
        winTrophyView.setFitHeight(135);

        // save the game's details
        totalBombas.add(totalBombs);
        totalLeftClicks.add(Tile.leftClickSucesses);
        totalPlayingTime.add(remainingSecondsReload-remainingSeconds);
        gameWinner.add("You!");

        // show results window
        Alert win = new Alert(AlertType.CONFIRMATION);
        ((Stage) win.getDialogPane().getScene().getWindow()).getIcons().add(mine);
        win.setTitle("Win!");
        win.setGraphic(winTrophyView);
        win.setHeaderText("Congratulations, you and your submarine escaped!");
        win.setContentText("You found all the sea mines in " + secondsPassed + " seconds.");
        win.showAndWait();
        timeline.play();
        reload();
    }

    /**
     * Pauses the game.
     */
    public void pause() {
        xs = new int[gridSize];
        ys = new int[gridSize];
        xys = new int[gridSize][gridSize];

        // freezes tiles
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if(grid[x][y].btn.isDisabled()==false) {
                    grid[x][y].btn.setDisable(true);
                    xs[x]=x; // didn't work
                    ys[y]=y; // didn't work
                    xys[x][y]=1;
                }
                else{
                    xs[x]=-1; // didn't work
                    ys[y]=-1; // didn't work
                    xys[x][y]=0;
                }
            }
        }

    }

    /**
     * Unpauses the game.
     */
    public void unpause() {

        // unfreezes tiles
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if(xys[x][y]==1) {
                    grid[x][y].btn.setDisable(false);
                }
            }
        }

    }

    /**
     * This code block is the main method of a JavaFX application.
     * The main method is the entry point of any Java application. In this case, it calls the launch method of the Application class, which is a method of the JavaFX library.
     * The launch method starts the JavaFX runtime system, which initializes the JavaFX toolkit and sets up the primary stage for the application. The primary stage is the main window that appears when the application is launched.
     * The launch method then calls the start method of the Application class, which is an abstract method that must be implemented by the developer. This is where the code for the application's user interface and functionality is written.
     * By calling the launch method in the main method, the JavaFX runtime system is started, the primary stage is set up, and the start method is called, which ultimately displays the application's user interface to the user.
     */
    public static void main(String[] args) {
        launch(args);
    }

}

