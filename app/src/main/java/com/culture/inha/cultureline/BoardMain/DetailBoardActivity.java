package com.culture.inha.cultureline.BoardMain;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.culture.inha.cultureline.Account.ProfileActivity;
import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.BaseActivity;
import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.DataSet.Author;
import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.Home.HomeActivity;
import com.culture.inha.cultureline.Lib.AutoResizeTextView;
import com.culture.inha.cultureline.Posting.PostAnswerActivity;
import com.culture.inha.cultureline.Posting.UpdateAnswerActivity;
import com.culture.inha.cultureline.Posting.UpdateQuestionActivity;
import com.culture.inha.cultureline.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class DetailBoardActivity extends BaseActivity {

    private final String TAG = "DetailBoardActivity";

    public static final int RC_POST_ANSWER = 90;
    public static final int RC_UPDATE_ANSWER = 91;
    public static final int RC_UPDATE_QUESTION = 81;

    RecyclerView recyclerView = null;
    private RecyclerView.Adapter<DetailBoardAdapter.ViewHolder> mAdapter;
    private Question mQuestion;

    private TextView txtCategory;
    private LinearLayout viewCancel;
    private RelativeLayout viewOption;
    private AutoResizeTextView txtTitle;
    private AppCompatTextView txtContent;
    private AppCompatTextView txtNumAnswer;
    private AppCompatTextView txtDate;
    private AppCompatButton btnAnswer;

    private ApiInterface apiInterface;

    // 변경사항 여부 체크용
    private boolean isChanged;

    @Override
    public void onBackPressed() {
        Log.d(TAG, "test");

        if (isChanged) {
            Intent intent = new Intent();
            intent.putExtra("question", mQuestion);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailboard);
        setupActivity();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    private void setupActivity() {
        mQuestion = (Question) getIntent().getSerializableExtra("question");

        viewCancel = findViewById(R.id.viewCancel);
        txtCategory = findViewById(R.id.txtCategory);
        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtContent);

        txtNumAnswer = findViewById(R.id.txtNumAnswer);
        txtDate = findViewById(R.id.txtDate);
        btnAnswer = findViewById(R.id.btnAnswer);

        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChanged) {
                    Intent intent = new Intent();
                    intent.putExtra("question", mQuestion);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), PostAnswerActivity.class)
                                .putExtra("question", mQuestion),
                        RC_POST_ANSWER);
            }
        });
        viewOption = findViewById(R.id.viewOption);
        viewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), viewOption);
                popup.inflate(R.menu.menu_modify);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuUpdate:
                                updateQuestion();
                                return true;
                            case R.id.menuDelete:
                                deleteQuestion();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        recyclerView = findViewById(R.id.recycleDetailBoard);

        txtCategory.setText(GlobalDataSet.getCategoryName(mQuestion.getCategoryId()));
        txtTitle.setText(mQuestion.getTitle());
        txtContent.setText(mQuestion.getContents());
        txtNumAnswer.setText(String.valueOf(mQuestion.getAnswers().size()));
        txtDate.setText(mQuestion.getUpdatedAt());

        /** 프로필과 답변들의 수정및 삭제 관련 리스너 정의*/
        mAdapter = new DetailBoardAdapter(mQuestion.getAnswers(), new DetailBoardAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(Author author) {
                Intent intent = new Intent(DetailBoardActivity.this, ProfileActivity.class);
                intent.putExtra("author", author);
                startActivity(intent);
            }

            @Override
            public void onUpdateClick(Answer mAnswer) {
                // 답변한 유저
                if (mAnswer.getAuthorId().intValue() == GlobalDataSet.user.getId().intValue()) {
                    Intent intent = new Intent(getApplicationContext(), UpdateAnswerActivity.class);
                    intent.putExtra("question", mQuestion);
                    intent.putExtra("answer", mAnswer);
                    startActivityForResult(intent, RC_UPDATE_ANSWER);
                } else {
                    Toast.makeText(DetailBoardActivity.this, "답변을 작성한 분이 아니네요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(Answer mAnswer) {
                // 질문 작성자 와 답변한 유저
                if (mQuestion.getAuthorId().intValue() == GlobalDataSet.user.getId().intValue() ||
                        mAnswer.getAuthorId().intValue() == GlobalDataSet.user.getId().intValue()) {
                    showDelteAnswerDialog(mAnswer);
                } else {
                    Toast.makeText(DetailBoardActivity.this,
                            "답변을 작성한 분이나 질문 작성자가 아니네요", Toast.LENGTH_SHORT).show();
                }
            }
        }, getSharedPreferences("user", 0).getString("token", null));

        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void updateQuestion() {
        if (mQuestion.getAuthorId().intValue() == GlobalDataSet.user.getId().intValue()) {
            Intent intent = new Intent(getApplicationContext(), UpdateQuestionActivity.class);
            intent.putExtra("question", mQuestion);
            startActivityForResult(intent, RC_UPDATE_QUESTION);
        } else {
            Toast.makeText(DetailBoardActivity.this, "질문을 작성한 분이 아니네요", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteQuestion() {
        if (mQuestion.getAuthorId().intValue() == GlobalDataSet.user.getId().intValue()) {
            showDeleteQuestionDialog();
        } else {
            Toast.makeText(DetailBoardActivity.this, "질문을 작성한 분이 아니네요", Toast.LENGTH_SHORT).show();
        }

    }

    private void showDeleteQuestionDialog() {
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
                        Call<String> call = apiInterface.deleteQuestion(
                                getSharedPreferences("user", 0).getString("token", "null"),
                                mQuestion.getId()
                        );
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Toast.makeText(getApplicationContext(), "질문이 삭제되었습니다", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(DetailBoardActivity.this, HomeActivity.class);
                                intent.putExtra("questionId", mQuestion.getId().intValue());
                                setResult(RESULT_OK, intent);
                                finish();
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

    private void showDelteAnswerDialog(final Answer mAnswer) {
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
                        Call<String> call = apiInterface.deleteAnswer(
                                getSharedPreferences("user", 0).getString("token", "null"),
                                mQuestion.getId(),
                                mAnswer.getId()
                        );
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Toast.makeText(DetailBoardActivity.this, "답변이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                isChanged = true;
                                mQuestion.getAnswers().remove(getAnswerPosition(mAnswer));
                                mAdapter.notifyDataSetChanged();
                                txtNumAnswer.setText(String.valueOf(mQuestion.getAnswers().size()));
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(DetailBoardActivity.this, "답변삭제가 실패하였습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 답변 등록/수정 에 대한 처리
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 답변 추가
        if (requestCode == RC_POST_ANSWER) {
            if (resultCode == RESULT_OK) {
                isChanged = true;

                mQuestion.getAnswers().add(0, (Answer) data.getSerializableExtra("answer"));
                mAdapter.notifyDataSetChanged();
                txtNumAnswer.setText(String.valueOf(mQuestion.getAnswers().size()));
            }
        }
        // 답변 정보 수정
        else if (requestCode == RC_UPDATE_ANSWER) {
            if (resultCode == RESULT_OK) {
                isChanged = true;
                Answer answer = (Answer) data.getSerializableExtra("answer");
                mQuestion.getAnswers().get(getAnswerPosition(answer)).setContents(answer.getContents());
                mAdapter.notifyDataSetChanged();
            }
        }
        // Question 정보 수정
        else if (requestCode == RC_UPDATE_QUESTION) {
            if (resultCode == RESULT_OK) {
                isChanged = true;
                Question question = (Question) data.getSerializableExtra("question");
                mQuestion = question;

                txtCategory.setText(GlobalDataSet.getCategoryName(mQuestion.getCategoryId()));
                txtTitle.setText(mQuestion.getTitle());
                txtContent.setText(mQuestion.getContents());
                txtDate.setText(mQuestion.getUpdatedAt());
            }
        }
    }

    private int getAnswerPosition(Answer answer) {
        int result = 0;
        for (Answer temp : mQuestion.getAnswers()) {
            if (answer.getId().intValue() == temp.getId().intValue())
                return result;
            result++;
        }
        return result;
    }
}