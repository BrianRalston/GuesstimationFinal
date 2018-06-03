//Written by Garrett Dawson
package com.example.ralston.guesstimationfinal;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RoundWinner extends AppCompatActivity {
    ConnectionClass connectionClass;
    public Connection con;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    public String userName = "";
    public int userPoints;
    private Timer timer;
    private static final String Format = "%02d:%02d";
    public TextView Timeleft;
    public String nameExtra;
    public static int SQLRoundNumber = 0;
    public int GID = getIntent().getIntExtra("GID", 0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_winner);

        //get user name
        Bundle bundle = getIntent().getExtras();
        nameExtra = bundle.toString();


        arrayList = new ArrayList<String>();
        //PlayerView is just the name of the list view//
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_round_winner, R.id.PlayerView, arrayList);
        ListView listView = (ListView) findViewById(R.id.PlayerView);
        listView.setAdapter(arrayAdapter);
        //Execute statement for user name//

    }

    public class ShowNameAndScore extends AsyncTask<String, String, ArrayList<String>> {
        String status = "";


        @Override
        protected void onPostExecute(ArrayList<String> e) {
            Iterator<String> iterator = e.iterator();
            while (iterator.hasNext()) {
                arrayAdapter.add(iterator.next().toString());
            }

            //Run timer for 15 seconds//
            new CountDownTimer(15000, 1) {
                public void onTick(long millisUntilFinished) {
                    Timeleft.setText('"' + String.format(Format, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }


                public void onFinish() {
                    //get round number from database
                    ConnectionClass con = new ConnectionClass();
                    Statement stmt;

                    try {
                        stmt = con.CONN().createStatement();
                        String query = "select RoundNumber from Game where GID = " + GID;

                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            SQLRoundNumber = rs.getInt("RoundNumber");
                        }

                        rs.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (SQLRoundNumber < 5) {
                        //increment round number in database

                        try {
                            stmt = con.CONN().createStatement();
                            String query = "update Game set RoundNumber = RoundNumber + 1 where GID = " + GID;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Start next round
                        //Intent i = new Intent(getApplicationContext(), /*may need to change this name*/ GameQuestion.class);
                        //i.putExtra(nameExtra,nameExtra);
                        //startActivity(i);

                    } else if (SQLRoundNumber == 5) {
                        //if users have played five rounds on game, sends players to game winner page
                        Intent i = new Intent(getApplicationContext(), GameWinner.class);
                        i.putExtra(nameExtra,nameExtra);
                        startActivity(i);

                    } else {
                        Toast t = Toast.makeText(getApplicationContext(), "There was an error creating your game.", Toast.LENGTH_LONG);
                        t.show();
                    }
                }
            }.start();

        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> names = null;
            try {
                ConnectionClass connectionClass = new ConnectionClass();
                //connect to database//
                if (connectionClass == null) {
                    status = "Check your internet connection!";
                } else {
                    String query = "select * from Player where Username = '" + userName + "'";
                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                    names = new ArrayList<String>();
                    while (rs.next()) {
                        String a = String.valueOf(rs.getInt(1));
                        String b = rs.getString(2);

                        names.add(a + " |  " + b);
                    }
                }
            } catch (SQLException e) {
                Log.d("sql error", e.getMessage());
            }
            return names;
        }
    }


}
