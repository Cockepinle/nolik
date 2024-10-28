package com.example.pract3class;

import java.util.Random;

public class GameLogic {

    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';
    private int playerXWins = 0;
    private int playerOWins = 0;
    private int draws = 0;
    public GameLogic() {
        currentPlayer = 'X'; // Начинаем с игрока X
    }

    public boolean makeMove(int row, int col) {
        if (board[row][col] == '\0') { // Проверка на пустую ячейку
            board[row][col] = currentPlayer;
            return true;
        }
        return false;
    }

    public boolean checkForWin() {
        // Проверка всех возможных выигрышных комбинаций
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) return true;
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) return true;
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) return true;

        return board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer;
    }
    public boolean makeBotMove() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (board[row][col] != '\0'); // Повторяем, пока не найдем пустую ячейку

        board[row][col] = currentPlayer; // Ставим символ бота
        return true; // Возвращаем true, так как ход был сделан
    }
    public char getCell(int row, int col) {
        return board[row][col];
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') return false;
            }
        }
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void incrementWins(char player) {
        if (player == 'X') playerXWins++;
        else playerOWins++;
    }

    public void incrementDraws() {
        draws++;
    }

    public void resetStatistics() {
        playerXWins = 0;
        playerOWins = 0;
        draws = 0;
    }
    public void clearCell(int row, int col) {
        board[row][col] = '\0'; // Сбрасываем состояние ячейки
    }

    public int getPlayerXWins() { return playerXWins; }
    public int getPlayerOWins() { return playerOWins; }
    public int getDraws() { return draws; }
}