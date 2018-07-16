package com.andrei.messager.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.MainActivity;
import com.andrei.messager.helpers.SetupAccountDatabase;
import com.andrei.messager.R;
import com.andrei.messager.helpers.HttpTask;
import com.andrei.messager.helpers.Utils;
import com.andrei.messager.ui.singup.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private OkHttpClient client;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = BuildConfig.BASE_ENDPOINT + "account/authorize";
            String body = String.format("{\"email\" : \"%s\", \"password\":\"%s\"}", mEmail, mPassword);
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(HttpTask.JSON, body))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return "ERROR";
            }
        }

        @Override
        protected void onPostExecute(final String jsonBody) {
            mAuthTask = null;
            showProgress(false);

            if (Utils.isJSONValid(jsonBody)) {
                System.out.println("JSON VALID");
                try {
                    JSONObject object = new JSONObject(jsonBody);
                    System.out.println(object);
                    String message = object.getString("message");
                    if (message.equals("AUTHORIZE_ALLOWED")) {
                        JSONObject accountInfo = object.getJSONObject("accountInfo");
                        String role = accountInfo.getString("role");
                        String id = accountInfo.getString("id");
                        String email = accountInfo.getString("email");
                        String username = accountInfo.getString("username");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(SetupAccountDatabase.ACC_ID, id);
                        contentValues.put(SetupAccountDatabase.EMAIL, email);
                        contentValues.put(SetupAccountDatabase.ROLE, role);
                        contentValues.put(SetupAccountDatabase.USERNAME, username);
                        SetupAccountDatabase sad = new SetupAccountDatabase(LoginActivity.this);
                        sad.insert(contentValues);
                        intent.putExtra(MainActivity.ROLE, role);
                        intent.putExtra(MainActivity.ID, id);
                        intent.putExtra(MainActivity.EMAIL, email);
                        intent.putExtra(MainActivity.USERNAME, username);
                        startActivity(intent);
                        finish();
                    } else if (message.equals("INCORRECT_PASSWORD")) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    } else if (message.equals("ACCOUNT_NOT_FOUND")) {
                        mEmailView.setError(getString(R.string.account_with_email_not_found));
                        mEmailView.requestFocus();
                    } else {
                        Toast.makeText(LoginActivity.this, "Service unavailable, please try again later", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Service unavailable, please try again later", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

