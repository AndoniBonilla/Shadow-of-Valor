package FightingGame;

import java.util.HashSet;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameApp 
{
    private static Stage primaryStage;
    private static Set<KeyCode> activeKeys = new HashSet<>();

    public static void setPrimaryStage(Stage stage) 
    {
        primaryStage = stage;
    }

    public static void startGameWithCharacters(Character player1Character, Character player2Character)
    {
        Pane root = new Pane();

        player1Character.getCharacterSprite().setX(100);
        player1Character.getCharacterSprite().setY(200);

        player2Character.getCharacterSprite().setX(400);
        player2Character.getCharacterSprite().setY(200);

        root.getChildren().addAll(player1Character.getCharacterSprite(), player2Character.getCharacterSprite());

        Scene scene = new Scene(root, 600, 400);
        scene.setOnKeyPressed(event -> activeKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> activeKeys.remove(event.getCode()));

        AnimationTimer gameLoop = new AnimationTimer() 
        {
            @Override
            public void handle(long now) 
            {
                handleInput(player1Character, player2Character);
                checkCollisions(player1Character, player2Character);
            }
        };
        gameLoop.start();

        primaryStage.setTitle("Fighting Game - Battle!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void handleInput(Character player1, Character player2) 
    {
        if (activeKeys.contains(KeyCode.W)) player1.move(0, -2);
        if (activeKeys.contains(KeyCode.S)) player1.move(0, 2);
        if (activeKeys.contains(KeyCode.A)) player1.move(-2, 0);
        if (activeKeys.contains(KeyCode.D)) player1.move(2, 0);
        if (activeKeys.contains(KeyCode.F)) player1.attack(player2);
        if (activeKeys.contains(KeyCode.G)) player1.useSpecialAbility(player2);

        if (activeKeys.contains(KeyCode.UP)) player2.move(0, -2);
        if (activeKeys.contains(KeyCode.DOWN)) player2.move(0, 2);
        if (activeKeys.contains(KeyCode.LEFT)) player2.move(-2, 0);
        if (activeKeys.contains(KeyCode.RIGHT)) player2.move(2, 0);
        if (activeKeys.contains(KeyCode.L)) player2.attack(player1);
        if (activeKeys.contains(KeyCode.K)) player2.useSpecialAbility(player1);
    }

    private static void checkCollisions(Character player1, Character player2) 
    {
        if (player1.getCharacterSprite().getBoundsInParent().intersects(player2.getCharacterSprite().getBoundsInParent()))
        {
            System.out.println("Players have collided!");
        }
    }
}
