package com.miniprojet.dominique.madjohminiprojet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameActivity extends AppCompatActivity {
    private final String URL_STRING = "192.168.43.172/word", MSG_NOUVELLE_PARTIE = "Nouvelle partie", MSG_PERDU = "Vous avez perdu",
            MSG_GAGNEE = "Vous avez gagnez", MSG_ERR = "Une erreur est survenue (internet ou serveur)", SCORE_KEY = "score", COUNT_CURRENT_KEY = "countCurrent",
            FR_WORD_KEY = "fr", EN_WORD_KEY = "en", LEVEL_KEY = "levelkey";
    private MyAsynckTask task;
    private int currentScore = 10;
    private EditText editText;
    private TextView textView, score;
    private int level = 0; // help us to give word a little bit difficult or not
    private String motATrouver = "";
    private boolean countCurrent = false; // to know if the current words can be counted
    private Button valider, recommencer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // variables Views

        editText = (EditText) findViewById(R.id.reponseGame);
        textView = (TextView) findViewById(R.id.textAfficheeGame);
        valider = (Button) findViewById(R.id.buttonValiderGame);
        recommencer = (Button) findViewById(R.id.buttonRecommencerGame);
        score = (TextView) findViewById(R.id.scoreTextGame);

        // variables Game
        if (savedInstanceState != null) { // if there is game's data saved
            currentScore = savedInstanceState.getInt(SCORE_KEY);
            countCurrent = savedInstanceState.getBoolean(COUNT_CURRENT_KEY);
            motATrouver = savedInstanceState.getString(EN_WORD_KEY);
            editText.setText(motATrouver.length() > 0 ? (CharSequence) (motATrouver.charAt(0) + "") : "");
            textView.setText(savedInstanceState.getString(FR_WORD_KEY));
            level = savedInstanceState.getInt(LEVEL_KEY);
            if (!countCurrent)
                valider.setText(getResources().getString(R.string.refresh_game_main));
            if (currentScore == 0 || currentScore == 20) {
                valider.setVisibility(View.GONE);
                recommencer.setVisibility(View.VISIBLE);
            }
        } else {
            valider.setEnabled(false);
            task = new MyAsynckTask();
            task.execute(URL_STRING);
        }

        score.setText(getResources().getString(R.string.score_game_main) + " " + currentScore);

        recommencer.setOnClickListener(new View.OnClickListener() { // in case of the person win or lose
            @Override
            public void onClick(View view) { // little processing
                recommencer.setVisibility(View.GONE);
                valider.setVisibility(View.VISIBLE);
                motATrouver = "";
                level = 0;
                score.setText(getResources().getString(R.string.score_game_main) + " " + (currentScore = 10));
                textView.setText("---------------");
                task = new MyAsynckTask();
                task.execute(URL_STRING);
                Toast.makeText(GameActivity.this, MSG_NOUVELLE_PARTIE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * to save the some variables which will be lost when configuration changes
     *
     * @param b the bundle where we'll save
     */
    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putInt(LEVEL_KEY, level); // the level
        b.putInt(SCORE_KEY, currentScore); // the score
        b.putBoolean(COUNT_CURRENT_KEY, countCurrent); // if the current word is still usable
        b.putString(FR_WORD_KEY, textView.getText().toString()); // the french word
        b.putString(EN_WORD_KEY, motATrouver); // the english word
        super.onSaveInstanceState(b);
    }

    /**
     * determine if the game is over and make score process
     *
     * @return if the game is over
     */
    private boolean partieTerminer() {
        if (countCurrent) {// if the current words are counted
            countCurrent = false;
            String motEntree = editText.getText().toString().trim().toUpperCase();
            if (motEntree.equals(motATrouver)) { // the word entered is good
                ++currentScore;
                ++level;
            } else {
                --currentScore;
                --level;
            }
            score.setText(getResources().getString(R.string.score_game_main) + " " + currentScore);
            if (currentScore == 0 || currentScore == 20) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * callback function for the Valider's click
     *
     * @param v the Valider View
     */
    public void validerClick(View v) {
        if (!textView.getText().toString().isEmpty()) {
            if (!partieTerminer()) { // if the game is not over
                v.setEnabled(false);
                task = new MyAsynckTask(); // ask to other words
                task.execute(URL_STRING);
            } else {
                if (currentScore == 0) // lose
                    Toast.makeText(GameActivity.this, MSG_PERDU, Toast.LENGTH_SHORT).show();
                else if (currentScore == 20) // win
                    Toast.makeText(GameActivity.this, MSG_GAGNEE, Toast.LENGTH_SHORT).show();
                valider.setVisibility(View.GONE);
                recommencer.setVisibility(View.VISIBLE);
            }
        } else {// if there is no french word on the textView (if the first request doesn't work for example)
            v.setEnabled(false);
            task = new MyAsynckTask();
            task.execute(URL_STRING);
        }
    }

    @Override
    public void onStop() {
        if (task != null)
            task.cancel(false); // to stop the task if the user quit
        super.onStop();
    }

    private class MyAsynckTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // make the request and receive the response on an other thread to avoid blocking the UI Thread
            HttpURLConnection co = null;
            BufferedReader buff = null;
            try {
                URL u = new URL("http", "192.168.43.172", 8080, "/word/" + level);
                co = (HttpURLConnection) u.openConnection();
                co.setDoOutput(false);
                co.setConnectTimeout(5 * 1000); // timeout of 5second
                co.connect();
                // connect to the web server
                if (co.getResponseCode() != 200)
                    return null;
                StringBuilder sb = new StringBuilder();
                buff = new BufferedReader(new InputStreamReader(co.getInputStream()));
                String line;
                while ((line = buff.readLine()) != null) {// read data
                    sb.append(line);
                }
                //read the response
                if (sb.length() != 0)
                    return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { // close all we have to close finally
                if (buff != null) {
                    try {
                        buff.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (co != null) {
                    co.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    // deserialize the response
                    JSONObject jo = new JSONObject(s);
                    textView.setText(jo.getString("fr").toUpperCase());
                    motATrouver = jo.getString("en").toUpperCase();
                    // little processing
                    editText.setText("" + motATrouver.charAt(0));
                    countCurrent = true;
                    valider.setText(getResources().getString(R.string.confirm_game_main));
                } catch (JSONException e) { // if an error occur
                    e.printStackTrace();
                    Toast.makeText(GameActivity.this, MSG_ERR, Toast.LENGTH_LONG).show();
                    valider.setText(getResources().getString(R.string.refresh_game_main));
                }
            } else { // if an error occur
                Toast.makeText(GameActivity.this, MSG_ERR, Toast.LENGTH_LONG).show();
                valider.setText(getResources().getString(R.string.refresh_game_main));
            }
            valider.setEnabled(true);
        }
    }
}
