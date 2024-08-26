package com.example.myapplication;

public class LoginManager {
    public int guardianID;
    public int childID;

    LoginManager(int guardianID, int childID)
    {
        this.guardianID = guardianID;
        this.childID = childID;
    }

}

/*

vvv   For Jackson   vvv


--example to gather guardian ID and child ID--

Bundle extras = getIntent().getExtras();
if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));

--example to send guardian ID and child ID--

Bundle extras = getIntent().getExtras();
if (extras != null) {
    intent.putExtra("guardianID", extras.getInt("guardianID"));
    intent.putExtra("childID", extras.getInt("childID"));
}
 */
