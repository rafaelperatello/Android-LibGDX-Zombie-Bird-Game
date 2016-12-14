package com.peralta.zombiebird;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.peralta.zombiebird.intefaces.GameListener;

public class AndroidLauncher extends AndroidApplication implements GameListener {
    private final String KEY_HIGH_SCORE = "KEY_HIGH_SCORE";
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int lastHighScore = sharedPref.getInt(KEY_HIGH_SCORE, 0);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new ZBGame(this, lastHighScore), config);
    }

    @Override
    public void onHighScoreUpdate(int score) {
        System.out.println("high score updade: " + score);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_HIGH_SCORE, score);
        editor.commit();
    }
}
