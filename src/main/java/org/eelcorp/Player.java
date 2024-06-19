package org.eelcorp;

import java.util.*;

public class Player {
    private enum Type {
        HUMAN,
        AI_EASY,
        AI_MID,
        AI_UNBEATABLE,
        AI_RAT
    }

    private Type playerType;
    private Board board;


    public Player(String type, Board board) {
        if (type.equals("user")) {
            playerType = Type.HUMAN;
        } else if (type.equals("easy")) {
            playerType = Type.AI_EASY;
        } else if (type.equals("medium")) {
            playerType = Type.AI_MID;
        } else if (type.equals("hard")) {
            playerType = Type.AI_UNBEATABLE;
        }

        this.board = board;
    }

    public int[] makeMove() throws Exception {
        int[] coords = switch (playerType) {
            case AI_EASY -> {
                System.out.println("Making move level \"easy\"");
                yield easyMove();
            }
            case AI_MID -> {
                System.out.println("Making move level \"medium\"");
                yield mediumMove();
            }
            case AI_UNBEATABLE -> {
                System.out.println("Making move level \"hard\"");
                yield hardMove();
            }
            case HUMAN -> humanMove();
            default -> throw new Exception("This is nonsense");
        };

        return coords;
    }

    public int[] humanMove() {
        char[][] gameBoard = board.getBoard();

        int[] coords = parseCoords();

        while (gameBoard[coords[0]][coords[1]] != ' ') {
            System.out.println("This cell is occupied! Choose another one!");
            coords = parseCoords();
        }

        return coords;
    }

    private int[] parseCoords() {
        int[] coords = new int[2];
        boolean verified = false;
        Scanner scanner = new Scanner(System.in);

        while (!verified) {
            System.out.println("Enter the coordinates:");
            try {
                String[] input = scanner.nextLine().split(" ");

                coords[0] = Integer.parseInt(input[0]);
                coords[1] = Integer.parseInt(input[1]);

                if (coords[0] < 1 || coords[0] > 3 || coords[1] < 1 || coords[1] > 3) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else {
                    verified = true;
                }

            } catch (Exception e) {
                System.out.println("You should enter numbers!");
            }
        }

        coords[0]--;
        coords[1]--;

        return coords;

    }

    private char[][] copyBoard(char[][] oldBoard) {
        char[][] newBoard = new char[3][3];

        for (int i = 0; i < oldBoard.length; i++) {
            for (int j = 0; j < oldBoard[i].length; j++) {
                newBoard[i][j] = oldBoard[i][j];
            }
        }
        return newBoard;
    }

    private int[] hardMove() {
        char[][] gameBoard = copyBoard(board.getBoard());
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];

        char playerSign = board.getGameState().equals("X_MOVE") ? 'X' : 'O';

        List<Integer[]> legalMoves = detectEmpty(gameBoard);

        for (Integer[] move : legalMoves) {
            char[][] coolBoard = copyBoard(gameBoard);
            coolBoard[move[0]][move[1]] = playerSign;
            int score = minimax(coolBoard, playerSign, false, 0);
            if (score > bestScore) {
                bestScore = score;
                bestMove[0] = move[0];
                bestMove[1] = move[1];
            }
        }

        return bestMove;
    }

    private int minimax(char[][] board, char playerSign, boolean isMaximizing, int depth) {
        int currentScore;
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        char opponentSign = playerSign == 'X' ? 'O' : 'X';

        List<Integer[]> remainingMoves = detectEmpty(board);

        if (isWon(board, playerSign)) {
            return 10 - depth;
        } else if (isWon(board, opponentSign)) {
            return depth - 10;
        } else if (remainingMoves.isEmpty()) {
            return 0;
        }

        for (Integer[] remainingMove : remainingMoves) {
            if (isMaximizing) {
                board[remainingMove[0]][remainingMove[1]] = playerSign;
                currentScore = minimax(board, playerSign, !isMaximizing, depth + 1);
                bestScore = Math.max(currentScore, bestScore);
            } else {
                board[remainingMove[0]][remainingMove[1]] = opponentSign;
                currentScore = minimax(board, playerSign, !isMaximizing, depth + 1);
                bestScore = Math.min(currentScore, bestScore);
            }

            board[remainingMove[0]][remainingMove[1]] = ' ';
        }

        return bestScore;
    }

    private boolean isWon(char[][] gameBoard, char playerSign) {
        for (int i = 0; i < gameBoard.length; i++) {
            int xCounter = 0;
            int oCounter = 0;
            for (int j = 0; j < gameBoard[i].length; j++) {

                if (gameBoard[i][j] == 'X') {
                    xCounter++;
                } else if (gameBoard[i][j] == 'O') {
                    oCounter++;
                }

                if (xCounter == 3 && playerSign == 'X') {
                    return true;
                } else if (oCounter == 3 && playerSign == 'O') {
                    return true;
                }
            }

            xCounter = 0;
            oCounter = 0;

            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[j][i] == 'X') {
                    xCounter++;
                } else if (gameBoard[j][i] == 'O') {
                    oCounter++;
                }

                if (xCounter == 3 && playerSign == 'X') {
                    return true;
                } else if (oCounter == 3 && playerSign == 'O') {
                    return true;
                }
            }
        }

        String diag1 = new String((new char[]{gameBoard[0][0], gameBoard[1][1], gameBoard[2][2]}));
        String diag2 = new String((new char[]{gameBoard[0][2], gameBoard[1][1], gameBoard[2][0]}));

        if ((diag2.equals("XXX") || diag1.equals("XXX")) && playerSign == 'X') {
            return true;
        } else if ((diag2.equals("OOO") || diag1.equals("OOO")) && playerSign == 'O') {
            return true;
        }

        return false;
    }

    private List<Integer[]> detectEmpty(char[][] gameBoard) {
        List<Integer[]> emptySpots = new ArrayList<>();

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] == ' ') {
                    emptySpots.add(new Integer[]{i, j});
                }
            }
        }

        return emptySpots;
    }


    private int[] easyMove() {
        Random generator = new Random();
        char[][] gameBoard = board.getBoard();

        List<Integer[]> validMoves = new ArrayList<>();

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] == ' ') {
                    Integer[] coords = {i, j};
                    validMoves.add(coords);
                }
            }
        }

        int selection = generator.nextInt(0, validMoves.size());

        return Arrays.stream(validMoves.get(selection)).mapToInt(Integer::intValue).toArray();
    }

    private int[] mediumMove() {
        Random generator = new Random();
        char[][] gameBoard = board.getBoard();
        boolean xTurn = board.getGameState().equals("X_MOVE");
        String winRegex = xTurn ? "^(?=[^X]*X[^X]*X[^X]*$)(?=[^\\s]*\\s[^\\s]*$).*" : "^(?=[^O]*O[^O]*O[^O]*$)(?=[^\\s]*\\s[^\\s]*$).*";
        String blockRegex = xTurn ? "^(?=[^O]*O[^O]*O[^O]*$)(?=[^\\s]*\\s[^\\s]*$).*" : "^(?=[^X]*X[^X]*X[^X]*$)(?=[^\\s]*\\s[^\\s]*$).*";
        List<Integer[]> blockCoords = new ArrayList<>();

        for (int i = 0; i < gameBoard.length; i++) {
            StringBuilder lineTest = new StringBuilder();

            for (int j = 0; j < gameBoard[i].length; j++) {
                lineTest.append(gameBoard[i][j]);
            }

            if (lineTest.toString().matches(winRegex)) {
                return new int[]{i, lineTest.toString().indexOf(" ")};
            } else if (lineTest.toString().matches(blockRegex)) {
                blockCoords.add(new Integer[]{i, lineTest.toString().indexOf(" ")});
            }

            lineTest.setLength(0);

            for (int j = 0; j < gameBoard[i].length; j++) {
                lineTest.append(gameBoard[j][i]);
            }

            if (lineTest.toString().matches(winRegex)) {
                return new int[]{lineTest.toString().indexOf(" "), i};
            } else if (lineTest.toString().matches(blockRegex)) {
                blockCoords.add(new Integer[]{lineTest.toString().indexOf(" "), i});
            }
        }

        String diag1 = new String((new char[]{gameBoard[0][0], gameBoard[1][1], gameBoard[2][2]}));

        if (diag1.matches(winRegex)) {
            System.out.println(diag1.indexOf(" "));
            return new int[]{diag1.indexOf(" "), diag1.indexOf(" ")};
        } else if (diag1.matches(blockRegex)) {
            blockCoords.add(new Integer[]{diag1.indexOf(" "), diag1.indexOf(" ")});
        }

        String diag2 = new String((new char[]{gameBoard[0][2], gameBoard[1][1], gameBoard[2][0]}));

        if (diag2.matches(winRegex)) {
            return new int[]{diag2.indexOf(" "), 2 - diag2.indexOf(" ")};
        } else if (diag2.matches(blockRegex)) {
            blockCoords.add(new Integer[]{diag2.indexOf(" "), 2 - diag2.indexOf(" ")});
        }

        if (!blockCoords.isEmpty()) {
            return new int[]{blockCoords.get(0)[0], blockCoords.get(0)[1]};
        }

        return easyMove();
    }

}
