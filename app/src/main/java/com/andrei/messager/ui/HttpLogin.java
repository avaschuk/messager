package com.andrei.messager.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.andrei.messager.IDataSubscriber;
import com.andrei.messager.helpers.HttpGetTask;

public class HttpLogin implements IDataSubscriber {

    private final Context context;
    private final HttpGetTask httpGetTask;

    public HttpLogin(Context context) {
        this.context = context;
        this.httpGetTask = new HttpGetTask(this);
    }

    @Override
    public void onDataLoaded(String data) {
        System.out.println("onDataLoaded in http login");
        System.out.println(data);
    }

    @Override
    public void onLoadError() {

    }

    void checkEmail() {
        System.out.println("checkEmail");
        if (isNetworkAvailable()) {
            System.out.println("if available");
            String [] url = {"http://qwe.com"};
            httpGetTask.execute(url);
        }
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {return false;}

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
