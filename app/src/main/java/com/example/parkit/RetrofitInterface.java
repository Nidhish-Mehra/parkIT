package com.example.parkit;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/parkit")
    Call<parkResult> executeParkit(@Body HashMap<String, String> map);

    @POST("/exit")
    Call<exitResult> executeExit(@Body HashMap<String, String> map);

}
