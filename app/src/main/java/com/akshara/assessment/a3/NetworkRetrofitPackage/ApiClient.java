package com.akshara.assessment.a3.NetworkRetrofitPackage;

import com.akshara.assessment.a3.BuildConfig;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {


    public static final String BASE_URL = BuildConfig.HOST;
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {


            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            ;

        }
        return retrofit;
    }
}