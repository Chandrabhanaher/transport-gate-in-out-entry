package com.example.ugc.ugc.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ugc.ugc.Home;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UGC";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String USER_NAME = "username";
    public static final String USER_CUST = "cust_name";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void checkLogin(){
        if(isLoggedIn()){
            Intent i = new Intent(_context, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(USER_NAME, pref.getString(USER_NAME, null));
        user.put(USER_CUST, pref.getString(USER_CUST, null));
        return user;
    }

    public void logoutUser(){
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.remove(USER_NAME);
        editor.remove(USER_CUST);
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void createLoginSession1(String user,String cust) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USER_NAME,user);
        editor.putString(USER_CUST,cust);
        editor.commit();
    }
}
