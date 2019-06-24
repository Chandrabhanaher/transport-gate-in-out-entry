package com.example.ugc.ugc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ugc.ugc.session.Login;


public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    ProgressBar progressBar;
    TextView textView,textView1;
    int i = 0;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        textView = (TextView)findViewById(R.id.textView);
        textView1 = (TextView)findViewById(R.id.textView1);
        progressBar.setVisibility(View.VISIBLE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);
                ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){

                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                }

            }
        },SPLASH_TIME_OUT);
    }


}
