package com.culture.inha.cultureline.SignUpIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.DataSet.LoginResult;
import com.culture.inha.cultureline.DataSet.User;
import com.culture.inha.cultureline.Lib.CustomEditText;
import com.culture.inha.cultureline.LoadGlobalDataActivity;
import com.culture.inha.cultureline.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class LoginActivity extends AppCompatActivity {

    private CustomEditText editEmail;
    private CustomEditText editPassword;
    private SignInButton btnGoogle;
    private Button btnLogin;
    private Button btnJoinEmail;

    private GoogleSignInClient mGoogleSignInClient;

    SharedPreferences pref;

    private final String TAG = "LoginActivity";
    static final int RC_GOOGLE_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("user", 0);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent singInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(singInIntent, RC_GOOGLE_SIGN_IN);
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogin();
            }
        });
        btnJoinEmail = findViewById(R.id.btnJoinEmail);
        btnJoinEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        setGoogleLogin();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getSharedPreferences("user", 0).getBoolean("isToken", false)) {
            startActivity(new Intent(this, LoadGlobalDataActivity.class));
            finish();
        }
    }

    private void setLogin() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<LoginResult> call = apiInterface
                .login(editEmail.getText().toString(),
                        editPassword.getText().toString());

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.body() == null || response.body().getAccessToken() == null) {
                    Toast.makeText(LoginActivity.this, "잘못된 이메일과 비밀번호입니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    String token = "bearer " + response.body().getAccessToken();

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("token", token);
                    editor.putBoolean("isToken", true);
                    editor.putInt("userId", response.body().getUser().getId());
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, LoadGlobalDataActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.d("FUck111", "fuck");
            }
        });
    }

    private void setGoogleLogin() {
        // 구글 이용자 정보 저장위한 객체
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Google 계정으로 로그인
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d("Test", "token" + account.getIdToken());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                Call<LoginResult> call = apiInterface.registGoogle(account.getEmail(), account.getIdToken());
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        // 서버와의 연결에 뭔가 문제가 있는 경우
                        if (response.body() == null)
                            Log.d("LoginActivity", "Response value is null" + String.valueOf(response.code()));
                            // 이미 같은 이메일로 회원가입이 되어있는 경우
                        else if (response.body().getAccessToken() == null) {
                            Toast.makeText(LoginActivity.this, "이 이메일로는 이미 가입이 되어있네요", Toast.LENGTH_SHORT).show();
                        }
                        // 이미 가입된 유저인 경우
                        else if (response.body().getUser() != null) {
                            String token = "bearer " + response.body().getAccessToken();

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token", token);
                            editor.putBoolean("isToken", true);
                            editor.putInt("userId", response.body().getUser().getId());
                            editor.apply();

                            startActivity(new Intent(LoginActivity.this, LoadGlobalDataActivity.class));
                            finish();
                        }
                        // 가입이 되어있지 않은 경우
                        else {
                            String token = "bearer " + response.body().getAccessToken();

                            Intent intent = new Intent(getApplicationContext(), SnsJoinActivity.class);
                            intent.putExtra("token", token);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Log.d("FUck111", "fuck");
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
                Log.d(TAG, "Faile");
            }
        }
    }

}
