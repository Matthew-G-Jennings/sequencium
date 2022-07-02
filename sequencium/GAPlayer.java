package sequencium;

import sequencium.*;
import java.util.*;

/**
 * A value based Sequencium player. Checks each possible move against a
 * predefined set of conditions to determine it's value
 * returns the "best" move.
 *
 * @author Oliver O'Connor, Matthew Jennings
 * @credit Michael Albert
 */
public class GAPlayer implements Player {

    boolean flipped = false;
    int opponenttotal = 0;
    boolean oppblocked = false;
    boolean oppenc = false;
    String name = "";

    /**
     * It's name is Clarence.
     */
    public GAPlayer() {
        this.name = "Clarence";
    }

    /**
     * Choose a move.
     *
     * @param board the board
     * @return a move
     */
    public int[] makeMove(int[][] board) {
        ArrayList<int[]> moves = new ArrayList<>();
        int[] move = new int[3];
        if (board[0][0] != 1) {
            flipped = true; // remember to flip the move later.
            board = flip(board);
        }
        int rows = board.length;
        int cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] > 0) {
                    for (int[] n : Utilities.neighbours(r, c, rows, cols)) {
                        if (board[n[0]][n[1]] == 0) {
                            moves.add(new int[] { n[0], n[1], board[r][c] + 1 });
                        }
                    }
                }
            }
        }
        int tempopptotal = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] < 0) {
                    tempopptotal += board[i][j] * -1;
                }
            }
        }
        if (tempopptotal == opponenttotal) {
            oppblocked = true;
        } else {
            opponenttotal = tempopptotal;
        }
        // print out the board.

        for (int[] a : board) {
            System.out.println();
            for (int i : a) {
                System.out.print(i + "\t");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------");

        int[] mvalues = new int[moves.size()];
        int bestMove = 0;
        int bestInd = 0;
        for (int i = 0; i < mvalues.length; i++) {
            mvalues[i] = evalMove(moves.get(i), board);
            if (mvalues[i] > bestMove) {
                bestMove = mvalues[i];
                bestInd = i;
            }
        }

        if (moves.size() > 0) {
            if (flipped) {
                // flip a move
                move[0] = board.length - moves.get(bestInd)[0] - 1;
                move[1] = board[0].length - moves.get(bestInd)[1] - 1;
                move[2] = moves.get(bestInd)[2];
                return move;
            }
            return moves.get(bestInd);
        } else {
            return new int[0];
        }

    }

    public int evalMove(int[] move, int[][] board) {
        int value = 0;
        final int INDV = 100; // weighting for higher indices (we are always top left, higher moves us down
                              // and across)
        final int VALV = 10000; // weighting for higher values
        final int VOPP = 1000; // weighting for opponents moves, increase to make more aggressive blocking.
        final int VWAL = 100; // weighting to block against a wall
        final int VHOR = 10; // weighting to move horizonally once the opponent has been encountered.

        value += move[0] * INDV;
        value += move[1] * INDV;
        value += move[2] * VALV;
        // there's only 8 spots, and the loop would be complicated, time for the most
        // cancerous if condition.
        try {
            if (board[move[0] - 1][move[1] - 1] < 0) {
                value += VOPP; // this is an opponents move, we like these
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL; // this is a wall, we also like these, but less.
        }
        try {
            if (board[move[0] - 1][move[1]] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }
        try {
            if (board[move[0] - 1][move[1] + 1] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }
        try {
            if (board[move[0]][move[1] - 1] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }
        try {
            if (board[move[0]][move[1] + 1] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }
        try {
            if (board[move[0] + 1][move[1] - 1] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }
        try {
            if (board[move[0] + 1][move[1]] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }
        try {
            if (board[move[0] + 1][move[1] + 1] < 0) {
                value += VOPP;
                oppenc = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            value += VWAL;
        }

        if (oppenc) {// pref horizontal to our own to create blocked sections
            try {
                if (board[move[0] + 1][move[1]] > 0) {
                    value += VHOR;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                value += VWAL * 10;
            }
            try {
                if (board[move[0] - 1][move[1]] > 0) {
                    value += VHOR;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                value += VWAL * 10;
            }
            try {
                if (board[move[0]][move[1] - 1] > 0) {
                    value += VHOR;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                value += VWAL * 10;
            }
            try {
                if (board[move[0]][move[1] + 1] > 0) {
                    value += VHOR;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                value += VWAL * 10;
            }
        }
        return value;
    }

    public int[][] flip(int[][] board) {
        int[][] fboard = new int[board.length][board[0].length];
        for (int i = 0; i < fboard.length; i++) {
            for (int j = 0; j < fboard[0].length; j++) {
                fboard[i][j] = board[board.length - i - 1][board[0].length - j - 1];
            }
        }
        return fboard;
    }

    /**
     * What is your name?
     *
     * @return Your name.
     */
    public String getName() {
        return name;
    };

}
