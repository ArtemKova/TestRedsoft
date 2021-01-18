package com.ka.testredsoft.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import com.ka.testredsoft.ProductCard;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    private static ApiFactory apiFactory;
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://rstestapi.redsoftdigital.com/api/v1/";

    private ApiFactory(){
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

    }

    public static ApiFactory getInstance(){
        if (apiFactory == null){
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }
    public ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }

    public ApiProdCard getApiProdCard(){
            return retrofit.create(ApiProdCard.class);
    }

}