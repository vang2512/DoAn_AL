package nim;

import java.util.ArrayList;
import java.util.List;

public class NimModel {
	private int[] piles;
	private boolean[][] selectedCircles;
	private boolean playerTurn;
	private boolean gameEnded;

	public void startNewGame(int numRows, int[] pileSizes) {
		piles = new int[numRows];
		selectedCircles = new boolean[numRows][];
		for (int i = 0; i < numRows; i++) {
			piles[i] = pileSizes[i];
			selectedCircles[i] = new boolean[pileSizes[i]];
		}
		playerTurn = true;
		gameEnded = false;
	}

	public int[] getPiles() {
		return piles;
	}

	public boolean[][] getSelectedCircles() {
		return selectedCircles;
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public boolean isGameEnded() {
		return gameEnded;
	}

	public void selectCircle(int row, int index) {
		if (row >= 0 && row < piles.length && index >= 0 && index < piles[row]) {
			for (int i = 0; i < selectedCircles.length; i++) {
				if (i != row) {
					for (int j = 0; j < selectedCircles[i].length; j++) {
						selectedCircles[i][j] = false;
					}
				}
			}
			selectedCircles[row][index] = !selectedCircles[row][index];
		}
	}

	public void removeCircles(int row) {
		boolean wasPlayerTurn = playerTurn;

		for (int i = 0; i < selectedCircles[row].length; i++) {
			if (selectedCircles[row][i]) {
				selectedCircles[row][i] = false;
				piles[row]--;
			}
		}
		gameEnded = checkGameEnd();
		if (gameEnded) {
			if (wasPlayerTurn) {
				gameEnded = true;
			} else {
				gameEnded = true;
			}
		}
		if (!gameEnded) {
			playerTurn = !playerTurn;
		}
	}

	private boolean checkGameEnd() {
		for (int pile : piles) {
			if (pile > 0)
				return false;
		}
		return true;
	}

	public boolean isGameOver() {
		return gameEnded;
	}

	public void computerTurn() {
		if (gameEnded)
			return;
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
		int nimSum = calculateNimSum();
		if (nimSum != 0) {
			for (int i = 0; i < piles.length; i++) {
				int target = piles[i] ^ nimSum;
				if (target < piles[i]) {
					int circlesToRemove = piles[i] - target;
					for (int j = 0; j < circlesToRemove; j++) {
						selectedCircles[i][j] = true;
					}
					removeCircles(i);
					return;
				}
			}
		} else {
			NimModel currentState = new NimModel();
			currentState.piles = piles.clone();
			currentState.selectedCircles = new boolean[piles.length][];
			for (int i = 0; i < piles.length; i++) {
				currentState.selectedCircles[i] = selectedCircles[i].clone();
			}
			int bestEval = Integer.MIN_VALUE;
			NimModel bestMove = null;
			for (NimModel child : generateChildren(true)) {
				int eval = minimaxWithAlphaBeta(child, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
				if (eval > bestEval) {
					bestEval = eval;
					bestMove = child;
				}
			}
			if (bestMove != null) {
				this.piles = bestMove.piles;
				this.selectedCircles = bestMove.selectedCircles;
			}
		}
	}

	private int calculateNimSum() {
		int nimSum = 0;
		for (int pile : piles) {
			nimSum ^= pile;
		}
		return nimSum;
	}

	public List<NimModel> generateChildren(boolean isMaximizing) {
		List<NimModel> children = new ArrayList<>();
		for (int row = 0; row < piles.length; row++) {
			for (int count = 1; count <= piles[row]; count++) {
				NimModel child = new NimModel();
				child.piles = piles.clone();
				child.piles[row] -= count;
				child.selectedCircles = new boolean[piles.length][];
				for (int i = 0; i < piles.length; i++) {
					child.selectedCircles[i] = selectedCircles[i].clone();
				}
				children.add(child);
			}
		}
		return children;
	}

	public int minimaxWithAlphaBeta(NimModel node, int depth, int alpha, int beta, boolean isMaximizing) {
		if (depth == 0 || node.isGameOver()) {
			return heuristic(node);
		}

		if (isMaximizing) {
			int maxEval = Integer.MIN_VALUE;
			for (NimModel child : node.generateChildren(true)) {
				int eval = minimaxWithAlphaBeta(child, depth - 1, alpha, beta, false);
				maxEval = Math.max(maxEval, eval);
				alpha = Math.max(alpha, eval);
				if (beta <= alpha)
					break;
			}
			return maxEval;
		} else {
			int minEval = Integer.MAX_VALUE;
			for (NimModel child : node.generateChildren(false)) {
				int eval = minimaxWithAlphaBeta(child, depth - 1, alpha, beta, true);
				minEval = Math.min(minEval, eval);
				beta = Math.min(beta, eval);
				if (beta <= alpha)
					break;
			}
			return minEval;
		}
	}

	public int heuristic(NimModel state) {
		return calculateNimSum() == 0 ? -1 : 1;
	}
}
