package FightingGame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FightArenaPanel extends JPanel
{
    private static final long serialVersionUID = 1L; // Added to resolve the serialVersionUID warning

    private BufferedImage player1Sprite, player2Sprite;
    private int player1X, player1Y;
    private int player2X, player2Y;

    public FightArenaPanel(BufferedImage player1Sprite, BufferedImage player2Sprite) 
    {
        this.player1Sprite = player1Sprite;
        this.player2Sprite = player2Sprite;
        this.player1X = 50;  // Initial X position for Player 1
        this.player1Y = 300; // Initial Y position for Player 1
        this.player2X = 500; // Initial X position for Player 2
        this.player2Y = 300; // Initial Y position for Player 2
        setBackground(Color.BLACK); // Set the background color of the arena
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(player1Sprite, player1X, player1Y, null); // Draw Player 1
        g.drawImage(player2Sprite, player2X, player2Y, null); // Draw Player 2
    }

    // Method to update player positions and repaint the panel
    public void updatePositions(int dx1, int dy1, int dx2, int dy2) 
    {
        player1X += dx1;
        player1Y += dy1;
        player2X += dx2;
        player2Y += dy2;
        repaint();
    }
}
