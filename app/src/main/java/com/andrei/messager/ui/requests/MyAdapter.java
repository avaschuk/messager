package com.andrei.messager.ui.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrei.messager.BuildConfig;
import com.andrei.messager.R;
import com.andrei.messager.helpers.HttpTask;
import com.andrei.messager.model.RequestEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAdapter extends ArrayAdapter<RequestEntity> {

    Context context;
    List<RequestEntity> requestList;
    IUpdateListView iUpdateListView;

    public MyAdapter(@NonNull Context context, int resource, @NonNull List<RequestEntity> objects, IUpdateListView iUpdateListView) {
        super(context, resource, objects);
        this.context = context;
        this.requestList = objects;
        this.iUpdateListView = iUpdateListView;
    }

    @Override
    public int getCount() {
        if (requestList != null) {
            return requestList.size();
        }
        return 0;
    }

    @Nullable
    @Override
    public RequestEntity getItem(int position) {
        if (requestList != null) {
            return requestList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (requestList != null) {
            return  requestList.get(position).hashCode();
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;
        System.out.println("GET VIEW");

        if (convertView == null) {
            System.out.println("IF");
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.request_list_view, null);
            holder.textName = convertView.findViewById(R.id.username_text_view);
            holder.textEmail = convertView.findViewById(R.id.email_text_view);
            convertView.setTag(holder);
        } else {
            System.out.println("ELSE");
            holder = (Holder) convertView.getTag();
        }

        RequestEntity requestEntity = getItem(position);
        System.out.println(requestEntity);
        holder.textName.setText(requestEntity.getUser().getUsername());
        holder.textEmail.setText(requestEntity.getUser().getEmail());
        final ImageButton approveButton = convertView.findViewById(R.id.approve_button);
        final ImageButton declineButton = convertView.findViewById(R.id.decline_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveButton.setEnabled(false);
                System.out.println("ON APPROVE CLICKED");
                System.out.println(position);
                System.out.println(requestList.get(position));
                new UpdateRequest(requestList.get(position), "approved", position).execute();
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ON DECLINE CLICKED");
                System.out.println(requestList.get(position));
                new UpdateRequest(requestList.get(position), "declined", position).execute();
            }
        });

        return convertView;
    }

    private class Holder {
        TextView textName;
        TextView textEmail;
    }

    private class UpdateRequest extends AsyncTask<Void, Void, Boolean> {

        private OkHttpClient client;
        private int position;
        private RequestEntity requestEntity;
        private String status;

        public UpdateRequest(RequestEntity requestEntity, String status, int position) {
            this.status = status;
            this.requestEntity = requestEntity;
            this.position = position;
            this.client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String url = BuildConfig.BASE_ENDPOINT + "account/request/update";
            String body = String.format("{\"requestId\" : \"%s\", \"status\":\"%s\"}", requestEntity.getId(), status);
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(HttpTask.JSON, body))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                System.out.println(response);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            requestList.remove(position);
            iUpdateListView.updateListView(requestList);
        }
    }
}
