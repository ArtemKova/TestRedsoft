package com.ka.testredsoft.api;

import com.ka.testredsoft.pojo.Example;
import com.ka.testredsoft.pojo.ExampleCard;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiProdCard {
    @GET("products/{id}")
    Observable<ExampleCard> getData(@Path("id") int id);
}
