package com.example.ugc.ugc.session;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ugc.ugc.Config;
import com.example.ugc.ugc.Home;
import com.example.ugc.ugc.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail,editTextPass;
    private Button btnLogin;
    private boolean loggedIn = false;
    SessionManager session;
    private RequestQueue rq;
    private MaterialBetterSpinner custSpinner;
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rq = Volley.newRequestQueue(this);

        session = new SessionManager(getApplicationContext());
        session.isLoggedIn();

        editTextEmail = (EditText)findViewById(R.id.txtUser);
        editTextPass = (EditText)findViewById(R.id.password);
        custSpinner = (MaterialBetterSpinner)findViewById(R.id.cust_spinner);


        listItems=new ArrayList<>();
        adapter = new ArrayAdapter<>(Login.this,R.layout.cust_item,R.id.txtName,listItems);
        custSpinner.setAdapter(adapter);


        btnLogin = (Button)findViewById(R.id.btnlogin);
        btnLogin.setOnClickListener(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        if(loggedIn){
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("TAG","Login");

        if(validate()){
            logins();
        }
    }

    private void logins() {
        final String user = editTextEmail.getText().toString().trim();
        final String pass = editTextPass.getText().toString().trim();
        final String cust = custSpinner.getText().toString().trim();
        final String url = Config.USER_LOGINS;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {
                        String ss = "";
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                ss = object.getString("message");
                            }
                            String ss1 = "Successfully";

                            if (ss1.equals(ss)) {
                                session.createLoginSession1(user, cust);
                                SharedPreferences sharedPreferences = Login.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                                editor.putString(Config.EMAIL_SHARED_PREF, user);
                                editor.putString(Config.CUST_SHARED_PREF,cust);
                                editor.commit();
                                editTextEmail.setText("");
                                editTextPass.setText("");
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Enter Currect Username", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Enter Currect Username", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Error: " + error.getMessage());
                    NetworkResponse networkResponse = error.networkResponse;
                    Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                    if (networkResponse != null) {
                        Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", user);
                    params.put("password", pass);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
    }

    private boolean validate() {
        boolean valid = true;
        final String user = editTextEmail.getText().toString().trim();
        final String pass = editTextPass.getText().toString().trim();
        final String cust = custSpinner.getText().toString().trim();

        if(user.isEmpty()){
            editTextEmail.setError("enter a valid username");
            editTextEmail.requestFocus();
            valid = false;
        }else {
            editTextEmail.setError(null);
        }
        if(pass.isEmpty()){
            editTextPass.setError("enter a valid password");
            valid = false;
        }else {
            editTextPass.setError(null);
        }if(cust.isEmpty()){
            custSpinner.setError("select customer name");
            valid = false;
        }else {
            custSpinner.setError(null);
        }
        return valid;
    }
//    Customer List

    public void onStart(){
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }
    private class BackTask extends AsyncTask<Void, Void,Void> {
        ArrayList<String> list;

        protected void onPreExecute(){
            super.onPreExecute();
            list=new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            InputStream is=null;
            String result="";

            try{
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost= new HttpPost(Config.CUST_LIST);
                HttpResponse response=httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // Get our response as a String.
                is = entity.getContent();
            }catch(IOException e){
                e.printStackTrace();
            }

            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result+=line;
                }
                is.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                JSONArray jArray =new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    JSONObject jsonObject=jArray.getJSONObject(i);
                    String CUSTNAME = jsonObject.getString("CUSTNAME");
                    list.add(CUSTNAME);
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            listItems.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        moveTaskToBack(true);
                        System.exit(0);
                        Login.super.onBackPressed();
                    }
                }).setNegativeButton("no", null).show();
    }
}
