package FightingGame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class FightArenaFrame extends JFrame 
{
    private static final long serialVersionUID = 1L; // Add serialVersionUID to resolve warning

    private FightArenaPanel arenaPanel;

    public FightArenaFrame(BufferedImage player1Sprite, BufferedImage player2Sprite) 
    {
        arenaPanel = new FightArenaPanel(player1Sprite, player2Sprite);
        add(arenaPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Key listener for player controls
        addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) 
            {
                int key = e.getKeyCode();
                
                // Player 1 controls (Arrow keys)
                if (key == KeyEvent.VK_LEFT) arenaPanel.updatePositions(-10, 0, 0, 0);
                if (key == KeyEvent.VK_RIGHT) arenaPanel.updatePositions(10, 0, 0, 0);
                if (key == KeyEvent.VK_UP) arenaPanel.updatePositions(0, -10, 0, 0);
                if (key == KeyEvent.VK_DOWN) arenaPanel.updatePositions(0, 10, 0, 0);
                
                // Player 2 controls (WASD keys)
                if (key == KeyEvent.VK_A) arenaPanel.updatePositions(0, 0, -10, 0);
                if (key == KeyEvent.VK_D) arenaPanel.updatePositions(0, 0, 10, 0);
                if (key == KeyEvent.VK_W) arenaPanel.updatePositions(0, 0, 0, -10);
                if (key == KeyEvent.VK_S) arenaPanel.updatePositions(0, 0, 0, 10);
            }
        });
    }
}
