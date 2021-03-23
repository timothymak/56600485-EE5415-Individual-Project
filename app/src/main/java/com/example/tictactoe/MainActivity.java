package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    // Restart Button
    private Button startButton;
    // Game Over
    private Boolean mGameOver;

    private Boolean enableUi;
    private Menu menu;

    private HashMap<Integer, Integer> settings;
    private HashMap<String, Integer> scoreStat;
    private TextView playerWinCountTextView;
    private TextView computerWinCountTextView;
    private TextView drawCountTextView;
    private TextView scoreBoardTextView;
    private TextView playerWinTextView;
    private TextView computerWinTextView;
    private TextView drawTextView;
    private ProgressBar loading;

    private SoundPool soundPool;
    private int[] soundIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSounds();
        loadSettings();
        Log.v("set locale", getResources().getStringArray(R.array.settings_language_codes)[this.settings.get(0)]);
        Log.v("systemLang", Locale.getDefault().getLanguage());
        setLocale(this, getResources().getStringArray(R.array.settings_language_codes)[this.settings.get(0)]);
        this.initTextView();

        mGame = new TicTacToeGame();
        mBoardButtons = new Button[mGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.button0);
        mBoardButtons[1] = (Button) findViewById(R.id.button1);
        mBoardButtons[2] = (Button) findViewById(R.id.button2);
        mBoardButtons[3] = (Button) findViewById(R.id.button3);
        mBoardButtons[4] = (Button) findViewById(R.id.button4);
        mBoardButtons[5] = (Button) findViewById(R.id.button5);
        mBoardButtons[6] = (Button) findViewById(R.id.button6);
        mBoardButtons[7] = (Button) findViewById(R.id.button7);
        mBoardButtons[8] = (Button) findViewById(R.id.button8);
        mInfoTextView = (TextView) findViewById(R.id.information);
        loading = (ProgressBar) findViewById(R.id.loading);
        mGame = new TicTacToeGame();
        startNewGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadScore();
        updateScore(null);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInfoTextView.setText(savedInstanceState.getString("mInfoTextView"));
        mInfoTextView.setTextColor(savedInstanceState.getInt("mInfoTextViewColor"));
        char[] mBoard = savedInstanceState.getCharArray("mBoard");
        for (int i = 0 ; i < mBoard.length ; i++) {
            if (mBoard[i] != ' ') {
                setMove(mBoard[i], i);
            }
        }
        mGameOver = savedInstanceState.getBoolean("mGameOver");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mInfoTextView", mInfoTextView.getText().toString());
        outState.putInt("mInfoTextViewColor", mInfoTextView.getCurrentTextColor());
        outState.putCharArray("mBoard", mGame.getmBoard());
        outState.putBoolean("mGameOver", mGameOver);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the aaction bar if it is present
        this.menu = menu;
        getMenuInflater().inflate(R.menu.option_menu, this.menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.option_menu_settings:
                SettingsDialog settingsDialog = new SettingsDialog(this.settings);
                settingsDialog.show(getSupportFragmentManager(), "SettingsDialog");
                return true;
            case R.id.option_menu_about:
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), "AboutDialog");
                return true;
            case R.id.option_menu_exit:
                this.finish();
                System.exit(0);
        }
        return false;
    }

//    @Override
//    public boolean onPrepareOptionsMenu (Menu menu) {
//        for (int i = 0 ; i < menu.size() ; i++) {
//            menu.getItem(i).setEnabled(enableUi);
//        }
//        return true;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.soundPool.release();
//        this.soundPool = null;
    }

    private void initTextView() {
        this.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        this.startButton = (Button) findViewById(R.id.button_restart);
        this.startButton.setText(R.string.start_a_new_game);
        this.scoreBoardTextView = (TextView) findViewById(R.id.scoreboard_textview);
        this.scoreBoardTextView.setText(R.string.score_board);
        this.playerWinTextView = (TextView) findViewById(R.id.player_win_textview);
        this.playerWinTextView.setText(R.string.player);
        this.computerWinTextView = (TextView) findViewById(R.id.computer_win_textview);
        this.computerWinTextView.setText(R.string.computer);
        this.drawTextView = (TextView) findViewById(R.id.draw_textview);
        this.drawTextView.setText(R.string.draw);
    }

    private void initSettings() {
        String systemLang = Locale.getDefault().getDisplayLanguage();
        Log.v("systemLang", systemLang + " " + Locale.getDefault().getLanguage());
        List<String> availableLang = Arrays.asList(getResources().getStringArray(R.array.settings_languages));
        int currentLang = 0;
        if(availableLang.contains(systemLang)) {
            currentLang = availableLang.indexOf(systemLang);
        }
        int currentDiff = 0;
        int currentFirst = 0;

        this.settings.put(0, currentLang);
        this.settings.put(1, currentDiff);
        this.settings.put(2, currentFirst);
        this.settings.put(3, 0);

        this.saveSettings(this.settings);
    }

    public void saveSettings(HashMap<Integer, Integer> settings) {
        SharedPreferences pref = getSharedPreferences("Tic Tac Toe", MODE_PRIVATE);
        for (Map.Entry<Integer, Integer> entry : settings.entrySet()) {
            pref.edit().putInt(String.valueOf(entry.getKey()), entry.getValue()).apply();
        }
    }

    public void loadSettings() {
        this.settings = new HashMap<>();
        SharedPreferences pref = getSharedPreferences("Tic Tac Toe", MODE_PRIVATE);
        if (!pref.contains("0") || !pref.contains("1") || !pref.contains("2") || !pref.contains("3")) {
            // 0 : language ; 1 : difficulty ; 2 : who goes first ; 3 : reset
            this.initSettings();
        }

        this.settings.put(0, (Integer) pref.getInt(String.valueOf(0), 0));
        this.settings.put(1, (Integer) pref.getInt(String.valueOf(1), 0));
        this.settings.put(2, (Integer) pref.getInt(String.valueOf(2), 0));
        this.settings.put(3, (Integer) pref.getInt(String.valueOf(3), 0));
    }

    public void changeSettings(HashMap<Integer, Integer> settings) {
        this.settings = settings;
        if (this.settings.get(3) == 1) {
            Log.v("reset score", "reset now");
            this.resetScore();
            this.settings.put(3, 0);
        }
        this.saveSettings(this.settings);
//        Log.v("new Lang", getResources().getStringArray(R.array.settings_languages)[this.settings.get(0)]);
//        Log.v("new Diff", getResources().getStringArray(R.array.settings_difficulties)[this.settings.get(1)]);
        setLocale(this, getResources().getStringArray(R.array.settings_language_codes)[this.settings.get(0)]);
        this.finish();
        this.startActivity(this.getIntent());
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void initScore() {
        this.scoreStat.put(getResources().getString(R.string.player_string), 0);
        this.scoreStat.put(getResources().getString(R.string.computer_string), 0);
        this.scoreStat.put(getResources().getString(R.string.draw_string), 0);
    }

    public void saveScore() {
        SharedPreferences pref = getSharedPreferences("Tic Tac Toe", MODE_PRIVATE);
        pref.edit().putInt(getResources().getString(R.string.player_string), this.scoreStat.get(getResources().getString(R.string.player_string))).apply();
        pref.edit().putInt(getResources().getString(R.string.computer_string), this.scoreStat.get(getResources().getString(R.string.computer_string))).apply();
        pref.edit().putInt(getResources().getString(R.string.draw_string), this.scoreStat.get(getResources().getString(R.string.draw_string))).apply();
    }

    public void loadScore() {
        this.scoreStat = new HashMap<>();
        SharedPreferences pref = getSharedPreferences("Tic Tac Toe", MODE_PRIVATE);
        if (!pref.contains(getResources().getString(R.string.player_string)) || !pref.contains(getResources().getString(R.string.computer_string)) || !pref.contains(getResources().getString(R.string.draw_string))) {
            this.initScore();
        }

        this.scoreStat.put(getResources().getString(R.string.player_string), pref.getInt(getResources().getString(R.string.player_string), 0));
        this.scoreStat.put(getResources().getString(R.string.computer_string), pref.getInt(getResources().getString(R.string.computer_string), 0));
        this.scoreStat.put(getResources().getString(R.string.draw_string), pref.getInt(getResources().getString(R.string.draw_string), 0));
    }

    public void updateScore(String winner) {
        if (winner != null) {
            this.scoreStat.put(winner, this.scoreStat.get(winner) + 1);
            saveScore();
        }
        playerWinCountTextView = (TextView) findViewById(R.id.player_win_count_textview);
        computerWinCountTextView = (TextView) findViewById(R.id.computer_win_count_textview);
        drawCountTextView = (TextView) findViewById(R.id.draw_count_textview);
        playerWinCountTextView.setText(String.valueOf(this.scoreStat.get(getResources().getString(R.string.player_string))));
        computerWinCountTextView.setText(String.valueOf(this.scoreStat.get(getResources().getString(R.string.computer_string))));
        drawCountTextView.setText(String.valueOf(this.scoreStat.get(getResources().getString(R.string.draw_string))));
    }

    public void resetScore() {
        SharedPreferences pref = getSharedPreferences("Tic Tac Toe", MODE_PRIVATE);
        pref.edit().remove(getResources().getString(R.string.player_string)).apply();
        pref.edit().remove(getResources().getString(R.string.computer_string)).apply();
        pref.edit().remove(getResources().getString(R.string.draw_string)).apply();
    }

    //--- Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        mGame.clearBoard();
        //---Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        int first = this.settings.get(2);
        // Random
        if (first == 2) {
            first = (int) Math.round(Math.random());
        }
        if (first == 1) {
            //---Computer goes first
            int move = mGame.getComputerMove(this.settings.get(1), true);
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.player_turn_text);
        }
        else {
            //---Human goes first
            mInfoTextView.setText(R.string.player_goes_first_text);
        }
    }

    //---Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        @Override
        public void onClick(View v) {
            if (mGameOver == false) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);
                    int winner = mGame.checkForWinner();
                    checkGameState(winner);

//                    --- If no winner yet, let the computer make a move
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.android_turn_text);
                        enableUi = false;
//                        unlockUi(false);
                        loading.setVisibility(View.VISIBLE);
                        List<Integer> toBeReleased = lockBoard();
                        int move = mGame.getComputerMove(settings.get(1), false);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                unlockUi(true);
                                unlockBoard(toBeReleased);
                                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                int winner = mGame.checkForWinner();
                                checkGameState(winner);
                            }
                        }, getResources().getIntArray(R.array.wait_time)[settings.get(1)]);
                    }
                }
            }
        }
    }

    private void checkGameState(int winner) {
        int soundId;
        if (winner == 0) {
            mInfoTextView.setTextColor(getResources().getColor(R.color.black));
            mInfoTextView.setText(R.string.player_turn_text);
            soundId = 3;
        } else if (winner == 1) {
            mInfoTextView.setTextColor(getResources().getColor(R.color.dark_blue));
            mInfoTextView.setText(R.string.game_tie_text);
            updateScore(getResources().getString(R.string.draw_string));
            mGameOver = true;
            soundId = 2;
        } else if (winner == 2) {
            mInfoTextView.setTextColor(getResources().getColor(R.color.green));
            mInfoTextView.setText(R.string.player_won_text);
            updateScore(getResources().getString(R.string.player_string));
            mGameOver = true;
            soundId = 0;
        } else {
            mInfoTextView.setTextColor(getResources().getColor(R.color.dark_red));
            mInfoTextView.setText(R.string.android_won_text);
            updateScore(getResources().getString(R.string.computer_string));
            mGameOver = true;
            soundId = 1;
        }
        loading.setVisibility(View.GONE);
        playSound(soundIds[soundId]);
    }

    private void playSound(int soundId) {
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }

    private void loadSounds() {
        this.soundPool = new SoundPool.Builder().build();
        TypedArray allSounds = getResources().obtainTypedArray(R.array.sounds);
        this.soundIds = new int[allSounds.length()];
        for (int i = 0 ; i < allSounds.length() ; i++) {
            soundIds[i] = soundPool.load(this, allSounds.getResourceId(i, -1), 1);
        }
    }

    private List<Integer> lockBoard() {
        List<Integer> toBeReleased = new ArrayList<Integer>();
        for (int i = 0; i < mBoardButtons.length; i++) {
            if (mBoardButtons[i].isEnabled()) {
                toBeReleased.add(i);
                mBoardButtons[i].setEnabled(false);
            }
        }
        return toBeReleased;
    }

    private void unlockBoard(List<Integer> toBeReleased) {
        for (int i : toBeReleased) {
            mBoardButtons[i].setEnabled(true);
        }
    }

    private void unlockUi(boolean b) {
        for (int i = 0 ; i < this.menu.size() ; i++) {
            this.menu.getItem(i).setEnabled(b);
        }
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(getResources().getColor(R.color.green));
        else
            mBoardButtons[location].setTextColor(getResources().getColor(R.color.dark_red));
    }

    //--- OnClickListener for Restart a New Game Button
    public void newGame(View v) {
        startNewGame();
    }

}