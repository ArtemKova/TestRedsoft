package com.ka.testredsoft.api;

import com.ka.testredsoft.pojo.Example;
import com.ka.testredsoft.pojo.ExampleCard;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET("products")
    Observable<Example> getData();


}
