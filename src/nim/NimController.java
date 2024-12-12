package nim;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NimController {
    private NimModel model;
    private NimView view;

    public NimController(NimModel model, NimView view) {
        this.model = model;
        this.view = view;
        
        view.getNewGameButton().addActionListener(e -> startNewGame());
        view.getRemoveButton().addActionListener(e -> {
            if (model.isPlayerTurn()) {
                int selectedRow = getSelectedRow();
                if (selectedRow != -1) {
                    model.removeCircles(selectedRow);
                    if (!model.isGameEnded() && !model.isPlayerTurn()) {
                        model.computerTurn();
                    }
                }
            }
            view.repaint();
        });

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
                view.repaint();
            }
        });
    }
    private void startNewGame() {
        String rowInput = JOptionPane.showInputDialog("Nhập số lượng hàng:");
        if (rowInput == null || rowInput.isEmpty()) return;
        int numRows;
        try {
            numRows = Integer.parseInt(rowInput);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int[] pileSizes = new int[numRows];
        for (int i = 0; i < numRows; i++) {
            String pileInput = JOptionPane.showInputDialog("Nhập số lượng ảnh cho hàng " + (i + 1) + ":");
            if (pileInput == null || pileInput.isEmpty()) return;
            try {
                pileSizes[i] = Integer.parseInt(pileInput);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        model.startNewGame(numRows, pileSizes);
        view.repaint();
    }
    private void handleMouseClick(int x, int y) {
        int rowHeight = 100;
        int imageWidth = 130;
        int gap = 5;
        int row = (y - 50) / rowHeight;
        if (row < 0 || row >= model.getPiles().length) return;
        int totalWidth = model.getPiles()[row] * (imageWidth + gap) - gap;
        int startX = (view.getWidth() - totalWidth) / 2;
        for (int i = 0; i < model.getPiles()[row]; i++) {
            if (x >= startX && x <= startX + imageWidth) {
                model.selectCircle(row, i);
                break;
            }
            startX += imageWidth + gap;
        }
    }

    private int getSelectedRow() {
        for (int i = 0; i < model.getSelectedCircles().length; i++) {
            for (boolean selected : model.getSelectedCircles()[i]) {
                if (selected) return i;
            }
        }
        return -1; 
    }
    public void run() {
        JFrame frame = new JFrame("Trò chơi Nim");
        frame.add(view);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
		   NimModel model = new NimModel();
	        NimView view = new NimView();
	        NimController controller = new NimController(model, view);
	        controller.startNewGame();
	        view.setModel(model);
	        controller.run();
	}

}
