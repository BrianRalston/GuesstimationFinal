//Written by Andrew Starkey
package com.example.ralston.guesstimationfinal;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.Timer;

public class GameQuestion extends AppCompatActivity
{
    ConnectionClass connectionClass;
    public Connection con;
    String z;
    String playerAnswer;
    String questionString;
    ArrayList<String> questionStringArray = new ArrayList<String>();
    boolean finished = false;
    boolean isSuccess = false;
    public ResultSet results;
    EditText answerEditText;
    int receivedQs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_question);

        final String passedUserName = getIntent().getExtras().getString("passedUserName");
        final int GID = getIntent().getIntExtra("GID", 0);

        getValues();
        startRound();
        ConnectionClass con = new ConnectionClass();

    }

    public ResultSet getQuestions()
    {
        try
        {
            Log.i("Reach:", "It reached here!");
            connectionClass = new ConnectionClass();
            if(connectionClass == null)
            {
                z = "Check Your Internet Access!";
            }
            else
            {
                Statement stmt = con.createStatement();
                String query = "Select Question from Question where QID < 11";
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next())
                {
                    results = rs;
                    isSuccess = true;

                }
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = ex.getMessage();

            Log.d("sql error", z);
        }
        return results;
    }

    public void setQuestion()
    {
        TextView question = (TextView) findViewById(R.id.questionTextView);
        question.setText(questionString);
    }

    public void getValues()
    {
        ResultSet rs = getQuestions();
        try
        {
            for (int i = 0; rs.next(); i++)
            {
                questionStringArray.add(rs.getString("Question"));
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception:",ex.getMessage());
        }
    }


    public void submitButtonPressed(View view)
    {
        answerEditText = ((EditText) findViewById(R.id.answerEditText));
        playerAnswer = ((EditText) findViewById(R.id.answerEditText)).getText().toString();
        if (playerAnswer == "")
        {
            //toast the user to enter something
            Toast.makeText(GameQuestion.this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
        }
        else {

            try
            {
                Log.i("Reach:", "It reached here!");
                connectionClass = new ConnectionClass();
                if (connectionClass == null)
                {
                    z = "Check Your Internet Access!";
                }
                else {
                    Statement stmt = con.createStatement();
                    String query = "Insert into Question (Answer) values" + playerAnswer;
                    ResultSet rs = stmt.executeQuery(query);
                }

            }
            catch (Exception ex)
            {
                Log.e("Exception:", ex.getMessage());
            }
        }
    }

    public void nextPagePressed(View view)
    {
        receivedQs++;
        if (receivedQs == 2)
        {
            //send to next page (need receivedQs in database)
            Intent intent = new Intent(getApplicationContext(), Wager.class);
            startActivity(intent);
        }
        else
        {
            //toast that all users need to hit next page
            Toast.makeText(GameQuestion.this, "Must wait for all players to hit Next!", Toast.LENGTH_SHORT).show();
        }
    }

    public void startRound()
    {
        //pull round number from database rather than this?
        int roundNumber = RoundWinner.SQLRoundNumber;
        if(roundNumber < questionStringArray.size());

        if(!finished)
        {
            TextView questionNumEditText = (TextView) findViewById(R.id.questionNumTextView);
            questionNumEditText.setText("Question " + (roundNumber));
            questionString = questionStringArray.get(roundNumber);
            setQuestion();
        }
    }

}
