package com.andrei.messager.ui.singup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.MainActivity;
import com.andrei.messager.R;
import com.andrei.messager.helpers.HttpTask;
import com.andrei.messager.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private Button nextButton;
    private Button backButton;
    private ProgressBar progressBar;
    private EmailCheck emailCheck;
    private CreateAccount createAccount;
    private EditText emailTextEdit;
    private EditText passwordTextEdit;
    private EditText userNameTextEdit;
    private LinearLayout layoutStepTwo;
    private int step = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.progress_bar_next);
        emailTextEdit = findViewById(R.id.email_text_view);
        passwordTextEdit = findViewById(R.id.password_edit_text);
        layoutStepTwo = findViewById(R.id.linear_layout_step_two);
        userNameTextEdit = findViewById(R.id.username_edit_text);
    }

    public void onLogInClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onNextClick(View view) throws InterruptedException {
        if (emailCheck != null) {
            System.out.println("IF EMAIL CHECK NOT NULL");
            return;
        }

        if (step == 1) {
            stepOne();
        } else if (step == 2) {
            stepTwo();
        }
    }

    private void stepOne() {
        emailTextEdit.setError(null);

        String email = emailTextEdit.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            emailTextEdit.setError(getString(R.string.error_field_required));
            focusView = emailTextEdit;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailTextEdit;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            UIChangeNextFirstStart();
            emailCheck = new EmailCheck(email);
            emailCheck.execute();
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            UIChangeNextFirstStart();
            emailCheck = new EmailCheck(email);
            emailCheck.execute();
        }
    }

    private void stepTwo() {
        passwordTextEdit.setError(null);
        userNameTextEdit.setError(null);

        String password = passwordTextEdit.getText().toString();
        String username = userNameTextEdit.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordTextEdit.setError(getString(R.string.error_field_required));
            focusView = passwordTextEdit;
            cancel = true;
        } else if (password.length() < 6) {
            passwordTextEdit.setError(getString(R.string.error_invalid_password));
            focusView = passwordTextEdit;
            cancel = true;
        } else if (TextUtils.isEmpty(username)) {
            userNameTextEdit.setError(getString(R.string.error_field_required));
            focusView = userNameTextEdit;
            cancel = true;
        } else if (username.length() < 3) {
            userNameTextEdit.setError(getString(R.string.error_incorrect_username));
            focusView = userNameTextEdit;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String email = emailTextEdit.getText().toString();
            createAccount = new CreateAccount(email, password, username);
            createAccount.execute();
        }
    }

    private void UIChangeNextFirstStart() {
        nextButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void UIChangeNextFirstEnd() {
        nextButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void onBackClick(View view) {
        emailTextEdit.setEnabled(true);
        passwordTextEdit.getText().clear();
        layoutStepTwo.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        step = 1;
    }

    private void createToastServiceUnavailable() {
        Toast.makeText(SignUpActivity.this, "Service unavailable, please try again later", Toast.LENGTH_SHORT).show();
    }

    private class EmailCheck extends AsyncTask<Void, Void, String> {
        private final String mEmail;
        private OkHttpClient client;
        private String ERROR = "ERROR";

        EmailCheck(String email) {
            mEmail = email;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            System.out.println(BuildConfig.BASE_ENDPOINT);
            String url = BuildConfig.BASE_ENDPOINT + "account/isExist";
            String body = String.format("{\"email\" : \"%s\"}", mEmail);
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
        protected void onPostExecute(String s) {
            emailCheck = null;
            System.out.println("onPostExecute");
            System.out.println(s);
            UIChangeNextFirstEnd();
            if (s.equals(ERROR)) {
                createToastServiceUnavailable();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    String message = object.getString("message");
                    if (message.equals("ACCOUNT_NOT_FOUND")) {
                        layoutStepTwo.setVisibility(View.VISIBLE);
                        backButton.setVisibility(View.VISIBLE);
                        step = 2;
                        emailTextEdit.setEnabled(false);
                    } else if (message.equals("ACCOUNT_ALREADY_EXIST")) {
                        Toast.makeText(SignUpActivity.this, "Account with this email already exist", Toast.LENGTH_SHORT).show();
                    } else {
                        createToastServiceUnavailable();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CreateAccount extends AsyncTask<Void, Void, String> {

        private String mEmail;
        private String mPassword;
        private String mUsername;
        private OkHttpClient client;
        private String ERROR = "ERROR";

        CreateAccount(String email, String password, String username) {
            this.mEmail = email;
            this.mPassword = password;
            this.mUsername = username;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            System.out.println("CREATE ACCOUNT IN BACKGROUND");
            String url = BuildConfig.BASE_ENDPOINT + "account/create";
            String body = String.format("{\"email\" : \"%s\"," +
                    "\"password\":\"%s\"," +
                    "\"username\":\"%s\"}",
                    mEmail, mPassword, mUsername);
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
        protected void onPostExecute(String s) {
            System.out.println(s);
            if (s.equals(ERROR)) {
                createToastServiceUnavailable();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    boolean error = object.getBoolean("error");
                    if (!error) {
                        JSONObject message = object.getJSONObject("message");
                        String id = message.getString("id");
                        String email = message.getString("email");
                        String role = message.getString("role");
                        String username = message.getString("username");
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.ID, id);
                        intent.putExtra(MainActivity.ROLE, role);
                        intent.putExtra(MainActivity.EMAIL, email);
                        intent.putExtra(MainActivity.USERNAME, username);
                        startActivity(intent);
                        finish();
                    } else {
                        createToastServiceUnavailable();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    createToastServiceUnavailable();
                }
            }
        }
    }
}
