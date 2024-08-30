package com.example.myapplication;

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
    private final String ip =       "192.168.0.249"    //IP address of local computer
                       , port =     "1066"             //Port the sql server resides on
                       , db =       "BlueBookDB"       //Database name
                       , username = "user1"            //Keep the same unless changed in users.sql
                       , password = "";                //Keep the same unless changed in users.sql

    @SuppressLint("NewApi")
    //Establishes connection between Android API and sql server
    public SQLConnection() {
        connect(username, password);
    }
    //Establishes connection  using variable username and password
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
    //Disconnects from sql server
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
    //Writes on the sql server using a TSQL statement (SELECT, UPDATE, INSERT, DELETE)
    public boolean update(String SQLStatement) {
        try {
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate(SQLStatement);
            Log.d("Message", "rows effected: " + count);
            return true;
        } catch(SQLException E) {
            Log.d("SQLMessage", "Could not process query.");
            Log.e("SQLError", E.getMessage());
            return false;
        }
    }
    //Returns A hashmap of the results from a TSQL SELECT statement
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
    //Gets Row from Hashmap
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
    //Gets the maximum ID from table only if ID variable is called "ID"
    public int getMaxID(String table)
    {
        HashMap<String, String[]> result = select("SELECT MIN(ID) + 1 as maxID FROM " + table + " WHERE ID + 1 NOT IN (SELECT ID FROM " + table + ");");
        String maxID = result.get("maxID")[0];
        if(maxID == null) return 0;
        return Integer.parseInt(maxID);
    }
}


/*example inside an onCreate function

SQLConnection conn = new SQLConnection("user1", "");
HashMap<String, String[]> result = c.select("SELECT * FROM GuardianLanguage WHERE guardianID > 10");
for(int i = 0; i < result.get("guardianID").length; i++)
{
    Log.d("msg", result.get("language")[i]);
}

c.disconnect();
*/
