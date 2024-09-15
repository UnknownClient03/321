package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.lang.Thread;

public class SQLConnection extends Thread {

    /*for prepared statement use:
          i - signify an integer
          s - signify a String
          n - signify a null type
     */
    private Connection conn;
    private boolean isConnected;
    private final String ip =       "192.168.86.22"    //IP address of local computer
                       , port =     "1433"                //Port the sql server resides on
                       , db =       "BlueBookDB"       //Database name
                       , username = "user1"            //Keep the same unless changed in users.sql
                       , password = "strongPwd123";    //Keep the same unless changed in users.sql
    private int ConnecitonTimeout = 2000;              //Connection Timeout in milliseconds. Would advise the ConnecitonTimeout to be > 3 for local networks and > 10 for public networks

    @SuppressLint("NewApi")
    //Establishes connection between Android API and sql server
    public SQLConnection() {
        connect(username, password);
    }
    //Constructor, only for use of threads
    private SQLConnection(boolean bool) {}
    //Establishes connection  using variable username and password
    public SQLConnection(String username, String password) {
        connect(username, password);
    }
    //connects too the database
    private void connect(String username, String password) {
        StrictMode.ThreadPolicy a=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        try {
            SQLConnection thread = new SQLConnection(false);
            thread.start();
            /*Thread.sleep(ConnecitonTimeout);
            isConnected = thread.isConnected;
            conn = thread.conn;
            if(!isConnected || thread.isAlive())
                throw new SQLException("Connection timed out ");*/
            for(int time = 0; thread.isAlive() && time < ConnecitonTimeout; time += 10)
                Thread.sleep(10);
            isConnected = thread.isConnected;
            conn = thread.conn;
            if(!isConnected)
                throw new SQLException("Connection timed out ");
        }
        catch (Exception e) {
            Log.d("SQLMessage", "Cannot connect to database.");
            Log.d("SQLError", e.getMessage());
        }
    }
    //thread
    public void run() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String ConnectURL = "jdbc:jtds:sqlserver://"+ip+":"+port+"/" + db;
            Log.d("SQLMessage", "Attempting to establish connection to database.");
            Properties properties = new Properties();
            properties.put("user", username);
            properties.put("password", password);
            conn = DriverManager.getConnection(ConnectURL, properties);
            Log.d("SQLMessage", "Connected to database.");
            isConnected = true;
        } catch (Exception E) {
            Log.d("SQLMessage", "Cannot connect to database.");
            Log.e("SQLError", E.getMessage());
            isConnected = false;
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

    //Checks if a connection is established
    public boolean isConn()
    {
        return isConnected;
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
    public boolean update(String SQLStatement, String[] params, char[] paramTypes) {
        try {
            PreparedStatement stmt = preparedSTMT(SQLStatement, params, paramTypes);
            int count = stmt.executeUpdate();
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
    public HashMap<String, String[]> select(String SQLStatement, String[] params, char[] paramTypes) {
        HashMap<String, String[]> hash= new HashMap<String, String[]>();
        try {
            PreparedStatement stmt = preparedSTMT(SQLStatement, params, paramTypes);
            ResultSet set = stmt.executeQuery();
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
    public int getMaxID(String table) {
        HashMap<String, String[]> result = select("SELECT MIN(ID) + 1 as maxID FROM " + table + " WHERE ID + 1 NOT IN (SELECT ID FROM " + table + ");");
        if(result.get("maxID")[0] == null) return 0;
        String maxID = result.get("maxID")[0];
        return Integer.parseInt(maxID);
    }

    private PreparedStatement preparedSTMT(String SQLStatement, String[] params, char[] paramTypes)
            throws SQLException {
        if(params.length != paramTypes.length) throw new android.database.SQLException("The params and the types of params entered for the prepared statement is not equal. " + String.valueOf(params.length) + " : " + String.valueOf(paramTypes.length));
        char[] charArr = SQLStatement.toCharArray();
        int charCount = 0;
        for(int i = 0; i < charArr.length; i++) if(charArr[i] == '?') charCount++;
        if(charCount != params.length) throw new android.database.SQLException("The params and the amount of '?' entered for the prepared statement is not equal. " + String.valueOf(params.length) + " : " + String.valueOf(charCount));

        PreparedStatement stmt = conn.prepareStatement(SQLStatement);
        for(int i = 0; i < params.length; i++)
            switch(paramTypes[i])
            {
                case 's': stmt.setString(i+1, params[i]); break;
                case 'i': stmt.setInt(i+1, Integer.parseInt(params[i])); break;
                case 'n': stmt.setNull(i+1, java.sql.Types.NULL); break;
                case 'b': stmt.setBoolean(i + 1, Boolean.parseBoolean(params[i])); break;
                default: throw new android.database.SQLException("param in ParamTypes is unknown: " + paramTypes[i]);
            }
        return stmt;
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
