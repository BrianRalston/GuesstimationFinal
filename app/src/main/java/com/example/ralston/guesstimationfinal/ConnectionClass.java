//Written by Andrew Starkey
package com.example.ralston.guesstimationfinal;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.*;

public class ConnectionClass {
    String ip = "1130grp8.database.windows.net";
    String db = "1130grp8";
    String un = "grp8";
    String pass = "k8Nry6c4";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://1130grp8.database.windows.net:1433;DatabaseName=1130grp8;user=grp8@1130grp8;password=k8Nry6c4;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connection = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}