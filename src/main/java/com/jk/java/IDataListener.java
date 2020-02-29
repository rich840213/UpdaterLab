package com.jk.java;

import okhttp3.Response;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface IDataListener  {

    void getAppDatas(LinkedHashMap<String, LinkedHashMap<String, String>> datas);
}
