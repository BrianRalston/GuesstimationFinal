//Written by Brian Ralston
package com.example.ralston.guesstimationfinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.*;

public class GameCreate extends AppCompatActivity {

    // Declaring buttons and text
    public Button submit;
    public EditText gameName;
    public EditText gamePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        //get username from previous intent
        String passedUserName = getIntent().getExtras().getString("passedUserName");

        //cast items
        submit = (Button) findViewById(R.id.CreateButton);
        gameName = (EditText) findViewById(R.id.CreateName);
        gamePassword = (EditText) findViewById(R.id.CreatePassword);

        //onclick for create button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckLogin cl = new CheckLogin();
                //send user inputted info to string
                String gameNameString = gameName.getText().toString();
                String gamePasswordString = gamePassword.getText().toString();

                //run checklogin below with user inputted info
                if (gameNameString != "" && gamePasswordString != "") {
                    cl.gameNameString = gameNameString;
                    cl.gamePasswordString = gamePasswordString;
                    cl.execute("");
                }
            }
        });
    }

    public class CheckLogin extends AsyncTask<String, String, String> {
        //initialize variables
        String status = "";
        String gameNameString = "";
        String gamePasswordString = "";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String s) {
            if (isSuccess) {
                //get username to this page
                String passedUserName = getIntent().getExtras().getString("passedUserName");

                //send user to next page with username
                Intent i = new Intent(getApplicationContext(), HostedGames.class);
                i.putExtra("passedUserName", passedUserName);
                startActivity(i);
            } else {
                //if error tell user with toast
                Toast t = Toast.makeText(getApplicationContext(), "There was an error creating your game.", Toast.LENGTH_LONG);
                t.show();
            }
        }

        //insert query for all user data//
        @Override
        protected String doInBackground(String... params) {
            String passedUserName = getIntent().getExtras().getString("passedUserName");

            try {
                ConnectionClass con = new ConnectionClass();
                //if they cannot connect to the database this status is updated and the user toasted
                if (con == null) {
                    status = "Check your internet connection";
                    Toast t = Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_LONG);
                    t.show();
                } else {
                    //check if user actually input values for both edittexts
                    if (!gameName.equals("") && !gamePassword.equals("")) {
                        //insert the game info to the game table in the database
                        String query = "insert into Game (GameName, GamePassword) values ('" + gameNameString + "', '" + gamePasswordString + "')";
                        Statement stmt = con.CONN().createStatement();
                        stmt.execute(query);
                        //update status to success
                        status = ("Game created successfully");

                        //pull GID from game just created
                        String selectQuery = "select GID from Game where GameName = '" + gameNameString + "'";
                        ResultSet rs = stmt.executeQuery(selectQuery);
                        int GID = 0;
                        while (rs.next()) {
                            GID = rs.getInt("GID");
                        }
                        rs.close();

                        //insert GID into Roster table in database
                        String GIDQuery = "insert into Roster (GID) values (" + GID + ")";
                        Statement GIDstmt = con.CONN().createStatement();
                        stmt.execute(GIDQuery);
                        //return true
                        isSuccess = true;
                    } else { //else return false
                        isSuccess = false;
                    }
                }
            } catch (Exception e) { //catch for sql try-catch
                isSuccess = false;
                status = e.getMessage();

                Log.d("sql error", status);
            }
            return status;
        }
    }
}