package com.andrei.messager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andrei.messager.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity implements Validator.ValidationListener, IMainActivityView {

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 11223;
    private Validator validator;
    private Context appContext;
    private HttpCheckEmail httpCheckEmail;
    private ProgressBar progressBar;
    private Button nextButton;
    private Button backButton;
    private Button confirmButton;
    private String enteredEmail;
    private String enteredPassword;
    private Boolean isEmailChecked = false;

    @NotEmpty
    @Email
    private EditText emailAddress;

    @Password(min = 6, message = "Minimum length is 6")
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validator = new Validator(this);
        validator.setValidationListener(this);
        appContext = getApplicationContext();
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        emailAddress = findViewById(R.id.email_address);
        passwordEditText = findViewById(R.id.password_edit_text);
        progressBar = findViewById(R.id.progress_bar);
        confirmButton = findViewById(R.id.confirm_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

//        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
           e.printStackTrace();
           System.out.println(e.getStatusCode());
        }
    }

    public void onLogInClick(View view) {
//        googleSignIn();
    }

    @Override
    public void onValidationSucceeded() {
        //layout_constraintTop_toBottomOf
        if (!isEmailChecked) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            nextButton.setVisibility(Button.INVISIBLE);
            httpCheckEmail = new HttpCheckEmail(appContext, this);
            httpCheckEmail.checkEmail(emailAddress.getText().toString());
        } else {
            Toast.makeText(this, "Going to create account!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String errorMessage = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(errorMessage);
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void returnUi() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        nextButton.setVisibility(Button.VISIBLE);
        passwordEditText.setVisibility(EditText.INVISIBLE);
        backButton.setVisibility(Button.INVISIBLE);
        confirmButton.setVisibility(Button.INVISIBLE);
        emailAddress.setEnabled(true);
        isEmailChecked = false;
    }

    @Override
    public void nextStep(String data) {
        //returnUi();
        try {
            JSONObject jsonObject = new JSONObject(data);
            String message =  jsonObject.getString("message");
            if (message.equals("ACCOUNT_NOT_FOUND")) {
                isEmailChecked = true;
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                passwordEditText.setVisibility(EditText.VISIBLE);
                emailAddress.setEnabled(false);
                backButton.setVisibility(Button.VISIBLE);
                confirmButton.setVisibility(Button.VISIBLE);
                Toast.makeText(this, "Account not found. You can register here", Toast.LENGTH_LONG).show();
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } else if (message.equals("ACCOUNT_ALREADY_EXIST")) {
                returnUi();
                Toast.makeText(this, "Email already used. Try login", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Something wrong. Please try again later", Toast.LENGTH_LONG).show();
                returnUi();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonClick(View view) {
        returnUi();
    }
}
