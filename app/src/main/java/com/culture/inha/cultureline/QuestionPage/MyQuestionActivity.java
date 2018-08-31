package com.culture.inha.cultureline.QuestionPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.BoardMain.DetailBoardActivity;
import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.Home.HomeActivity;
import com.culture.inha.cultureline.Posting.UpdateQuestionActivity;
import com.culture.inha.cultureline.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class MyQuestionActivity extends AppCompatActivity {
    private final String TAG = "MyQuestionActivity";

    public static final int RC_MYQUESTION = 70;

    private ArrayList<Question> mQuestionSet;

    private RecyclerView recycleMyQuestion = null;
    private MyQuestionAdapter mAdapter;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyQuestionActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myquestion);

        LinearLayout viewCancel = findViewById(R.id.viewCancel);
        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyQuestionActivity.this, HomeActivity.class));
                finish();
            }
        });

        mQuestionSet = new ArrayList<>();
        mAdapter = new MyQuestionAdapter(new MyQuestionAdapter.OnMyQuestionListener() {
            @Override
            public void onDetailClick(Question question) {
                Intent intent = new Intent(MyQuestionActivity.this, DetailBoardActivity.class);
                intent.putExtra("question", question);
                startActivityForResult(intent, RC_MYQUESTION);
            }

            @Override
            public void onUpdateClick(Question question) {
                Intent intent = new Intent(getApplicationContext(), UpdateQuestionActivity.class);
                intent.putExtra("question", question);
                startActivityForResult(intent, RC_MYQUESTION);
            }

            @Override
            public void onDeleteClick(Question question) {
                showDeleteQuestionDialog(question);
            }
        });
        recycleMyQuestion = findViewById(R.id.recycleMyQuestion);

        onLoad();
    }

    /**
     * 데이터 로딩
     */
    private void onLoad() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ArrayList<Question>> call = apiInterface.myQuestion(getSharedPreferences("user", 0).getString("token", null));
        call.enqueue(new Callback<ArrayList<Question>>() {
            @Override
            public void onResponse(Call<ArrayList<Question>> call, Response<ArrayList<Question>> response) {
                if (response.body().size() != 0) {
                    mQuestionSet.addAll(response.body());
                    recycleMyQuestion.setAdapter(mAdapter);
                    mAdapter.addAll(response.body());
                } else {
                    findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Question>> call, Throwable t) {
                Log.d(TAG, "Failed");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // question이 intent로 돌아오면 답변의 개수 변경(답변 삭제 및 답변 추가)
            if (data.getSerializableExtra("question") != null) {
                Question questionFromIntent = (Question) data.getSerializableExtra("question");
                mQuestionSet.set(getQuestionPosition(questionFromIntent.getId()), questionFromIntent);
                mAdapter.changeItem(getQuestionPosition(questionFromIntent.getId()), questionFromIntent);
            }
            // 질문이 삭제될 경우 intent에는 int형 questionId가 있음
            else {
                int deletedQuestionPosition = getQuestionPosition(data.getIntExtra("questionId", -1));
                mQuestionSet.remove(deletedQuestionPosition);
                mAdapter.removeItem(deletedQuestionPosition);
            }
        }
        if (mQuestionSet.size() == 0) {
            findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 질문 삭제
     */
    private void showDeleteQuestionDialog(final Question question) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("해당 게시물을 삭제하시겠습니까?")
                .setMessage("확인을 누르실 경우 게시물이 삭제됩니다")
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(baseUrl)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .build();
                        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                        Call<String> call = apiInterface.deleteQuestion(
                                getSharedPreferences("user", 0).getString("token", "null"),
                                question.getId()
                        );
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Toast.makeText(getApplicationContext(), "질문이 삭제되었습니다", Toast.LENGTH_LONG).show();

                                int qPos = getQuestionPosition(question.getId());
                                mQuestionSet.remove(qPos);
                                mAdapter.removeItem(qPos);

                                if (mQuestionSet.size() == 0) {
                                    findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "질문 삭제가 실패하였습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getQuestionPosition(int qId) {
        int result = 0;
        for (Question question : mQuestionSet) {
            if (question.getId() == qId)
                return result;
            result++;
        }
        return result;
    }

}
