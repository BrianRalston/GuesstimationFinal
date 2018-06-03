//Written by Garrett Dawson
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//00B30C
public class Register extends AppCompatActivity {

    public Connection con;
    ConnectionClass connectionClass;

    //declaring variables//
    public String username;
    public String password;
    public String passconfirm;
    public String useremail;
    public Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ok = (Button) findViewById(R.id.SignUpButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLogin cl = new CheckLogin();
                EditText signUpBox = (EditText) findViewById(R.id.SignUpBox);
                username = signUpBox.getText().toString();
                EditText pwordText = (EditText) findViewById(R.id.SignUpPass);
                password = pwordText.getText().toString();
                EditText confirmText = (EditText) findViewById(R.id.SignUpPassConfirm);
                passconfirm = confirmText.getText().toString();
                EditText emailText = (EditText) findViewById(R.id.SignUpEmail);
                useremail = emailText.getText().toString();
                cl.username = username;
                cl.password = password;
                cl.useremail = useremail;
                cl.execute("");
            }
        });

    }

    public class CheckLogin extends AsyncTask<String, String, String> {
        String status = "";
        String username = "";
        String password = "";
        String useremail = "";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String s) {
            if (isSuccess) {
                Intent intent = new Intent(getApplicationContext(), UserLogin.class);
                startActivity(intent);
            } else {
                Toast t = Toast.makeText(getApplicationContext(), "An error occurred while creating your account.", Toast.LENGTH_LONG);
                t.show();
            }
        }

        //insert query for all user data
        @Override
        protected String doInBackground(String... params) {
            try {
                ConnectionClass connectionClass = new ConnectionClass();

                //select info from player table to make sure username is not taken
                String takenUsername = "";
                Statement stmtCheck = connectionClass.CONN().createStatement();;
                String checkQuery = "select Username from Player where Username = '" + username + "'";
                ResultSet rs = stmtCheck.executeQuery(checkQuery);
                while (rs.next()) {
                    //assign player table data to variables
                    takenUsername = rs.getString("Username");
                }
                rs.close();

                if (connectionClass == null) {
                    status = "Check your internet connection";
                } else {
                    if (username.equals("") || password.equals("") || useremail.equals("")) {
                        Toast t = Toast.makeText(getApplicationContext(), "Please make sure you have entered a value into each text box.", Toast.LENGTH_LONG);
                        t.show();
                        TextView errorUpdate = (TextView) findViewById(R.id.errorView);
                        errorUpdate.setText("An error occurred while creating your account.");
                        isSuccess = false;
                    }
                    else if (!username.equals(takenUsername)) {
                        Toast t = Toast.makeText(getApplicationContext(), "This username is already taken, please try again.", Toast.LENGTH_LONG);
                        t.show();
                        isSuccess = false;
                    } else {
                        String query = "insert into Player (Username, Password, Email) values ('" + username + "', '" + password + "', '" + useremail + "')";
                        Statement stmtInsert = connectionClass.CONN().createStatement();
                        stmtInsert.execute(query);
                        status = ("Account created successfully");
                        Toast t = Toast.makeText(getApplicationContext(), "Account creation successful, please login!", Toast.LENGTH_LONG);
                        t.show();
                        isSuccess = true;
                    }
                }
            } catch (Exception e) {
                isSuccess = false;
                status = e.getMessage();

                Log.d("sql error", status);
            }
            return status;
        }
    }


    public void loginButtonClick(View v) {
        //intent to take user back to home page//
        Intent intent = new Intent(getApplicationContext(), UserLogin.class);
        startActivity(intent);
    }
}
