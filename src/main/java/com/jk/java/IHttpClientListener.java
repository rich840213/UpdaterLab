package com.jk.java;

import okhttp3.Response;

public interface IHttpClientListener {

    void parseHtmlText(Response response);
}
