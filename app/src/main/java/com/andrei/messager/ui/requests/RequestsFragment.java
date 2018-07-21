package com.andrei.messager.ui.requests;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.MainActivity;
import com.andrei.messager.R;
import com.andrei.messager.helpers.HttpTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RequestsFragment extends Fragment {

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userId = getArguments().getString(MainActivity.ID);
        System.out.println("userId");
        System.out.println(userId);
        if (userId != null) new getRequests(userId).execute();
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }


    private class getRequests extends AsyncTask<Void, Void, String> {

        private String userId;
        private OkHttpClient client;
        private String ERROR = "ERROR";

        public getRequests(String userId) {
            this.userId = userId;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = BuildConfig.BASE_ENDPOINT + "account/request/all";
            String body = String.format("{\"userId\" : \"%s\"}", userId);
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(HttpTask.JSON, body))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return ERROR;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            System.out.println(json);
        }
    }

}
