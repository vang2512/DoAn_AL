package tuan_3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class View_Nim extends JPanel {
    private int[] piles;
    private boolean[][] selectedCircles;
    private boolean playerTurn = true;
    private boolean gameEnded = false;
    private JButton removeButton;
    private JButton newGameButton;
    private int selectedRow = -1;
    private BufferedImage circleImage; 
    private BufferedImage backgroundImage; 
    private BufferedImage  image_af;

    public View_Nim() {
        try {
            circleImage = ImageIO.read(new File("src/dim.png")); 
            backgroundImage = ImageIO.read(new File("src/flappybirdbg.png")); 
            image_af= ImageIO.read(new File("src/dim_af.png")); 
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        removeButton = new JButton("Remove Selected");
        newGameButton = new JButton("New Game");

        removeButton.addActionListener(new RemoveAction());
        newGameButton.addActionListener(new NewGameAction());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCircle(e.getX(), e.getY());
            }
        });
        setLayout(new BorderLayout());
        JPanel controls = new JPanel();
        controls.add(removeButton);
        controls.add(newGameButton);
        add(controls, BorderLayout.SOUTH);

        startNewGame();
    }

    private void startNewGame() {
        String rowInput = JOptionPane.showInputDialog("Nhập số lượng hàng:");
        if (rowInput == null) return;
        int numRows = Integer.parseInt(rowInput);
        piles = new int[numRows];
        selectedCircles = new boolean[numRows][];
        for (int i = 0; i < numRows; i++) {
            String pileInput = JOptionPane.showInputDialog("Nhập số lượng ảnh cho hàng " + (i + 1) + ":");
            if (pileInput == null) return;
            piles[i] = Integer.parseInt(pileInput);
            selectedCircles[i] = new boolean[piles[i]];
        }
        playerTurn = true;
        gameEnded = false;
        selectedRow = -1;
        repaint();
    }
    private void selectCircle(int mouseX, int mouseY) {
        int rowHeight = 100; 
        int imageWidth = 130; 
        int gap = 5; 
        int row = (mouseY - 50) / rowHeight;
                if (row >= 0 && row < piles.length) {
            int totalWidth = piles[row] * (imageWidth + gap) - gap;
            int x = (getWidth() - totalWidth) / 2;
            for (int i = 0; i < piles[row]; i++) {
                if (mouseX >= x && mouseX <= x + imageWidth &&
                    mouseY >= (50 + row * rowHeight) &&
                    mouseY <= (50 + row * rowHeight + imageWidth)) {
                    selectedCircles[row][i] = !selectedCircles[row][i];
                       if (selectedRow != -1 && selectedRow != row) {
                        for (int j = 0; j < selectedCircles[selectedRow].length; j++) {
                            selectedCircles[selectedRow][j] = false;
                        }
                    }
                    selectedRow = row;
                    break; 
                }
                x += imageWidth + gap;
            }
            repaint();
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        int y = 50;
        for (int i = 0; i < piles.length; i++) {
            drawRow(g, i, piles[i], y);
            y += 100;
        }

        if (gameEnded) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.RED);
            String winner = playerTurn ? "Người chơi thắng!!" : "Máy tính thắng!";
            g.drawString(winner, getWidth() / 2 - 100, getHeight() / 2);
        }
    }


    private void drawRow(Graphics g, int row, int numCircles, int y) {
        int imageWidth = 130; 
        int gap = 5; 
        int totalWidth = numCircles * (imageWidth + gap) - gap; 
        int x = (getWidth() - totalWidth) / 2; 

        for (int i = 0; i < numCircles; i++) {
            BufferedImage img = selectedCircles[row][i] ? image_af: circleImage;
            g.drawImage(img, x, y, imageWidth, imageWidth, null); 
            x += imageWidth + gap; 
        }
    }

    private void computerMove() {
        playerTurn = true;
        repaint();
        checkWin();
    }

    private boolean isGameOver(int[] currentPiles) {
        for (int pile : currentPiles) {
            if (pile > 0) {
                return false;
            }
        }
        return true;
    }

    private void checkWin() {
        boolean empty = true;
        for (int pile : piles) {
            if (pile > 0) {
                empty = false;
                break;
            }
        }
        if (empty) {
            gameEnded = true;
            repaint();
        }
    }

    private class RemoveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (playerTurn && !gameEnded) {
                int countSelected = 0;
                if (selectedRow != -1) {
                    for (int j = 0; j < piles[selectedRow]; j++) {
                        if (selectedCircles[selectedRow][j]) {
                            countSelected++;
                            selectedCircles[selectedRow][j] = false;
                        }
                    }
                    piles[selectedRow] -= countSelected;
                    if (piles[selectedRow] < 0) {
                        piles[selectedRow] = 0;
                    }
                }
                playerTurn = false;
                repaint();
                if (!gameEnded) {
                    computerMove();
                }
            }
        }
    }

    private class NewGameAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startNewGame();
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Trò chơi Nim");
        View_Nim game = new View_Nim();
        frame.add(game);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
