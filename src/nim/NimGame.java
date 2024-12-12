package nim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.ArrayList;

public class NimGame extends JPanel {
	private int[] piles;
	private boolean[][] selectedCircles;
	private boolean playerTurn = true;
	private boolean gameEnded = false;
	private JButton removeButton;
	private JButton newGameButton;
	private int selectedRow = -1;
	private BufferedImage circleImage;
	private BufferedImage backgroundImage;
	private BufferedImage image_af;

	public NimGame() {
		try {
			circleImage = ImageIO.read(new File("src/dim.png"));
			backgroundImage = ImageIO.read(new File("src/flappybirdbg.png"));
			image_af = ImageIO.read(new File("src/dim_af.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		removeButton = new JButton("Xóa");
		newGameButton = new JButton("Game Mới");

		removeButton.addActionListener(new RemoveAction());
		newGameButton.addActionListener(new NewGameAction());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (playerTurn)
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
		if (rowInput == null)
			return;
		int numRows = Integer.parseInt(rowInput);
		piles = new int[numRows];
		selectedCircles = new boolean[numRows][];

		for (int i = 0; i < numRows; i++) {
			String pileInput = JOptionPane.showInputDialog("Nhập số lượng ảnh cho hàng " + (i + 1) + ":");
			if (pileInput == null)
				return;
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
				if (mouseX >= x && mouseX <= x + imageWidth && mouseY >= (50 + row * rowHeight)
						&& mouseY <= (50 + row * rowHeight + imageWidth)) {
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

	private void computerTurn() {
		List<Integer> nonEmptyRows = new ArrayList<>();
		for (int i = 0; i < piles.length; i++) {
			if (piles[i] > 0) {
				nonEmptyRows.add(i);
			}
		}
		if (nonEmptyRows.size() == 1) {
			int row = nonEmptyRows.get(0);
			if (piles[row] == 1) {
				for (int z = 0; z < piles[row]; z++) {
					selectedCircles[row][z] = true;
				}
				removeCircles(row);
				return;
			} else {
				for (int i = 0; i < piles[row] - 1; i++) {
					selectedCircles[row][i] = true;
				}
				removeCircles(row);
				return;
			}
		} else if (nonEmptyRows.size() == 2 && (piles[nonEmptyRows.get(0)] == 1 || piles[nonEmptyRows.get(1)] == 1)) {
			int row1 = nonEmptyRows.get(0);
			int row2 = nonEmptyRows.get(1);
			if (piles[row1] != 1) {
				for (int j = 0; j < piles[row1]; j++) {
					selectedCircles[row1][j] = true;
				}
				removeCircles(row1);
				return;
			} else if (piles[row2] != 1) {
				for (int j = 0; j < piles[row2]; j++) {
					selectedCircles[row2][j] = true;
				}
				removeCircles(row2);
				return;
			}
		}
		int nimSum = 0;
		for (int pile : piles) {
			nimSum ^= pile;
		}
		if (nimSum != 0) {
			for (int i = 0; i < piles.length; i++) {
				int target = piles[i] ^ nimSum;
				if (target <= piles[i]) {
					int circlesToRemove = piles[i] - target;
					
					for (int j = 0; j < circlesToRemove; j++) {
						selectedCircles[i][j] = true;
					}
					removeCircles(i);
					return;
				}
			}
		} else {
			Node currentState = new Node(piles, selectedCircles, false);
			int bestValue = Integer.MIN_VALUE;
			Node bestMove = null;

			for (Node child : generateChildren(currentState, true)) {
	            int moveValue = minimaxWithAlphaBeta(child, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
				if (moveValue > bestValue) {
					bestValue = moveValue;
					bestMove = child;
				}
			}
			if (bestMove != null) {
				this.piles = bestMove.piles;
				this.selectedCircles = bestMove.selectedCircles;
			}
		}
		if (checkGameEnd()) {
			gameEnded = true;
		}
		playerTurn = true;
		repaint();
	}
	private void removeCircles(int row) {
		for (int i = 0; i < selectedCircles[row].length; i++) {
			if (selectedCircles[row][i]) {
				selectedCircles[row][i] = false;
				piles[row]--;
			}
		}
		if (checkGameEnd()) {
			gameEnded = true;
		}
		playerTurn = !playerTurn;

		if (!playerTurn) {
			computerTurn();
		}
		repaint();
	}
	private boolean checkGameEnd() {
	    for (int pile : piles) {
	        if (pile > 0) {
	            return false; 
	        }
	    }
	    gameEnded = true;
	    return true;
	}
	public int heuristic(Node state) {
		int nimSum = 0;
		for (int pile : state.piles) {
			nimSum ^= pile;
		}
		return nimSum == 0 ? -1 : 1;
	}
	private int minimaxWithAlphaBeta(Node node, int depth, int alpha, int beta, boolean isMaximizing) {
	    if (depth == 0 || node.isGameOver()) {
	        return heuristic(node);
	    }
	    if (isMaximizing) {
	        int maxEval = Integer.MIN_VALUE;
	        for (Node child : generateChildren(node, true)) {
	            int eval = minimaxWithAlphaBeta(child, depth - 1, alpha, beta, false);
	            maxEval = Math.max(maxEval, eval);
	            alpha = Math.max(alpha, eval);
	            if (beta <= alpha) {
	                break;
	            }
	        }
	        return maxEval;
	    } else {
	        int minEval = Integer.MAX_VALUE;
	        for (Node child : generateChildren(node, false)) {
	            int eval = minimaxWithAlphaBeta(child, depth - 1, alpha, beta, true);
	            minEval = Math.min(minEval, eval);
	            beta = Math.min(beta, eval);
	            if (beta <= alpha) {
	                break; 
	            }
	        }
	        return minEval;
	    }
	}
	public List<Node> generateChildren(Node state, boolean maxPlayer) {
	    List<Node> children = new ArrayList<>();
	    for (int row = 0; row < state.piles.length; row++) {
	        if (state.piles[row] > 0) {
	            Node child = new Node(state.piles, state.selectedCircles, !maxPlayer);
	            child.piles[row]--;
	            children.add(child);
	        }
	    }
	    return children;
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
	        String winner = playerTurn ? "Máy Tính Thắng!" : "Người Chơi Thắng!";
	        g.drawString(winner, getWidth() / 2 - 150, getHeight() / 2);
	    }
	}
	private void drawRow(Graphics g, int row, int numCircles, int y) {
		int imageWidth = 130;
		int gap = 5;
		int totalWidth = numCircles * (imageWidth + gap) - gap;
		int x = (getWidth() - totalWidth) / 2;
		for (int i = 0; i < numCircles; i++) {
			BufferedImage img = selectedCircles[row][i] ? image_af : circleImage;
			g.drawImage(img, x, y, imageWidth, imageWidth, null);
			x += imageWidth + gap;
		}
	}

	private class NewGameAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			startNewGame();
		}
	}

	private class RemoveAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedRow != -1 && playerTurn) {
				removeCircles(selectedRow);
			}
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Trò chơi Nim");
		NimGame game = new NimGame();
		frame.add(game);
		frame.setSize(800, 600);
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
}
