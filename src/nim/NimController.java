package nim;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NimController {
    private NimModel model;
    private NimView view;
    private boolean playerStarts;

    public NimController(NimModel model, NimView view) {
        this.model = model;
        this.view = view;
        view.getPlayerButton().addActionListener(e -> handlePlayerTurn());
        view.getComputerButton().addActionListener(e -> handleComputerTurn());
        view.getNewGameButton().addActionListener(e -> startNewGame());
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
            String pileInput = JOptionPane.showInputDialog("Nhập số lượng que cho hàng " + (i + 1) + ":");
            if (pileInput == null || pileInput.isEmpty()) return;
            try {
                pileSizes[i] = Integer.parseInt(pileInput);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        model.startNewGame(numRows, pileSizes);
        determineFirstTurn();
        view.updateButtonStyles(); 
        view.repaint();
    }
    private void determineFirstTurn() {
        int choice = JOptionPane.showOptionDialog(view,
                "Bạn muốn ai đi trước?", "Chọn lượt chơi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"Người chơi", "Máy tính"}, "Người chơi");
        if (choice == JOptionPane.YES_OPTION) {
            playerStarts = true;
        } else {
            playerStarts = false;
        }
        model.setPlayerTurn(playerStarts); 
    }
    private void handlePlayerTurn() {
        if (!model.isGameEnded() && model.isPlayerTurn()) {
            int selectedRow = getSelectedRow();
            if (selectedRow != -1) {
                model.removeCircles(selectedRow);
                if (!model.isGameEnded()) {
                    model.setPlayerTurn(false); 
                }
            } else {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn ít nhất một que để xóa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
            view.updateButtonStyles(); 
            view.repaint();
        }
    }
    private void handleComputerTurn() {
        if (!model.isGameEnded() && !model.isPlayerTurn()) {
            model.computerTurn();
            if (!model.isGameEnded()) {
                model.setPlayerTurn(true); 
            }
            view.updateButtonStyles(); 
            view.repaint();
        }
    }
    private void handleMouseClick(int x, int y) {
    	 if (!model.isPlayerTurn() || model.isGameEnded()) {
    	        return;
    	    }
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
        view.repaint();
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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(frame, "Bạn có chắc chắn muốn thoát không?",
                        "Xác nhận thoát", JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION)
                    frame.dispose();
            }
        });
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        NimModel model = new NimModel(); 
        NimView view = new NimView();
        NimController controller = new NimController(model, view);
        view.setModel(model); 
        controller.startNewGame(); 
        controller.run(); 
    }

}

