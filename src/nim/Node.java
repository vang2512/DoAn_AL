package nim;

import java.util.Arrays;
public class Node {
    int[] piles;
    boolean[][] selectedCircles;
    boolean playerTurn;
    
    public Node(int[] piles, boolean[][] selectedCircles, boolean playerTurn) {
        this.piles = piles.clone();
        this.selectedCircles = new boolean[selectedCircles.length][];
        for (int i = 0; i < selectedCircles.length; i++) {
            this.selectedCircles[i] = selectedCircles[i].clone();
        }
        this.playerTurn = playerTurn;
    }
    public boolean isGameOver() { 
        for (int pile : piles) {
            if (pile > 0) {
                return false;
            }
        }
        return true;
    }
}

