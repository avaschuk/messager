package com.andrei.messager;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrei.messager.helpers.SetupAccountDatabase;
import com.andrei.messager.model.User;
import com.andrei.messager.ui.contact.AddContact;
import com.andrei.messager.ui.message.MessageFragment;
import com.andrei.messager.ui.requests.RequestsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ID = "ID";
    public static final String ROLE = "ROLE";
    public static final String EMAIL = "EMAIL";
    public static final String USERNAME = "USERNAME";

    private TextView navUsernameTextView;
    private TextView navEmailTextView;
    private String ACC_ID;
    private NavigationView navigationView;
    private FrameLayout frameLayoutMain;
    private LinearLayout linearLayoutSearch;
    private SearchView searchView;

    private SimpleCursorAdapter mAdapter;
    private JSONArray usersArray = new JSONArray();

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
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        navUsernameTextView = header.findViewById(R.id.nav_header_user_name);
        navEmailTextView = header.findViewById(R.id.nav_header_email);
        frameLayoutMain = findViewById(R.id.content_frame);
        linearLayoutSearch = findViewById(R.id.linear_search);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString(ID);
            ACC_ID = id;

            String email = bundle.getString(EMAIL);
            String role = bundle.getString(ROLE);
            String username = bundle.getString(USERNAME);
            if (email != null) navEmailTextView.setText(email);
            if (username != null) navUsernameTextView.setText(username);
        }


        Fragment fragment = new MessageFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String title = "";

        switch (id) {
            case R.id.nav_log_out:
                logOut();
                return true;
            case R.id.nav_requests:
                fragment = new RequestsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ID, ACC_ID);
                fragment.setArguments(bundle);
                title = getResources().getString(R.string.label_requests);
                break;
            default:
                fragment = new MessageFragment();
                title = getResources().getString(R.string.label_messages);
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }

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

    public void logOut() {
        SetupAccountDatabase dbHelper = new SetupAccountDatabase(this);
        dbHelper.deleteAccountById(ACC_ID);
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final String[] from = new String[] {"username", "email"};
        final int[] to = new int[] {android.R.id.text1, android.R.id.text2};
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView tv = (TextView)view;
                System.out.println("columnIndex");
                System.out.println(columnIndex);
                String s = cursor.getString(columnIndex);
//                tv.setBackgroundColor(Color.WHITE);
                tv.setText(s);
                tv.setTextColor(Color.WHITE);
                return true;
            }
        };
        mAdapter.setViewBinder(binder);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                switchMainWithSearch(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                switchMainWithSearch(true);
                return true;
            }
        });
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                try {
                    JSONObject userObject = usersArray.getJSONObject(position);
                    Intent intent = new Intent(MainActivity.this, AddContact.class);
                    User user = new User();
                    user.setId(userObject.getString("id"));
                    user.setEmail(userObject.getString("email"));
                    user.setUsername(userObject.getString("username"));
                    intent.putExtra("userObject", user);
                    if (userObject.has("request") && !userObject.isNull("request")) user.setHasRequest(true);
                    startActivity(intent);
                    searchItem.collapseActionView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("on suggestion click");
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) new FindUser(newText, ACC_ID).execute();
                return false;
            }
        });
        return true;
    }

    private void populateAdapter(JSONArray users) {
        System.out.println("populate adapter");
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "username", "email" });
        for (int i = 0; i < users.length(); i++) {
            try {
                JSONObject object = users.getJSONObject(i);
                String id = object.getString("id");
                String username = object.getString("username");
                String email = object.getString("email");
                c.addRow(new Object[] {id, username, email});
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.changeCursor(c);
    }

    private void switchMainWithSearch(boolean showMain) {
        frameLayoutMain.setVisibility(showMain ? View.VISIBLE : View.GONE);
        linearLayoutSearch.setVisibility(!showMain ? View.VISIBLE : View.GONE);
    }

    private class FindUser extends AsyncTask<Void, Void, String> {

        private String usernameOrEmail;
        private String userId;
        private OkHttpClient client;

        public FindUser(String usernameOrEmail, String userId) {
            this.userId = userId;
            this.usernameOrEmail = usernameOrEmail;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = BuildConfig.BASE_ENDPOINT + "account/friend/find";

            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            urlBuilder.addQueryParameter("usernameOrEmail", usernameOrEmail);
            urlBuilder.addQueryParameter("userId", userId);
            Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return "ERROR";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("ERROR")) {
                System.out.println("IF 1");
                Toast.makeText(MainActivity.this, "Service unavailable, please try again later", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    boolean error = object.getBoolean("error");
                    if (!error) {
                        if (object.has("users")) {
                            JSONArray array = object.getJSONArray("users");
                            usersArray = array;
                            populateAdapter(array);
                        }
                    } else {
                        System.out.println("ELSE 1");
                        Toast.makeText(MainActivity.this, "Service unavailable, please try again later", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    System.out.println("CATCH 1");
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Service unavailable, please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
