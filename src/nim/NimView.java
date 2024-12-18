package nim;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NimView extends JPanel {
    private BufferedImage circleImage;
    private BufferedImage backgroundImage;
    private BufferedImage selectedImage;
    private JButton playerButton;
    private JButton computerButton;
    private JButton newGameButton;
    private NimModel model;

    public void setModel(NimModel model) {
        this.model = model; 
        repaint(); 
    }
    public NimView() {
        try {
            circleImage = ImageIO.read(new File("src/dim.png"));
            backgroundImage = ImageIO.read(new File("src/flappybirdbg.png"));
            selectedImage = ImageIO.read(new File("src/dim_af.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        playerButton = new JButton("Người chơi");
        computerButton = new JButton("Máy tính");
        newGameButton = new JButton("Game Mới");
        setLayout(new BorderLayout());
        JPanel controls = new JPanel();
        controls.add(playerButton);
        controls.add(computerButton);
        controls.add(newGameButton);
        add(controls, BorderLayout.SOUTH);
    }

    public JButton getPlayerButton() {
        return playerButton;
    }

    public JButton getComputerButton() {
        return computerButton;
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }
    public void drawGame(int[] piles, boolean[][] selectedCircles, boolean gameEnded, boolean playerTurn, Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
        int y = 50;
        for (int i = 0; i < piles.length; i++) {
            drawRow(g, piles[i], selectedCircles[i], y);
            y += 100;
        }
        if (gameEnded) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.RED);
            String winner = playerTurn ? "Máy Tính Thắng !" : "Người Chơi Thắng !";
            g.drawString(winner, getWidth() / 2 - 150, getHeight() / 2);
        }
    }
    private void drawRow(Graphics g, int numCircles, boolean[] selected, int y) {
        int imageWidth = 130;
        int gap = 5;
        int totalWidth = numCircles * (imageWidth + gap) - gap;
        int x = (getWidth() - totalWidth) / 2;
        for (int i = 0; i < numCircles; i++) {
            BufferedImage img = selected[i] ? selectedImage : circleImage;
            g.drawImage(img, x, y, imageWidth, imageWidth, null);
            x += imageWidth + gap;
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (model != null) {
            drawGame(model.getPiles(), model.getSelectedCircles(), model.isGameEnded(), model.isPlayerTurn(), g);
        }
    }
    public void updateButtonStyles() {
        if (model != null && model.isPlayerTurn()) {
            playerButton.setBackground(Color.GREEN);
            computerButton.setBackground(UIManager.getColor("Button.background"));
        } else {
            computerButton.setBackground(Color.GREEN);
            playerButton.setBackground(UIManager.getColor("Button.background"));
        }
        newGameButton.setBackground(Color.CYAN); 
    }
}
