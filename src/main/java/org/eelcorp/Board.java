package org.eelcorp;

import java.util.*;

public class Board {
    private char[][] board;
    private GameState gameState;
    private Player xPlayer;
    private Player oPlayer;

    private enum GameState {
        MAIN_MENU,
        X_MOVE,
        O_MOVE,
        EXITING
    }

    public Board() {
        board = new char[3][3];

        for (char[] chars : board) {
            Arrays.fill(chars, ' ');
        }

        gameState = GameState.MAIN_MENU;
    }

    public void playGame() {
        boolean gameOver = false;

        while (!gameOver) {
            switch (gameState) {
                case MAIN_MENU:
                    mainMenu();
                    break;
                case X_MOVE:
                    printBoard();
                    try {
                        int[] coords = xPlayer.makeMove();
                        board[coords[0]][coords[1]] = 'X';
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    gameOver = evalState();
                    gameState = GameState.O_MOVE;
                    break;
                case O_MOVE:
                    printBoard();
                    try {
                        int[] coords = oPlayer.makeMove();
                        board[coords[0]][coords[1]] = 'O';
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    gameOver = evalState();
                    gameState = GameState.X_MOVE;
                    break;
            }
        }

    }

    private void mainMenu() {
        Scanner scanner = new Scanner(System.in);

        boolean validInput = false;

        while (!validInput) {
            String[] input = scanner.nextLine().split(" ");

            switch (input[0]) {
                case "start":
                    if (input.length == 3) {
                        if (checkPlayerInput(input[1]) && checkPlayerInput(input[2])) {
                            xPlayer = new Player(input[1], this);
                            oPlayer = new Player(input[2], this);
                            validInput = true;
                            gameState = GameState.X_MOVE;
                        } else {
                            System.out.println("Bad parameters!");
                        }
                    } else {
                        System.out.println("Bad parameters!");
                    }
                    break;
                case "exit":
                    validInput = true;
                    System.exit(0);
                    break;
                default:
                    System.out.println("Bad parameters!");
                    break;
            }
        }
    }

    public char[][] getBoard() {
        return board;
    }


    public String getGameState() {
        return gameState.name();
    }


    private boolean checkPlayerInput(String input) {
        boolean matches = false;
        String[] allowedInputs = {"user", "easy", "medium", "hard"};

        for (String allowedInput : allowedInputs) {
            if (allowedInput.equals(input)) {
                matches = true;
            }
        }

        return matches;
    }

    public void printBoard() {
        System.out.println("---------");
        for (int i = 0; i < board.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    public boolean evalState() {

        for (int i = 0; i < board.length; i++) {
            int xCounter = 0;
            int oCounter = 0;

            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'X') {
                    xCounter++;
                } else if (board[i][j] == 'O') {
                    oCounter++;
                }

                if (xCounter == 3) {
                    printBoard();
                    System.out.println("X wins!");
                    return true;
                } else if (oCounter == 3) {
                    printBoard();
                    System.out.println("O wins!");
                    return true;
                }
            }

            xCounter = 0;
            oCounter = 0;

            for (int j = 0; j < board[i].length; j++) {
                if (board[j][i] == 'X') {
                    xCounter++;
                } else if (board[j][i] == 'O') {
                    oCounter++;
                }

                if (xCounter == 3) {
                    printBoard();
                    System.out.println("X wins");
                    return true;
                } else if (oCounter == 3) {
                    printBoard();
                    System.out.println("O wins");
                    return true;
                }
            }
        }

        String diag1 = new String((new char[]{board[0][0], board[1][1], board[2][2]}));
        String diag2 = new String((new char[]{board[0][2], board[1][1], board[2][0]}));

        if (diag2.equals("XXX") || diag1.equals("XXX")) {
            printBoard();
            System.out.println("X wins");
            return true;
        } else if (diag2.equals("OOO") || diag1.equals("OOO")) {
            printBoard();
            System.out.println("O wins");
            return true;
        }

        boolean notFull = false;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == ' ') {
                    notFull = true;
                    break;
                }
            }
        }

        if (notFull) {
            return false;
        } else {
            printBoard();
            System.out.println("Draw");
            return true;
        }

    }


}
