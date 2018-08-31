package com.culture.inha.cultureline.BoardMain;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.R;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

class AnswerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final String TAG = "DetailBoard_Holder";

    public boolean isChecked;
    private boolean isCommentOpen;

    public LinearLayout viewProfile;
    public CircleImageView imgProfile;
    public AppCompatTextView txtProfile;
    public AppCompatTextView txtContents;
    public AppCompatTextView txtCategory;
    public AppCompatImageView imgIsAnswer;
    public AppCompatImageButton checkboxLike;
    public AppCompatTextView txtLikeNum;
    public AppCompatTextView txtNumComment;
    public AppCompatTextView txtDate;
    public RelativeLayout viewOption;
    public RelativeLayout viewComment;
    public Answer answer;

    private DetailBoardAdapter.OnItemClickListener listener;

    private String token;
    private ApiInterface apiInterface;

    public AnswerHolder(View view, DetailBoardAdapter.OnItemClickListener listener, String token) {
        super(view);
        viewProfile = view.findViewById(R.id.viewProfile);
        imgProfile = view.findViewById(R.id.imgProfile);
        txtProfile = view.findViewById(R.id.txtProfile);
        txtContents = view.findViewById(R.id.txtContent);
        txtCategory = view.findViewById(R.id.txtCategory);
        imgIsAnswer = view.findViewById(R.id.imgIsAnswer);
        checkboxLike = view.findViewById(R.id.checkboxLike);
        txtLikeNum = view.findViewById(R.id.txtLikeNum);
        txtDate = view.findViewById(R.id.txtDate);

        viewOption = view.findViewById(R.id.viewOption);
        txtNumComment = view.findViewById(R.id.txtNumComment);
        viewComment = view.findViewById(R.id.viewComment);

        this.listener = listener;
        this.token = token;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.viewOption:
                PopupMenu popup = new PopupMenu(view.getContext(), viewOption);
                popup.inflate(R.menu.menu_modify);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuUpdate:
                                listener.onUpdateClick(answer);
                                return true;
                            case R.id.menuDelete:
                                listener.onDeleteClick(answer);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
                break;
            case R.id.checkboxLike:
                if (!isChecked) {
                    Call<String> call = apiInterface.likeAnswer(token, answer.getQuestionId(), answer.getId());
                    checkboxLike.setEnabled(false);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d(TAG, "LikeCheck is worked " + response.body());
                            txtLikeNum.setText(String.valueOf(Integer.valueOf(txtLikeNum.getText().toString()) + 1));
                            checkboxLike.setBackgroundResource(R.drawable.heart_on);
                            isChecked = !isChecked;
                            checkboxLike.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d(TAG, "LikeCheck is failed");
                            checkboxLike.setEnabled(true);
                        }
                    });
                } else {
                    Call<String> call = apiInterface.likeAnswer(token, answer.getQuestionId(), answer.getId());
                    checkboxLike.setEnabled(false);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            txtLikeNum.setText(String.valueOf(Integer.valueOf(txtLikeNum.getText().toString()) - 1));
                            checkboxLike.setBackgroundResource(R.drawable.heart_off);
                            isChecked = !isChecked;
                            checkboxLike.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            checkboxLike.setEnabled(true);
                        }
                    });
                }
                break;

            case R.id.viewProfile:
                listener.onProfileClick(answer.getAuthor());
                break;
            case R.id.imgIsAnswer:
                listener.onAnswerCheck(answer);
                break;
            case R.id.viewComment:
                if(!isCommentOpen) {
                    ((AppCompatTextView)view.findViewById(R.id.txtComment)).setText("댓글 접기");
                    isCommentOpen = true;
                    listener.onCommentClick(answer, isCommentOpen);
                } else {
                    ((AppCompatTextView)view.findViewById(R.id.txtComment)).setText("댓글 열기");
                    isCommentOpen = false;
                    listener.onCommentClick(answer, isCommentOpen);
                }
                break;
            default:
                break;
        }
    }

}
