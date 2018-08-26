package com.culture.inha.cultureline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.DataSet.Categories;
import com.culture.inha.cultureline.DataSet.User;
import com.culture.inha.cultureline.Home.HomeActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class LoadGlobalDataActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    @Override
    public void onStart() {
        super.onStart();
        // 유저 데이터 / 카테고리 정보 서버로부터 받아오기
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        Call<ArrayList<Categories>> callLoad = apiInterface.category(getSharedPreferences("user", 0).getString("token", "hello"));
        callLoad.enqueue(new Callback<ArrayList<Categories>>() {
            @Override
            public void onResponse(Call<ArrayList<Categories>> call, Response<ArrayList<Categories>> response) {
                if (response.body() == null) {
                    Log.d("LoadGlobalDataActivity", "Response value is null " + String.valueOf(response.code()));
                } else {
                    Log.d("LoadGlobalDataActivity", "First Load GlobalDataActivity is success");
                    ArrayList<String> categories = new ArrayList<>();
                    for (int i = 0; i < response.body().size(); i++) {
                        categories.add(response.body().get(i).getName());
                    }
                    GlobalDataSet.categories = categories;
                    loadUserData();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Categories>> call, Throwable t) {
                Log.d("LoadGlobalDataActivity", "Load GlobalDataActivity is failed");
            }
        });
    }

    private void loadUserData() {
        Call<User> callLoad = apiInterface.myProfile(getSharedPreferences("user", 0).getString("token", "hello"));
        callLoad.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null) {
                    Log.d("LoadGlobalDataActivity", "Response value is null " + String.valueOf(response.code()));
                } else {
                    Log.d("LoadGlobalDataActivity", "Final Load GlobalDataActivity is success");
                    GlobalDataSet.user = response.body();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("LoadGlobalDataActivity", "Load GlobalDataActivity is failed");
            }
        });
    }
}
