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
import com.culture.inha.cultureline.DataSet.LoginResult;
import com.culture.inha.cultureline.DataSet.User;
import com.culture.inha.cultureline.Lib.CustomEditText;
import com.culture.inha.cultureline.LoadGlobalDataActivity;
import com.culture.inha.cultureline.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class JoinActivity extends BaseActivity {

    CustomEditText editName = null;
    CustomEditText editStudentId = null;
    CustomEditText editMajor = null;
    CustomEditText editEmail = null;
    CustomEditText editPassword = null;
    AppCompatButton btnJoin = null;

    User userData = null;
    boolean joinTrigger, nameTrigger, majorTrigger, sIdTrigger, emailTrigger, passwordTrigger, finalTrigger;

    public boolean checkName(String name) {
        return name.length() > 0;
    }

    public boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    // 비밀번호: 최소 8자이상, 숫자와 문자 혼합, 공백 허용 X
    public boolean checkPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        boolean isNormal = m.matches();
        return isNormal;
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
                emailTrigger = checkEmail(editEmail.getText().toString());
                passwordTrigger = checkPassword(editPassword.getText().toString());

                joinTrigger = nameTrigger && majorTrigger && emailTrigger && passwordTrigger && sIdTrigger;

                if (joinTrigger) {
                    userData.setName(editName.getText().toString());
                    userData.setStuId(editStudentId.getText().toString());
                    userData.setMajor(editMajor.getText().toString());
                    userData.setEmail(editEmail.getText().toString());
                    btnJoin.setBackgroundResource(R.drawable.button_corner);
                } else {
                    btnJoin.setBackgroundResource(R.drawable.button_corner_before);
                }
            }
        };

        editName.addTextChangedListener(textWatcher);
        editStudentId.addTextChangedListener(textWatcher);
        editMajor.addTextChangedListener(textWatcher);
        editEmail.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinTrigger) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

                    Call<LoginResult> call = apiInterface.register(userData.getName(),
                            userData.getStuId(), userData.getMajor(),
                            userData.getEmail(), editPassword.getText().toString(),
                            editPassword.getText().toString());
                    call.enqueue(new Callback<LoginResult>() {
                        @Override
                        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                            if (response.body() == null)
                                Log.d("JoinActivity", "Response value is null " + String.valueOf(response.code()));
                            // 존재하는 이메일인 경우
                            else if(response.body().getAccessToken() == null)
                                Toast.makeText(JoinActivity.this, "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                            else {
                                Log.d("JoinActivity", " " + String.valueOf(response.code()));
                                String token = "bearer " + response.body().getAccessToken();
                                getSharedPreferences("user", 0)
                                        .edit().putString("token", token)
                                        .putBoolean("isToken", true)
                                        .putInt("userId", ((User)response.body().getUser()).getId()).
                                        apply();
                                Intent intent = new Intent(getApplicationContext(), LoadGlobalDataActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResult> call, Throwable t) {
                            Log.d("FUck111", "fuck");
                        }
                    });
                }

            }
        });
    }

}
