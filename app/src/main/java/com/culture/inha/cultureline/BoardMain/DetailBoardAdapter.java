package com.culture.inha.cultureline.BoardMain;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.DataSet.Author;
import com.culture.inha.cultureline.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class DetailBoardAdapter extends RecyclerView.Adapter<DetailBoardAdapter.ViewHolder> {
    private final String TAG = "DetailBoardAdapter";

    private List<Answer> mAnswerSet;
    private OnItemClickListener mListener;
    private String mToken;

    public interface OnItemClickListener {
        void onProfileClick(Author author);

        void onUpdateClick(Answer answer);

        void onDeleteClick(Answer answer);
    }

    public DetailBoardAdapter(List<Answer> searchDataSet, OnItemClickListener listener, String token) {
        mAnswerSet = searchDataSet;
        mListener = listener;
        mToken = token;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_detailboard, parent, false);
        DetailBoardAdapter.ViewHolder vh = new ViewHolder(v, mListener, mToken);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtProfile.setText(mAnswerSet.get(position).getAuthor().getName());
        holder.txtContents.setText(mAnswerSet.get(position).getContents());
        holder.txtLikeNum.setText(String.valueOf(mAnswerSet.get(position).getLike()));
        holder.txtDate.setText(mAnswerSet.get(position).getUpdatedAt());
        holder.viewProfile.setOnClickListener(holder);
        holder.viewOption.setOnClickListener(holder);
        holder.answer = mAnswerSet.get(position);

        holder.checkboxLike.setChecked(mAnswerSet.get(position).getLiked() == 1);
        holder.checkboxLike.setOnCheckedChangeListener(holder);
        holder.txtLikeNum.setText(String.valueOf(mAnswerSet.get(position).getLike()));
    }

    @Override
    public int getItemCount() {
        return mAnswerSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CheckBox.OnCheckedChangeListener {
        private final String TAG = "DetailBoard_Holder";

        public LinearLayout viewProfile;
        public CircleImageView imgProfile;
        public AppCompatTextView txtProfile;
        public AppCompatTextView txtContents;
        public AppCompatTextView txtCategory;
        public CheckBox checkboxLike;
        public AppCompatTextView txtLikeNum;
        public AppCompatTextView txtDate;
        public RelativeLayout viewOption;
        public Answer answer;

        private OnItemClickListener listener;
        private String token;
        private ApiInterface apiInterface;

        ViewHolder(View view, OnItemClickListener listener, String token) {
            super(view);
            viewProfile = view.findViewById(R.id.viewProfile);
            imgProfile = view.findViewById(R.id.imgProfile);
            txtProfile = view.findViewById(R.id.txtProfile);
            txtContents = view.findViewById(R.id.txtContent);
            txtCategory = view.findViewById(R.id.txtCategory);
            checkboxLike = view.findViewById(R.id.checkboxLike);
            txtLikeNum = view.findViewById(R.id.txtLikeNum);
            txtDate = view.findViewById(R.id.txtDate);
            viewOption = view.findViewById(R.id.viewOption);
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
                case R.id.viewProfile:
                    listener.onProfileClick(answer.getAuthor());
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                Call<String> call = apiInterface.likeAnswer(token, answer.getQuestionId(), answer.getId());
                checkboxLike.setEnabled(false);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "LikeCheck is worked " + response.body());
                        txtLikeNum.setText(String.valueOf(Integer.valueOf(txtLikeNum.getText().toString()) + 1));
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
                        checkboxLike.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        checkboxLike.setEnabled(true);
                    }
                });
            }
        }

    }
}
