package com.bigbang.superteam.rest;

import com.bigbang.superteam.common.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import static com.bigbang.superteam.util.Constant.URL;
import static com.bigbang.superteam.util.Constant.URL1;

/**
 * Created by dgohil on 6/17/15.
 */
public class RestClient extends BaseActivity {
    private static CommonService REST_CLIENT_COMMON_SERVICE;
    private static CommonService REST_CLIENT_TEAM_WORK;

    static {
        setupRestClient();
    }

    private RestClient() {
    }

    private static void setupRestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory()) // This is the important line ;)
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        REST_CLIENT_COMMON_SERVICE = buildAdapter(URL1, gson).create(CommonService.class);
        REST_CLIENT_TEAM_WORK = buildAdapter(URL, gson).create(CommonService.class);
    }

    private static RestAdapter buildAdapter(String endPoint, Gson gson) {

        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(endPoint)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    public static CommonService getCommonService() {
        return REST_CLIENT_COMMON_SERVICE;
    }

    public static CommonService getCommonService3() {
        return REST_CLIENT_COMMON_SERVICE;
    }

    public static CommonService getTeamWork() {
        return REST_CLIENT_TEAM_WORK;
    }

}
