//Written by Garrett Dawson
package com.example.ralston.guesstimationfinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class GameWinner extends AppCompatActivity {
    //variables for the class//
    ConnectionClass connectionClass;
    public Connection con;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    public String userName = "";
    public int userPoints;
    public String nameExtra;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_winner);

        //get user name
        Bundle bundle = getIntent().getExtras();
        nameExtra = bundle.toString();

        arrayList = new ArrayList<String>();
        //PlayerView is just the name of the list view//
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_game_winner, R.id.PlayerView, arrayList);
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
                    names = new ArrayList<String>();
                    ResultSet rs = statement.executeQuery(query);
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

    //main menu button that takes user back to home page//
    public void mainMenuButton(View v) {
        Intent i = new Intent(getApplicationContext(), HostedGames.class);
        i.putExtra(nameExtra,nameExtra);
        startActivity(i);
    }
}

