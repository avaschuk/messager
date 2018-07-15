package com.andrei.messager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.andrei.messager.helpers.SetupAccountDatabase;
import com.andrei.messager.ui.contacts.Contacts;
import com.andrei.messager.ui.singup.MainActivity;

import java.util.HashMap;

public class SplashScreen extends Activity {

    private boolean isLoggedIn = true;

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
            System.out.println("map !!!!!!!!!!!!");
            System.out.println(map);
            String id = map.get(SetupAccountDatabase.ACC_ID);
            if (id.equals("id")) {
                intent = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                String email = map.get(SetupAccountDatabase.EMAIL);
                String role = map.get(SetupAccountDatabase.ROLE);
                intent = new Intent(SplashScreen.this, Contacts.class);
                intent.putExtra(Contacts.ID, id);
                intent.putExtra(Contacts.ROLE, role);
                intent.putExtra(Contacts.EMAIL, email);
            }

            startActivity(intent);
            finish();
        }
    }

}
