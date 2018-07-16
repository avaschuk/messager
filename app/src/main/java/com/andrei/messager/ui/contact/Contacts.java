package com.andrei.messager.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.andrei.messager.MainActivity;
import com.andrei.messager.R;
import com.andrei.messager.SplashScreen;
import com.andrei.messager.helpers.SetupAccountDatabase;

public class Contacts extends AppCompatActivity {

    public static final String ID = "ID";
    public static final String ROLE = "ROLE";
    public static final String EMAIL = "EMAIL";
    private String id;
    private TextView idTextView;
    private String role;
    private TextView adminTextView;
    private String email;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("CONTACTS");
        System.out.println(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        idTextView = findViewById(R.id.id_text_view);
        adminTextView = findViewById(R.id.admin_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString(ID);
            role = bundle.getString(ROLE);
            email = bundle.getString(EMAIL);
            if (id != null) idTextView.setText(id);
            if (email != null) emailTextView.setText(email);
            if (role != null) {
                if (role.equals("ADMIN")) {
                    adminTextView.setText("Hello, BOSS!");
                    adminTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void logOutButton(View view) {
        SetupAccountDatabase dbHelper = new SetupAccountDatabase(this);
        dbHelper.deleteAccountById(id);
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    public void onMainActivityClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        finish();
    }
}
