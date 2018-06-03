//Written by Brian Ralston
package com.example.ralston.guesstimationfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.*;

public class UserLogin extends AppCompatActivity {

    ConnectionClass connectionClass;

    // Declaring buttons and text
    public Button login;
    public Button register;
    public EditText username;
    public EditText password;
    public String nameExtra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Casting buttons and editTexts
        login = (Button) findViewById(R.id.LoginButton);
        register = (Button) findViewById(R.id.RegisterButton);
        username = (EditText) findViewById(R.id.UsernameEditText);
        password = (EditText) findViewById(R.id.PasswordEditText);

        // After clicking the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //connect to database
                ConnectionClass con = new ConnectionClass();

                Statement stmt;

                //send edittext values to strings
                String usernameString = username.getText().toString();
                String passwordString = username.getText().toString();

                //initialize strings being pulled from database
                String SQLUsername = "";
                String SQLPassword = "";
                String SQLEmail = "";
                int PID;

                //check if users input values into both edittexts
                if (!usernameString.equals("") && !passwordString.equals("")) {

                    try {

                        //connect to database
                        stmt = con.CONN().createStatement();

                        //select info from player table
                        String query = "select * from Player where Username = '" + usernameString + "'";
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            //assign player table data to variables
                            PID = rs.getInt("PID");
                            SQLUsername = rs.getString("Username");
                            SQLPassword = rs.getString("Password");
                            SQLEmail = rs.getString("Email");
                        }
                        rs.close();

                        //check if entered password matches the database password for that username
                        if (SQLPassword.equals(passwordString)) {
                            //if so send to next page with username
                            Intent i = new Intent(getApplicationContext(), HostedGames.class);
                            i.putExtra("passedUserName", usernameString);
                            startActivity(i);

                        } else {

                            //if not tell the user that it doesn't match
                            Toast t = Toast.makeText(getApplicationContext(), "This username or password does not match our records.", Toast.LENGTH_LONG);
                            t.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast t = Toast.makeText(getApplicationContext(), "Please enter both a username and a password.", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
        // After clicking register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send user to register page
                Intent registerIntent = new Intent(getApplicationContext(), Register.class);
                startActivity(registerIntent);
            }
        });
    }
}