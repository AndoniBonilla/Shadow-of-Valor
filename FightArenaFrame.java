package FightingGame;

// Import necessary classes for handling user input and graphical rendering
import java.awt.event.KeyAdapter; // KeyAdapter simplifies the implementation of keyboard event handling.  
import java.awt.event.KeyEvent; // KeyEvent provides constants and methods to process specific key inputs.
import java.awt.image.BufferedImage; // BufferedImage handles images, particularly for sprites.

import javax.swing.JFrame; // JFrame creates the main application window.

/**
 * The `FightArenaFrame` class sets up the main window for the fight arena.
 * It manages the graphical rendering of the fight scene and processes player movements.
 */
public class FightArenaFrame extends JFrame 
{
    private static final long serialVersionUID = 1L; // Resolves a serialization warning for JFrame (important for compatibility across versions).

    private FightArenaPanel arenaPanel; // Custom panel for rendering the fight arena and managing player movements.

    /**
     * Constructor for the FightArenaFrame class.
     * Initializes the fight arena window, sets up sprites for Player 1 and Player 2, and handles user input.
     *
     * @param player1Sprite The sprite image for Player 1.
     * @param player2Sprite The sprite image for Player 2.
     */
    public FightArenaFrame(BufferedImage player1Sprite, BufferedImage player2Sprite) 
    {
        // Initialize the fight arena panel with Player 1's and Player 2's sprites.
        arenaPanel = new FightArenaPanel(player1Sprite, player2Sprite);
        
        // Add the arena panel to the JFrame to display it.
        add(arenaPanel);
        
        // Set the window size to 800x600 pixels for a clear view of the arena.
        setSize(800, 600);
        
        // Ensure the application exits when the user closes the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Make the window visible to the user.
        setVisible(true);

        /**
         * Add a key listener to handle player movements using keyboard inputs.
         * The KeyAdapter class simplifies the implementation of the KeyListener interface.
         */
        addKeyListener(new KeyAdapter() 
        {
            /**
             * Overrides the `keyPressed` method to process player movements based on specific key inputs.
             *
             * @param e KeyEvent object representing the key that was pressed.
             */
            @Override
            public void keyPressed(KeyEvent e)
            { 
                int key = e.getKeyCode(); // Retrieve the code of the pressed key.
                
                // Handle Player 1's movements using arrow keys.
                if (key == KeyEvent.VK_LEFT) 
                    arenaPanel.updatePositions(-10, 0, 0, 0); // Move Player 1 left by 10 pixels.
                if (key == KeyEvent.VK_RIGHT) 
                    arenaPanel.updatePositions(10, 0, 0, 0); // Move Player 1 right by 10 pixels.
                if (key == KeyEvent.VK_UP) 
                    arenaPanel.updatePositions(0, -10, 0, 0); // Move Player 1 up by 10 pixels.
                if (key == KeyEvent.VK_DOWN) 
                    arenaPanel.updatePositions(0, 10, 0, 0); // Move Player 1 down by 10 pixels.
                
                // Handle Player 2's movements using WASD keys.
                if (key == KeyEvent.VK_A) 
                    arenaPanel.updatePositions(0, 0, -10, 0); // Move Player 2 left by 10 pixels.
                if (key == KeyEvent.VK_D) 
                    arenaPanel.updatePositions(0, 0, 10, 0); // Move Player 2 right by 10 pixels.
                if (key == KeyEvent.VK_W) 
                    arenaPanel.updatePositions(0, 0, 0, -10); // Move Player 2 up by 10 pixels.
                if (key == KeyEvent.VK_S) 
                    arenaPanel.updatePositions(0, 0, 0, 10); // Move Player 2 down by 10 pixels.
            }
        });
    }
}
