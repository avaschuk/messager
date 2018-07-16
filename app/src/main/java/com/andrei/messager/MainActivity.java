package com.andrei.messager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.andrei.messager.ui.contact.SearchContactFragment;
import com.andrei.messager.ui.message.MessageFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ID = "ID";
    public static final String ROLE = "ROLE";
    public static final String EMAIL = "EMAIL";

    private TextView navUsernameTextView;
    private TextView navEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        navUsernameTextView = header.findViewById(R.id.nav_header_user_name);
        navEmailTextView = header.findViewById(R.id.nav_header_email);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String email = bundle.getString(EMAIL);
            String role = bundle.getString(ROLE);
            if (email != null) navEmailTextView.setText(email);
            navUsernameTextView.setText("USERNAME");
//            if (role != null) {
//                if (role.equals("ADMIN")) {
//                    adminTextView.setText("Hello, BOSS!");
//                    adminTextView.setVisibility(View.VISIBLE);
//                }
//            }
        }


        Fragment fragment = new MessageFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("onNavigationItemSelected");
        int id = item.getItemId();
        System.out.println("id ---- " + id);

        Fragment fragment = null;

        switch (id) {
            case R.id.nav_messages:
                fragment = new MessageFragment();
                break;
            case R.id.nav_find_contacts:
                fragment = new SearchContactFragment();
                break;
            default:
                fragment = new MessageFragment();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
