package com.andrei.messager.helpers;

import android.os.AsyncTask;

import com.andrei.messager.IDataSubscriber;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpTask extends AsyncTask<String, Void, String> {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private IDataSubscriber dataSubscriber;
    private OkHttpClient client;
    private RequestBody requestBody;
    private Boolean error;

    public HttpTask(final IDataSubscriber dataSubscriber) {
        this.dataSubscriber = dataSubscriber;
        this.client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
        this.error = false;
    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        try {
            String url = strings[0];
            String method = strings[1];
            String body = strings[2];
            if (body != null) requestBody = RequestBody.create(JSON, body);
            Request request = new Request.Builder()
                    .url(url)
                    .method(method, requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            this.error = true;
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        if (this.error)
            dataSubscriber.onLoadError();
        else
            dataSubscriber.onDataLoaded(s);
    }
}
