package com.andrei.messager.ui;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        String body = "{\"email\": \"test@test.com\"}";
        String response = main.post("http://localhost:8080/api/account/isExist", body);
        System.out.println(String.format("{\"email\" : \"%s\"}", "qwe.ads"));
        System.out.println(response);
    }
}
