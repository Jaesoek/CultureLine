package com.culture.inha.cultureline.Posting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.BaseActivity;
import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.DataSet.User;
import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.Home.HomeActivity;
import com.culture.inha.cultureline.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;
import static com.culture.inha.cultureline.Home.MainBoardFragment.RC_POST_QUESTION;

public class PostQuestionActivity extends BaseActivity {

    TextView txtCategory;
    EditText editTitle;
    EditText editContents;
    Button btnPost;

    boolean isFinish;
    boolean[] isSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postquestion);
        setupUI(findViewById(R.id.main_parent));

        LinearLayout viewCancel = findViewById(R.id.viewCancel);
        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtCategory = findViewById(R.id.txtCategory);
        editTitle = findViewById(R.id.editTitle);
        editContents = findViewById(R.id.editContents);
        btnPost = findViewById(R.id.btnPost);

        isPostFinish();

        btnPost.setVisibility(View.VISIBLE);
        btnPost.setBackgroundResource(R.drawable.button_corner_before);

        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFinish)
                    Toast.makeText(PostQuestionActivity.this, "내용을 입력하여 버튼을 활성화해주세요", Toast.LENGTH_SHORT).show();
                else {
                    postQuestion();
                }
            }
        });

        isSelected = new boolean[GlobalDataSet.categories.size()];
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("질문의 카테고리를 설정해주세요")
                .setMultiChoiceItems(GlobalDataSet.categories.toArray(new String[GlobalDataSet.categories.size()]),
                        isSelected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index, boolean isOk) {
                isSelected[index] = isOk;
            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                txtCategory.setText(GlobalDataSet.getCategoryName(isSelected));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void isPostFinish() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFinish = editTitle.getText().toString().length() != 0
                        && editContents.getText().toString().length() != 0
                        && txtCategory.getText().toString().length() != 0;
                if (isFinish) {
                    btnPost.setBackgroundResource(R.drawable.button_corner);
                } else {
                    btnPost.setBackgroundResource(R.drawable.button_corner_before);
                }
            }
        };
        txtCategory.addTextChangedListener(textWatcher);
        editTitle.addTextChangedListener(textWatcher);
        editContents.addTextChangedListener(textWatcher);
    }

    private void postQuestion() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<Question> call = apiInterface.postQuestion(
                getSharedPreferences("user", 0).getString("token", "null"),
                GlobalDataSet.setCategoryName(isSelected),
                editTitle.getText().toString(),
                editContents.getText().toString()
        );

        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                Toast.makeText(PostQuestionActivity.this, "질문등록에 성공하였습니다", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("question", response.body());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.d("FUck111", "fuck");
                Toast.makeText(PostQuestionActivity.this,
                        "질문등록에 실패하였습니다\n잠시 후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(PostQuestionActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
