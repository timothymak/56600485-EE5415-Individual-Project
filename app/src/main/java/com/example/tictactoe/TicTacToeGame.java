package com.example.tictactoe;

import android.util.Log;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeGame {

    // Characters used to represent the human, computer, and open spots
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';
    public static final int BOARD_SIZE = 9;
    private char mBoard[] = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private Random mRand;
    private int computerMove;

    /** The constructor of the TicTacToeGame have to be remove some code as follows */
    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
        char turn = HUMAN_PLAYER; // Human starts first
        int win = 0; // Set to 1, 2, or 3 when game is over
        computerMove = 0;
    }

    public char[] getmBoard() {
        return this.mBoard;
    }

    public void setmBoard(char[] mBoard) {
        this.mBoard = mBoard;
    }

    /** Clear the board of all X's and O's. */
    public void clearBoard() {
        computerMove = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /** Set the given player at the given location on the game board * */
    public void setMove(char player, int location) {
        mBoard[location] = player;
    }

    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    private void getUserMove() {
        // Eclipse throws a NullPointerException with Console.readLine
        // Known bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=122429
        //Console console = System.console();

        Scanner s = new Scanner(System.in);

        int move = -1;

        while (move == -1) {
            try {
                System.out.print("Enter your move: ");
                move = s.nextInt();

                while (move < 1 || move > BOARD_SIZE ||
                        mBoard[move-1] == HUMAN_PLAYER || mBoard[move-1] == COMPUTER_PLAYER) {

                    if (move < 1 || move > BOARD_SIZE)
                        System.out.println("Please enter a move between 1 and " + BOARD_SIZE + ".");
                    else
                        System.out.println("That space is occupied.  Please choose another space.");

                    System.out.print("Enter your move: ");
                    move = s.nextInt();
                }
            }
            catch (InputMismatchException ex) {
                System.out.println("Please enter a number between 1 and " + BOARD_SIZE + ".");
                s.next();  // Get next line so we start fresh
                move = -1;
            }
        }

        mBoard[move-1] = HUMAN_PLAYER;
    }

    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    public int getComputerMove(int computerLevel, boolean firstMove) {
        int move;
        int[] corners = new int[]{0, 2, 6, 8};
        computerMove += 1;
        if (computerLevel != 0) {
            if (computerLevel == 2 && firstMove) {
                // start at one corner ; 0, 2, 6, 8
                move = corners[mRand.nextInt(4)];
                mBoard[move] = COMPUTER_PLAYER;
                return move;
            }
            // First see if there's a move O can make to win
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    char curr = mBoard[i];
                    mBoard[i] = COMPUTER_PLAYER;
                    if (checkForWinner() == 3) {
                        System.out.println("Computer is moving to " + (i + 1));
                        return i;
                    }
                    else
                        mBoard[i] = curr;
                }
            }
            // See if there's a move O can make to block X from winning
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    char curr = mBoard[i]; // Save the current number
                    mBoard[i] = HUMAN_PLAYER;
                    if (checkForWinner() == 2) {
                        mBoard[i] = COMPUTER_PLAYER;
                        System.out.println("Computer is moving to " + (i + 1));
                        return i;
                    }
                    else
                        mBoard[i] = curr;
                }
            }
            if (computerLevel == 2) {
//                if (mBoard[4] != HUMAN_PLAYER) {
                if (computerMove == 3 && (mBoard[4] == COMPUTER_PLAYER)) {
                    for (int i : corners) {
                        if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                            mBoard[i] = COMPUTER_PLAYER;
                            if (winnable()) {
                                return i;
                            }
                            else {
                                mBoard[i] = OPEN_SPOT;
                            }
                        }
                    }
                }

                if (computerMove == 2 && (mBoard[1] == HUMAN_PLAYER || mBoard[3] == HUMAN_PLAYER || mBoard[5] == HUMAN_PLAYER || mBoard[7] == HUMAN_PLAYER)) {
                    move = 4;
                    mBoard[move] = COMPUTER_PLAYER;
                    return move;
                }
                if (mBoard[0] == OPEN_SPOT || mBoard[2] == OPEN_SPOT || mBoard[6] == OPEN_SPOT || mBoard[8] == OPEN_SPOT) {
                    do {
                        move = corners[mRand.nextInt(4)];
                    } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
                    mBoard[move] = COMPUTER_PLAYER;
                    return move;
                }
//                }
            }
        }
        // Generate random move
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        mBoard[move] = COMPUTER_PLAYER;
        return move;
    }

    private boolean winnable() {
        int winConditions = 0;
        for (int i = 0 ; i < BOARD_SIZE ; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    winConditions += 1;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        if (winConditions > 1) {
            return true;
        }
        else {
            return false;
        }
    }

}
