package com.jk.java;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

abstract public class HttpClient implements IHttpClientListener {

    private OkHttpClient client;
    private String[] url;

    public HttpClient(OkHttpClient client, String[] url) {

        this.client = client;
        this.url = url;

        rqBuilder();
    }

//    public HttpClient() {
//
//    }

    private void rqBuilder() {

        for (String urlText : url) {
            Request request = new Request.Builder()
                    .url(urlText)
                    .method("GET", null)
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Pragma", "no-cache")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("DNT", "1")
                    .addHeader("Upgrade-Insecure-Requests", "1")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                    .addHeader("Sec-Fetch-Dest", "document")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .addHeader("Accept-Language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-CN;q=0.6")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                parseHtmlText(response);

                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
