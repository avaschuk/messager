package com.andrei.messager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.andrei.messager.ui.MainActivity;

public class SplashScreen extends Activity {

    private boolean isLoggedIn = true;
    private static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("MESSAGER LOADING...");
                new PrepareData().execute();
            }
        }, SPLASH_TIME_OUT);
    }

    private class PrepareData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = null;
            if (isLoggedIn) {
                intent = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                intent = new Intent(SplashScreen.this, Contacts.class);
            }

            startActivity(intent);
            finish();
        }
    }

}
