package FightingGame;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.function.BiConsumer;
import javafx.geometry.Pos;

public class CharacterSelectScreen {
    private Scene scene;
    private Label player1Selection;
    private Label player2Selection;
    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private Character player1Character;
    private Character player2Character;
    private Button player1ReadyButton;
    private Button player2ReadyButton;

    // Callback that will be called when both players are ready
    private BiConsumer<Character, Character> onReady;

    public void setOnReady(BiConsumer<Character, Character> onReady) {
        this.onReady = onReady;
    }

    public Scene getScene() {
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);

        GridPane characterGrid = new GridPane();
        characterGrid.setHgap(10);
        characterGrid.setVgap(10);

        List<Character> characters = CharacterRoster.getCharacters();

        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
            Button characterButton = new Button(character.getName());
            characterButton.setOnAction(e -> selectCharacter(character));
            characterGrid.add(characterButton, i % 4, i / 4);
        }

        player1Selection = new Label("Player 1: Not selected");
        player1ReadyButton = new Button("Player 1 Ready");
        player1ReadyButton.setDisable(true);
        player1ReadyButton.setOnAction(e -> {
            player1Ready = true;
            player1ReadyButton.setDisable(true);
        });

        Button player1DeselectButton = new Button("Deselect Player 1");
        player1DeselectButton.setOnAction(e -> {
            if (!player1Ready) {
                player1Character = null;
                player1Selection.setText("Player 1: Not selected");
                player1ReadyButton.setDisable(true);
            }
        });

        VBox player1Controls = new VBox(10, player1Selection, player1ReadyButton, player1DeselectButton);
        player1Controls.setAlignment(Pos.CENTER);

        player2Selection = new Label("Player 2: Not selected");
        player2ReadyButton = new Button("Player 2 Ready");
        player2ReadyButton.setDisable(true);
        player2ReadyButton.setOnAction(e -> {
            player2Ready = true;
            player2ReadyButton.setDisable(true);
        });

        Button player2DeselectButton = new Button("Deselect Player 2");
        player2DeselectButton.setOnAction(e -> {
            if (!player2Ready) {
                player2Character = null;
                player2Selection.setText("Player 2: Not selected");
                player2ReadyButton.setDisable(true);
            }
        });

        VBox player2Controls = new VBox(10, player2Selection, player2ReadyButton, player2DeselectButton);
        player2Controls.setAlignment(Pos.CENTER);

        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> {
            if (player1Ready && player2Ready) {
                // Use the onReady callback if set, otherwise start directly
                if (onReady != null) {
                    onReady.accept(player1Character, player2Character);
                } else {
                    System.out.println("onReady callback not set.");
                }
            } else {
                System.out.println("Both players need to be ready before starting.");
            }
        });

        HBox playerControls = new HBox(50, player1Controls, player2Controls);
        playerControls.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(characterGrid, playerControls, startGameButton);
        scene = new Scene(mainLayout, 600, 400);
        return scene;
    }

    private void selectCharacter(Character character) {
        if (!player1Ready && player1Character == null) {
            player1Selection.setText("Player 1: " + character.getName());
            player1Character = character;
            player1ReadyButton.setDisable(false);
        } else if (!player2Ready && player2Character == null) {
            player2Selection.setText("Player 2: " + character.getName());
            player2Character = character;
            player2ReadyButton.setDisable(false);
        }
    }
}
