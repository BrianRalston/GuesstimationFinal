//Written by Brian Ralston
package com.example.ralston.guesstimationfinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class HostedGames extends AppCompatActivity {

    //initialize arraylist to hold games available
    private ArrayList<String> arrayListToDo;
    //initialize adapter to make clickable
    private ArrayAdapter<String> arrayAdapterToDo;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosted_games);

        //get the userID and username passed from the login page intent
        final String passedUserName = getIntent().getExtras().getString("passedUserName");

        //create arraylist and adapter
        arrayListToDo = new ArrayList<String>();
        arrayAdapterToDo = new ArrayAdapter<String>(this, R.layout.row, R.id.row, arrayListToDo);
        //cast listview to adapter
        ListView listView = (ListView) findViewById(R.id.currentGamesLv);
        listView.setAdapter(arrayAdapterToDo);
        ShowGames showGames = new ShowGames();
        //execute showgames found below
        showGames.execute("");

        //make listview clickable
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the selected listView item
                //pull line from selected line
                String selectedItem = (String) parent.getItemAtPosition(position);
                //set delimiter as ')' so that it assigns the number before it to a string
                String stringGID = selectedItem.substring(0,selectedItem.indexOf(")"));
                //convert that string to a usable int
                int GID = Integer.parseInt(stringGID);

                //connect to database
                ConnectionClass con = new ConnectionClass();

                String status;

                //pull value of PID2 and if it is empty join the game
                //this is because if PID2 is empty that means there is at least one empty slot
                try {
                    //select query
                    String checkQuery = "select PID2 from Roster where GID = " + Integer.toString(GID);
                    Statement stmt = con.CONN().createStatement();
                    ResultSet rs = stmt.executeQuery(checkQuery);
                    //initialize PID2 variable
                    int PID2 = 0;
                    try{
                        //send value of PID2 to variable
                        PID2 = rs.getInt("PID2");
                    } catch (Exception e) {
                        status = e.getMessage();
                        Log.d("sql error", status);
                    }

                    //tell user what GID they have chosen
                    Toast t = Toast.makeText(getApplicationContext(), "You have chosen " + Integer.toString(GID) + ", joining game!", Toast.LENGTH_LONG);
                    t.show();

                    //now do some adding to the database and send them to the next page that is not functioning so there will be a crash
                    try {
                        if (con == null) {
                            status = "Check your internet connection";
                        } else {

                            //get users PID from username being passed between pages
                            Statement stmt1 = con.CONN().createStatement();
                            String selectQuery = "select PID from Player where UserName = '" + passedUserName + "'";
                            ResultSet rs1 = stmt1.executeQuery(selectQuery);
                            int PID = 0;
                            while (rs1.next()) {
                                PID = rs1.getInt("PID");
                            }
                            rs1.close();

                            //check if PID1 in database has value, if it does, insert name into PID2 otherwise insert into PID1
                            int PID1 = 0;

                            Statement stmt2 = con.CONN().createStatement();
                            String checkQuery1 = "select PID1 from Roster where GID = " + Integer.toString(GID);
                            ResultSet rs2 = stmt2.executeQuery(checkQuery1);

                            while (rs2.next()) {

                                PID1 = rs2.getInt("PID1");

                                //insert name into PID1 if it does not have a value, else insert it into PID2
                                if (PID1 == 0) {

                                    String PID1Query = "insert into Roster (PID1) values (" + Integer.toString(PID) + ")";
                                    Statement stmt3 = con.CONN().createStatement();
                                    stmt3.execute(PID1Query);

                                    //intent to send them to the functionality of the game
                                    Intent i = new Intent(getApplicationContext(), GameQuestion.class);
                                    //send extra variables to next page
                                    i.putExtra("passedUserName", passedUserName);
                                    i.putExtra("GID", GID);
                                    startActivity(i);

                                    ///////////////////////////////////////////////
                                    //Important to note that the project works   //
                                    //up to this point, author of following pages//
                                    //were unable to get them working            //
                                    ///////////////////////////////////////////////

                                } else {
                                    //else insert into PID2
                                    String PID2Query = "insert into Roster PID2 values (" + Integer.toString(PID) + ")";
                                    Statement stmt4 = con.CONN().createStatement();
                                    stmt4.execute(PID2Query);

                                    //send user to next page with extra variables
                                    Intent i = new Intent(getApplicationContext(), GameQuestion.class);
                                    i.putExtra("passedUserName", passedUserName);
                                    i.putExtra("GID", GID);
                                    startActivity(i);
                                }
                            }
                            rs2.close();
                        }
                    } catch (Exception e) {
                        status = e.getMessage();
                        Log.d("sql error", status);
                    }
                    rs.close();
                } catch (Exception e) {
                    status = e.getMessage();
                    Log.d("sql error", status);
                }
            }
        });
    }



    public void CreateGameButtonClick(View v) {
        String passedUserName = getIntent().getExtras().getString("passedUserName");
        Intent i = new Intent (getApplicationContext(), GameCreate.class);
        i.putExtra("passedUserName", passedUserName);
        startActivity(i);
    }

    //class utilized above to show games on arraylist
    public class ShowGames extends AsyncTask<String,String,ArrayList<String>> {
        String status = "";

        @Override
        protected void onPostExecute(ArrayList<String> r) {
            Iterator<String> iterator = r.iterator();
            while (iterator.hasNext()) {
                //sent iterator to string as long as there is another value
                arrayAdapterToDo.add(iterator.next().toString());
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> games = null;
            try {
                ConnectionClass con = new ConnectionClass();
                if (con == null) {
                    status = "Check Your Internet Access!";
                } else {
                    //check if password matches the username given if so pass the username on
                    String query = "SELECT GameName FROM Game";
                    //connect to database
                    Statement stmt = con.CONN().createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    games = new ArrayList<String>();
                    //add formating first line to the arraylist
                    games.add("ID   Game Name");
                    //assign GID to a and GameName to b

                    int i = 1;
                    while (rs.next()) {
                        String a = String.valueOf(i);
                        String b = rs.getString(1);


                        //add to arraylist with formatting
                        games.add(a + ")  " + b);
                        i++;
                    }

                }

            } catch (SQLException e) {
                Log.d("sql error", e.getMessage());
            }
            //return arraylist
            return games;
        }
    }
}
