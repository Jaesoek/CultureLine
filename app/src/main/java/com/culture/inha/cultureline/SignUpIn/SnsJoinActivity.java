package com.culture.inha.cultureline.SignUpIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.BaseActivity;
import com.culture.inha.cultureline.DataSet.User;
import com.culture.inha.cultureline.Lib.CustomEditText;
import com.culture.inha.cultureline.LoadGlobalDataActivity;
import com.culture.inha.cultureline.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class SnsJoinActivity extends BaseActivity {

    CustomEditText editName = null;
    CustomEditText editStudentId = null;
    CustomEditText editMajor = null;
    CustomEditText editEmail = null;
    CustomEditText editPassword = null;
    AppCompatButton btnJoin = null;

    User userData = null;
    boolean joinTrigger, nameTrigger, majorTrigger, sIdTrigger, finalTrigger;

    public boolean checkName(String name) {
        return name.length() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        editName = findViewById(R.id.editName);
        editStudentId = findViewById(R.id.editStudentId);
        editMajor = findViewById(R.id.editMajor);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editEmail.setVisibility(View.GONE);
        editPassword.setVisibility(View.GONE);

        btnJoin = findViewById(R.id.btnJoin);

        userData = new User();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nameTrigger = editName.getText().toString().length() != 0;
                majorTrigger = editMajor.getText().toString().length() != 0;
                sIdTrigger = editStudentId.getText().toString().length() != 0;

                joinTrigger = nameTrigger && majorTrigger && sIdTrigger;

                if (joinTrigger) {
                    userData.setName(editName.getText().toString());
                    userData.setStuId(editStudentId.getText().toString());
                    userData.setMajor(editMajor.getText().toString());
                    btnJoin.setBackgroundResource(R.drawable.button_corner);
                } else {
                    btnJoin.setBackgroundResource(R.drawable.button_corner_before);
                }
            }
        };

        editName.addTextChangedListener(textWatcher);
        editStudentId.addTextChangedListener(textWatcher);
        editMajor.addTextChangedListener(textWatcher);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinTrigger) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

                    Call<User> call = apiInterface.registerSns(getIntent().getStringExtra("token"),
                            userData.getName(), userData.getStuId(), userData.getMajor());

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            Log.d("Success", "Connecting Success");
                            getSharedPreferences("user", 0)
                                    .edit().putString("token", getIntent().getStringExtra("token"))
                                    .putBoolean("isToken", true)
                                    .putInt("userId", response.body().getId()).apply();
                            Intent intent = new Intent(getApplicationContext(), LoadGlobalDataActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("FUck111", "fuck");
                            Toast.makeText(SnsJoinActivity.this, "서버와의 통신이 불안정합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

}

