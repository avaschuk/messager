package com.andrei.messager.ui.singup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.IDataSubscriber;
import com.andrei.messager.helpers.HttpTask;

public class HttpCheckEmail implements IDataSubscriber {

    private final Context context;
    private final HttpTask httpTask;
    private final ISignUpActivityView mainActivityView;

    public HttpCheckEmail(Context context, final ISignUpActivityView mainActivityView) {
        this.context = context;
        this.mainActivityView = mainActivityView;
        this.httpTask = new HttpTask(this);
    }

    @Override
    public void onDataLoaded(String data) {
        System.out.println("onDataLoaded in http login");
        System.out.println(data);
        mainActivityView.nextStep(data);
    }

    @Override
    public void onLoadError() {
        mainActivityView.returnUi();
        Toast.makeText(context, "Service unavailable. Please try again later", Toast.LENGTH_LONG).show();
    }

    void checkEmail(String email) {
        if (isNetworkAvailable()) {
            String url = BuildConfig.BASE_ENDPOINT + "account/isExist";
            String body = String.format("{\"email\" : \"%s\"}", email);
            String [] dataArray = {url, "POST", body};
            httpTask.execute(dataArray);
        } else {
            Toast.makeText(context, "Network not available.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {return false;}

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
