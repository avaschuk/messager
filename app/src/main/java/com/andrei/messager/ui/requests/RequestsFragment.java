package com.andrei.messager.ui.requests;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.MainActivity;
import com.andrei.messager.R;
import com.andrei.messager.helpers.HttpTask;
import com.andrei.messager.model.RequestEntity;
import com.andrei.messager.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RequestsFragment extends ListFragment implements IUpdateListView {

    private ListView listView;
    private String userId;
    private MyAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);
        Activity activity = getActivity();
        userId = getArguments().getString(MainActivity.ID);
        listView = rootView.findViewById(android.R.id.list);

        if (userId != null) new getRequests(userId).execute();
        return rootView;
    }

    public void showRequests(String json) {
        List<RequestEntity> listRequest = new ArrayList<>();
        try {
            JSONObject requestsArray = new JSONObject(json);
            if (!requestsArray.isNull("requests") && requestsArray.has("requests")) {
                JSONArray array = requestsArray.getJSONArray("requests");
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        RequestEntity requestEntity = new RequestEntity();
                        requestEntity.setId(object.getString("id"));
                        User user = new User();
                        JSONObject userObject = object.getJSONObject("account");
                        user.setUsername(userObject.getString("username"));
                        user.setEmail(userObject.getString("email"));
                        user.setId(userObject.getString("id"));
                        requestEntity.setUser(user);

                        System.out.println("object in request fragment");
                        System.out.println(object);
                        listRequest.add(requestEntity);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("getActivity()");
        System.out.println(getActivity());
        myAdapter = new MyAdapter(getActivity(), android.R.id.list, listRequest, this);
        listView.setAdapter(myAdapter);
    }

    @Override
    public void updateListView(List<RequestEntity> requestList) {
        myAdapter.addAll(requestList);
        myAdapter.notifyDataSetChanged();
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
            if (!json.equals(ERROR)) showRequests(json);
        }
    }

}
