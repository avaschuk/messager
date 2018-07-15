package com.andrei.messager.ui.singup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.IDataSubscriber;
import com.andrei.messager.helpers.HttpTask;

public class HttpCreateAccount implements IDataSubscriber {

    private final Context context;
    private final HttpTask httpTask;
    private final IMainActivityCreateAccountView mainActivity;

    public HttpCreateAccount(Context context, IMainActivityCreateAccountView mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.httpTask = new HttpTask(this);
    }

    @Override
    public void onDataLoaded(String data) {
        mainActivity.login(data);
    }

    @Override
    public void onLoadError() {
        mainActivity.errorWhileCreateAccount();
    }

    public void createAccount(String email, String password) {
        if (isNetworkAvailable()) {
            String url = BuildConfig.BASE_ENDPOINT + "account/create";
            String body = String.format("{\"email\" : \"%s\", \"password\":\"%s\"}", email, password);
            String [] dataArray = {url, "POST", body};
            httpTask.execute(dataArray);
        } else {
            Toast.makeText(context, "Network not available.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {return false;}

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
