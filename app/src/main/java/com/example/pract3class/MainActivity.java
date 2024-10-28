package com.example.pract3class;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    SharedPreferences themeSettings;
    SharedPreferences.Editor settingsEditor;
    ImageButton imageTheme;
    private Button[][] buttons = new Button[3][3];
    private GameLogic gameLogic;
    private TextView statisticsTextView;
    private boolean isPlayerVsBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Скрываем текст заголовка
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        themeSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        if (!themeSettings.contains("MODE_NIGHT_NO")) {
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_NO", false);
            settingsEditor.apply();
        } else {
            setCurentTheme();
        }

        setContentView(R.layout.activity_main); // Вызов здесь

        imageTheme = findViewById(R.id.img);
        updateImageTheme();

        imageTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeSettings.getBoolean("MODE_NIGHT_NO", false)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    settingsEditor = themeSettings.edit();
                    settingsEditor.putBoolean("MODE_NIGHT_NO", false);
                    settingsEditor.apply();
                    Toast.makeText(MainActivity.this, "Темная тема отключена", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    settingsEditor = themeSettings.edit();
                    settingsEditor.putBoolean("MODE_NIGHT_NO", true);
                    settingsEditor.apply();
                    Toast.makeText(MainActivity.this, "Темная тема включена", Toast.LENGTH_SHORT).show();
                }
                restartActivity(); // Перезапускаем активность для применения изменений
            }
        });

        gameLogic = new GameLogic();

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(new ButtonClickListener(i, j));
            }
        }

        statisticsTextView = findViewById(R.id.statisticsTextView);

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> resetStatistics());

        updateStatistics();
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioPlayerVsBot) {
                    isPlayerVsBot = true; // Играть с роботом
                } else {
                    isPlayerVsBot = false; // Играть с другом
                }
            }
        });
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void updateImageTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_NO", false)) {
            imageTheme.setImageResource(R.drawable.ic_android_black_24dp);
        } else {
            imageTheme.setImageResource(R.drawable.suni);
        }
    }

    private void setCurentTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_NO", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            if (gameLogic.makeMove(row, col)) {
                buttons[row][col].setText(String.valueOf(gameLogic.getCurrentPlayer()));

                if (gameLogic.checkForWin()) {
                    gameLogic.incrementWins(gameLogic.getCurrentPlayer());
                    updateStatistics();
                    resetBoard();
                } else if (gameLogic.isBoardFull()) {
                    gameLogic.incrementDraws();
                    updateStatistics();
                    resetBoard();
                } else {
                    gameLogic.switchPlayer(); // Переключаем игрока

                    handleBotMove(); // Добавляем вызов для хода бота, если играем против него
                }
            }

        }
    }
    private void updateButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(String.valueOf(gameLogic.getCell(i, j))); // Предполагается метод getCell для получения состояния ячейки
            }
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                gameLogic.clearCell(i, j); // Очищаем состояние ячейки в логике игры

            }
        }
    }

    private void resetStatistics() {
        gameLogic.resetStatistics();
        updateStatistics();
    }

    private void updateStatistics() {
        statisticsTextView.setText("Победы X: " + gameLogic.getPlayerXWins() +
                "\nПобеды O: " + gameLogic.getPlayerOWins() +
                "\nНичьи: " + gameLogic.getDraws());
    }
    private void handleBotMove() {
        if (isPlayerVsBot && gameLogic.getCurrentPlayer() == 'O') { // Предположим, что бот — 'O'
            gameLogic.makeBotMove(); // Логика хода бота
            updateButtons(); // Обновляем отображение кнопок после хода бота

            // Проверка на выигрыш после хода бота
            if (gameLogic.checkForWin()) {
                gameLogic.incrementWins(gameLogic.getCurrentPlayer());
                updateStatistics(); // Обновляем статистику
                resetBoard(); // Сбрасываем игровое поле
            } else if (gameLogic.isBoardFull()) {
                gameLogic.incrementDraws(); // Увеличиваем счетчик ничьих
                updateStatistics(); // Обновляем статистику
                resetBoard(); // Сбрасываем игровое поле
            }

            gameLogic.switchPlayer(); // Переключаем обратно на игрока X
        }
    }
}