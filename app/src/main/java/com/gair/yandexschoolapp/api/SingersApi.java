package com.gair.yandexschoolapp.api;

import com.gair.yandexschoolapp.entity.Singer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SingersApi {
    @GET("mobilization-2016/artists.json")
    Call<List<Singer>> getSingers();
}

