package com.andrei.messager.helpers;

import android.os.AsyncTask;

import com.andrei.messager.IDataSubscriber;



public class HttpGetTask extends AsyncTask<String, Void, String> {

    private IDataSubscriber dataSubscriber;

    public HttpGetTask(final IDataSubscriber dataSubscriber) {
        this.dataSubscriber = dataSubscriber;
    }

    @Override
    protected String doInBackground(String... urls) {
        String url = urls[0];
        System.out.println("url -" + url);
        String response = "response X Y I";
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        dataSubscriber.onDataLoaded(s);
    }
}
