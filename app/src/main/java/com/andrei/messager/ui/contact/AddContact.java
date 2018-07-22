package com.andrei.messager.ui.contact;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.R;
import com.andrei.messager.helpers.HttpTask;
import com.andrei.messager.helpers.SetupAccountDatabase;
import com.andrei.messager.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddContact extends AppCompatActivity {

    private TextView emailTextView;
    private TextView usernameTextView;
    private Button addContactButton;
    private User userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        emailTextView = findViewById(R.id.email_text_view);
        usernameTextView = findViewById(R.id.username_text_view);
        addContactButton = findViewById(R.id.add_button);
        if (getIntent().getSerializableExtra("userObject") != null) {
            userObject = (User) getIntent().getSerializableExtra("userObject");
            emailTextView.setText(userObject.getEmail());
            usernameTextView.setText(userObject.getUsername());
            if (userObject.getHasRequest()) blockButtonAddContact();
            System.out.println(userObject);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void addContactOnClick(View view) {
        new GetAccountDetails().execute();
    }

    private class GetAccountDetails extends AsyncTask<Void, Void, HashMap<String, String>> {

        private SetupAccountDatabase dbHelper;

        @Override
        protected void onPreExecute() {
            addContactButton.setEnabled(false);
            dbHelper = new SetupAccountDatabase(AddContact.this);
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            return dbHelper.getAccountDetails();
        }

        @Override
        protected void onPostExecute(HashMap<String, String> map) {
            String id = map.get(SetupAccountDatabase.ACC_ID);
            new SendFriendRequest(id, userObject.getId()).execute();

        }
    }

    private class SendFriendRequest extends AsyncTask<Void, Void, String> {

        private OkHttpClient client;
        private String fromId;
        private String toId;
        private String ERROR = "ERROR";

        public SendFriendRequest(String fromId, String toId) {
            this.fromId = fromId;
            this.toId = toId;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = BuildConfig.BASE_ENDPOINT + "account/request/add";
            String body = String.format("{\"fromId\" : \"%s\", \"toId\":\"%s\"}", fromId, toId);
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(HttpTask.JSON, body))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return ERROR;
            }
        }

        @Override
        protected void onPostExecute(String jsonBody) {
            if (jsonBody.equals(ERROR)) {
                Toast.makeText(AddContact.this, "Service unavailable, please try again later", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject object = new JSONObject(jsonBody);
                    boolean error = object.getBoolean("error");
                    if (error) {
                        Toast.makeText(AddContact.this, "Service unavailable, please try again later", Toast.LENGTH_SHORT).show();
                    } else {
                        blockButtonAddContact();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void blockButtonAddContact() {
        addContactButton.setEnabled(false);
        addContactButton.setText("RequestEntity created");
    }
}
