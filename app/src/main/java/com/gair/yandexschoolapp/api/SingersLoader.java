package com.gair.yandexschoolapp.api;

import com.gair.yandexschoolapp.db.SingersOpenHelper;
import com.gair.yandexschoolapp.entity.Singer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingersLoader {

    private  SingersApi singersApi;

    public SingersLoader() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://download.cdn.yandex.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        singersApi = retrofit.create(SingersApi.class);
    }


    public void getSingersAsync(final LoadStatus callback) {
        singersApi.getSingers().enqueue(new Callback<List<Singer>>() {
            @Override
            public void onResponse(Call<List<Singer>> call, Response<List<Singer>> response) {
                callback.success((ArrayList<Singer>) response.body());
            }

            @Override
            public void onFailure(Call<List<Singer>> call, Throwable t) {
                callback.error(t.getMessage());
            }
        });
    }


    public interface LoadStatus {
        void success(ArrayList<Singer> singersList);
        void error(String message);
    }

}
