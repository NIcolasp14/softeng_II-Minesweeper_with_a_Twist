// java imports
import java.util.ArrayList;

// javafx imports
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;


/**
 * Represents a Tile in a game of Minesweeper
 * and all the actions it might happen to it,
 * as well as every design it might change to.
 */
public class Tile extends StackPane {

    /**number of bombs adjacent to a tile*/
    int numBombs = 0;

    /** To check if the first four flags deployed on tiles, flagged the super bomb.
     If the super bomb is flagged within the first 4 attempts, the row and column
     where it is located is revealed.*/
    public static int flagged_count = 0;

    /** To constraint the number of flagged tiles.
     The number of flags must be the same as the number of bombs in the ongoing game.*/
    public static int count_flags = 0;

    /** How many attempts to uncover tiles were successful in one game
     It has a typo, I know :). Also, neighbours and color, British and American
     versions of English are mixed in here.
     This variable is presented in the Rounds MenuItem.*/
    public static int leftClickSucesses = 0;

    /** flag: does a tile contain a bomb?*/
    boolean hasBomb;

    /** flag: does this tile contain the super bomb?*/
    public boolean isSuperBomb;

    /** flag: is this tile active or has it been disabled? */
    private boolean active = true;

    /** flag: is this tile flagged?*/
    private boolean flagged = false;

    /** count the tiles that have been uncovered to build the statement that
    when it is met, the player wins:
            (Total tiles) - (Uncovered tiles) = (Total bombs in the current game) */
    public static int uncoveredTiles = 0;

    /** In Main.java, the color of every number of bombs adjacent to a tile is defined. Null is default
     in case of 0, which is not displayed. An empty tile is displayed instead.*/
    Color color = null;

    /** each tile is a button that can be clicked of flagged and then becomes disabled.*/
    Button btn = new Button();

    /** for the recursive reveal of neighbors in the case of a blank click
        (blank click is when we click a tile with no adjacent bombs)*/
    ArrayList<Tile> neighbours = new ArrayList<Tile>();

    /** Image for flagging tiles. Can be changed for different aesthetics.*/
    static Image flag = new Image("./images/flag.png");
//    alternative tile graphic, apple's wave emoji
//    static Image wave = new Image("./images/wave.png");

    /**
     * The Tile constructor constructs a Tile using its x and y coordinates,
     * as well as the information for the existence or not of a bomb behind
     * the Tile. It also defines the Tile's dimensions in the UI and sets
     * that if the Tile receives a mouse click, the method onClick is called,
     * because various actions can be taken from a mouse click, depending on
     * the situation and the mouse click (if it is primary/left or not).
     * */
    Tile(int x, int y, boolean hasBomb) {
        this.hasBomb = hasBomb;

        if (hasBomb) {
            Main.numBombs++;
        }

        btn.setMinHeight(35);
        btn.setMinWidth(35);
        btn.setOnMouseClicked(this::onClick);
        getChildren().addAll(btn);

        setTranslateX(x * 35);
        setTranslateY(y * 35);

    }

    /**
     * onClick is a method that defines what will happen to the Tile
     * when it is clicked. First of all, there is a clicking sound.
     * Then, it examines if a primary/left or secondary/right click
     * has been performed. If it is the former, we have the scenarios
     * of it having a bomb, a blank tile or a simple tile. A blank tile
     * would recursively reveal other tiles, as in the classical game.
     * If it is the latter, we have the secondary click which flags the tiles.
     * If a super bomb exists and if it is flagged within the first four tries
     * the super bomb's column and row are revealed.
     * */
    private void onClick(MouseEvent e) {
        if (Main.sound) {
            AudioClip click = new AudioClip(Main.class.getResource("./sounds/click.wav").toString());
            click.play();
        }

        // Left Click <-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-
        if (e.getButton() == MouseButton.PRIMARY) {
            if (!flagged) {

                uncoveredTiles++;
                btn.setBackground(null);
                btn.setDisable(true);
                active = false;

                if (hasBomb) {
                    Main.gameOver();
                } else {
                    leftClickSucesses++;
                    // Blank
                    if (this.numBombs == 0) {
                        blankClick(this);
                    } else {
                        btn.setText(Integer.toString(numBombs));
                        btn.setTextFill(color);
                    }
                }

                // Checking and validating game's progress via the console
                System.out.println("---------------Left Click---------------");
                System.out.println(Main.gridSize*Main.gridSize);
                System.out.println(uncoveredTiles);
                System.out.println(Main.totalBombs);
                System.out.println("----------------------------------------");

                // Win statement. We win if the total sum of tiles minus the uncovered tiles
                // is equal to the number of bombs.
                if ((Main.gridSize*Main.gridSize-uncoveredTiles) == Main.totalBombs) Main.win();
            }
        }
        // Right Click ->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->
        else {
            if (!flagged) {
                if(count_flags<Main.totalBombs) {

                    flagged = true;
                    count_flags++;
                    flagged_count++;

                    Main.totalFlags.setText("Flagged Positions: " + Tile.count_flags);
                    btn.setGraphic(new ImageView(flag));

                    if (this.hasBomb) {
                        Main.foundBombs++;
                        // FLAG WIN, rejected.
                        // if (Main.foundBombs == Main.numBombs) Main.win();
                        // It would make the hardest final choices easy, because
                        // they would be reduced to trying to put the flag to different squares until
                        // the win.

                    }
                    if (this.isSuperBomb && flagged_count < 4) {

                    // this.btn.setDisable(true);
                    // this.btn.setGraphic(Main.supermine);

                        for (int i = 0; i < Main.gridSize; i++) {
                            if (i == Main.y_super_bomb)
                                continue; // if the tile contains the super bomb, ignore it
                            if (Main.grid[Main.x_super_bomb][i].btn.isDisabled()==true)
                                continue; // if the tile is already disabled, ignore it,
                                          // don't count it as new uncovered tile

                            uncoveredTiles++;
                            Tile columnTile = Main.grid[Main.x_super_bomb][i];

                            if (columnTile.hasBomb == true) {
                                Main.foundBombs++;
                                uncoveredTiles--;
                                columnTile.btn.setGraphic(new ImageView(Main.mine));
                                columnTile.btn.setDisable(true);
                                continue;
                            }

                            columnTile.btn.setDisable(true);
                            columnTile.btn.setGraphic(null);
                            columnTile.btn.setText(Integer.toString(columnTile.numBombs));
                            columnTile.btn.setTextFill(columnTile.color);
                            columnTile.active = false;

                        }


                        for (int i = 0; i < Main.gridSize; i++) {
                            if (i == Main.x_super_bomb)
                                continue; // if the tile contains the super bomb, ignore it
                            if (Main.grid[i][Main.y_super_bomb].btn.isDisabled()==true)
                                continue; // if the tile is already disabled, ignore it
                                          // don't count it as new uncovered tile

                            uncoveredTiles++;
                            Tile rowTile = Main.grid[i][Main.y_super_bomb];

                            if (rowTile.hasBomb == true) {
                                uncoveredTiles--;
                                Main.foundBombs++;
                                rowTile.btn.setGraphic(new ImageView(Main.mine));
                                rowTile.btn.setDisable(true);
                                continue;
                            }

                            rowTile.btn.setDisable(true);
                            rowTile.btn.setGraphic(null);
                            rowTile.btn.setText(Integer.toString(rowTile.numBombs));
                            rowTile.btn.setTextFill(rowTile.color);
                            rowTile.active = false;

                        }

                        // FLAG WIN, rejected.
                        // if (Main.foundBombs == Main.numBombs) Main.win();
                        // It would make the hardest final choices easy, because
                        // they would be reduced to trying to put the flag to different squares until
                        // the win.

                    }
                }

            } else { // Unflagging Tile

                if (hasBomb) {
                    Main.foundBombs--;
                }

                count_flags--;
                btn.setGraphic(null);
                flagged = false;
                Main.totalFlags.setText("Flagged Positions: " + Tile.count_flags);

            }
        }
    }

    /**
     * blankClick is a method that recursively reveals adjacent tiles of a blank tile.
     * For each neighbor tile that is active, the method disables the corresponding button,
     * removes any graphics from it, sets its text to the number of bombs around it, sets its
     * text color to the corresponding color, and marks it as inactive. The uncoveredTiles
     * variable is also incremented, keeping track of the number of uncovered tiles.
     *
     * If the neighbor tile has no bombs around it, the method recursively calls itself with the
     * current neighbor tile as its argument. This means that the same process will be applied to
     * all of the neighbor's neighboring tiles until all of the blank tiles and their adjacent
     * non-blank tiles have been uncovered.
     * */
    private void blankClick(Tile tile) {
        for (int i = 0; i < tile.neighbours.size(); i++) {
            // reveals neighbours and their number
            if (tile.neighbours.get(i).active) {
                tile.neighbours.get(i).btn.setDisable(true);
                tile.neighbours.get(i).btn.setGraphic(null);
                tile.neighbours.get(i).btn.setText(Integer.toString(tile.neighbours.get(i).numBombs));
                tile.neighbours.get(i).btn.setTextFill(tile.neighbours.get(i).color);
                tile.neighbours.get(i).active = false;
                uncoveredTiles++;
                if (tile.neighbours.get(i).numBombs == 0) {
                    // if another blank tile if found, then a recursive appearance of tiles is triggered
                    blankClick(tile.neighbours.get(i));
                }

            }
        }
    }


}
