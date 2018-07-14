package com.andrei.messager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;


public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 11223;
    private Validator validator;
    private Button signUpButton;
    private Context appContext;
    private HttpLogin httpLogin;

    @NotEmpty
    @Email
    private EditText emailAddress;

//    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, message = "Minimum length is 6")
//    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validator = new Validator(this);
        validator.setValidationListener(this);
        signUpButton = findViewById(R.id.sign_up_button);
        emailAddress = findViewById(R.id.email_address);
//        password = findViewById(R.id.password);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

//        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
//        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        System.out.println("onNextClick");
        System.out.println(emailAddress.getText());
        appContext = getApplicationContext();
        httpLogin = new HttpLogin(appContext);
        httpLogin.checkEmail(emailAddress.getText().toString());
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
}
