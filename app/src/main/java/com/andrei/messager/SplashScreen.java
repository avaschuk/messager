package com.andrei.messager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.andrei.messager.helpers.SetupAccountDatabase;
import com.andrei.messager.ui.singup.SignUpActivity;

import java.util.HashMap;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new SetupAccountDatabase(this);
        new PrepareData().execute();
    }

    private class PrepareData extends AsyncTask<Void, Void, HashMap<String, String>> {

        private SetupAccountDatabase dbHelper;

        @Override
        protected void onPreExecute() {
            dbHelper = new SetupAccountDatabase(SplashScreen.this);
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            return dbHelper.getAccountDetails();
        }

        @Override
        protected void onPostExecute(HashMap<String, String> map) {
            Intent intent = null;
            String id = map.get(SetupAccountDatabase.ACC_ID);
            System.out.println("id in splashscreen" + id);
            if (id.equals("id")) {
                intent = new Intent(SplashScreen.this, SignUpActivity.class);
            } else {
                String email = map.get(SetupAccountDatabase.EMAIL);
                String role = map.get(SetupAccountDatabase.ROLE);
                String username = map.get(SetupAccountDatabase.USERNAME);
                intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra(MainActivity.ID, id);
                intent.putExtra(MainActivity.ROLE, role);
                intent.putExtra(MainActivity.EMAIL, email);
                intent.putExtra(MainActivity.USERNAME, username);
            }

            startActivity(intent);
            finish();
        }
    }

}
