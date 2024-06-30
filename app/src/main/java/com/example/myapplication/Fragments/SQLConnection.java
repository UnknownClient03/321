package com.example.myapplication.Fragments;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Arrays;
import java.util.HashMap;

public class SQLConnection {

    private Connection conn;
    private final String ip =       "192.168.0.249"
                       , port =     "1066"
                       , db =       "BlueBookDB"
                       , username = "user1"
                       , password = "";

    @SuppressLint("NewApi")
    public SQLConnection() {
        connect(username, password);
    }
    public SQLConnection(String username, String password) {
        connect(username, password);
    }
    public void connect(String username, String password) {
        StrictMode.ThreadPolicy a=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String ConnectURL = "jdbc:jtds:sqlserver://"+ip+":"+port+"/" + db + ";"
                    +"; user="+username
                    +"; password="+password+";";

            conn = DriverManager.getConnection(ConnectURL, username, password);
            Log.d("SQLMessage", "Connected to database.");
        }
        catch (Exception E){
            Log.d("SQLMessage", "Cannot connect to database.");
            Log.e("SQLError", E.getMessage());
        }
    }
    public void disconnect() {
        try {
            conn.close();
            Log.d("SQLMessage", "Disconnected from database.");
        } catch(SQLException E)
        {
            Log.d("SQLMessage", "Cannot disconnected from database.");
            Log.e("SQLError", E.getMessage());
        }

    }
    public void update(String SQLStatement) {
        try {
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate(SQLStatement);
            Log.d("Message", "rows effected: " + count);
        } catch(SQLException E) {
            Log.d("SQLMessage", "Could not process query.");
            Log.e("SQLError", E.getMessage());
        }
    }
    public HashMap<String, String[]> select(String SQLStatement) {
        HashMap<String, String[]> hash= new HashMap<String, String[]>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(SQLStatement);
            ResultSetMetaData rsmd = set.getMetaData();
            int colNum = rsmd.getColumnCount();

            String[][] arr = new String[colNum+1][100];
            int rowCount = 0;
            for(rowCount = 0; set.next() && rowCount < 100; rowCount++)
                for(int i = 0; i < colNum; i++)
                    arr[i][rowCount] = set.getString(i+1);

            for(int i = 0; i < colNum; i++)
                hash.put(rsmd.getColumnName(i+1), Arrays.copyOf(arr[i], rowCount));
            Log.d("SQLMessage", rowCount + " Rows gathered");
        } catch(SQLException E) {
            Log.d("SQLMessage", "Could not process query.");
            Log.e("SQLError", E.getMessage());
        }
        return hash;
    }
    public String[] getRow(HashMap<String, String[]> hash, int rowNum) {
        int rowCount = hash.keySet().size();
        String[] row = new String[rowCount];
        int rowIndex = 0;
        if(hash.get(hash.keySet().toArray()[0]).length > rowNum)
            for (String key : hash.keySet()) {
                row[rowIndex] = hash.get(key)[rowNum];
                rowIndex++;
            }
        else
            throw new ArrayIndexOutOfBoundsException("Failed - rowNum needs to be less then the hashMap length.");

        return row;
    }

    public int getMaxID(String table)
    {
        HashMap<String, String[]> result = select("SELECT MIN(ID) + 1 as maxID FROM " + table + " WHERE ID + 1 NOT IN (SELECT ID FROM " + table + ");");
        String maxID = result.get("maxID")[0];
        return Integer.parseInt(maxID);
    }
}
