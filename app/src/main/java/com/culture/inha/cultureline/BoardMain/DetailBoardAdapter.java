package com.culture.inha.cultureline.BoardMain;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.DataSet.Author;
import com.culture.inha.cultureline.DataSet.Comment;
import com.culture.inha.cultureline.Lib.DecodeBitMap;
import com.culture.inha.cultureline.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class DetailBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "DetailBoardAdapter";

    public static final int ANSWER = 0;
    public static final int COMMENT = 1;

    private ArrayList<ItemForDetail> mDataSet;
    private OnItemClickListener mListener;
    private String mToken;

    public interface OnItemClickListener {
        void onProfileClick(Author author);

        void onUpdateClick(Answer answer);

        void onDeleteClick(Answer answer);

        void onAnswerCheck(Answer answer);

        void onCommentClick(Answer answer, boolean isOpened);

//        void onCommentPost();
//        void onCommentUpdate();
//        void onCommentDelete();
    }

    public DetailBoardAdapter(OnItemClickListener listener, String token) {
        mDataSet = new ArrayList<>();
        mListener = listener;
        mToken = token;
    }

    public void changeItem(int position, ItemForDetail item) {
        mDataSet.set(position, item);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        if (mDataSet != null && mDataSet.size() > 0) {
            mDataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addAll(ArrayList<ItemForDetail> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    public void addItemMore(ItemForDetail item) {
        mDataSet.add(0, item);
        notifyItemInserted(0);
    }
    public void addItemMore(int position, ItemForDetail item) {
        mDataSet.add(position, item);
        notifyItemRangeChanged(position + 1, 1);
    }
    public void addItemMore(int position, ArrayList<ItemForDetail> item) {
        mDataSet.addAll(position, item);
        notifyItemRangeChanged(position + 1, item.size());
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ANSWER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_detailboard, parent, false);
            return new AnswerHolder(view, mListener, mToken);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comment, parent, false);
            return new CommentHolder(view, mToken);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _holder, int position) {
        if (_holder instanceof AnswerHolder) {
            AnswerHolder holder = (AnswerHolder) _holder;

            holder.txtProfile.setText(mDataSet.get(position).getAnswer().getAuthor().getName());
            holder.txtContents.setText(mDataSet.get(position).getAnswer().getContents());
            holder.txtLikeNum.setText(String.valueOf(mDataSet.get(position).getAnswer().getLike()));
            holder.txtDate.setText(mDataSet.get(position).getAnswer().getUpdatedAt());
            holder.viewProfile.setOnClickListener(holder);
            holder.viewOption.setOnClickListener(holder);
            holder.answer = mDataSet.get(position).getAnswer();

            if (mDataSet.get(0).getAnswer().getSelected() != 0) {
                if (position == 0) {
                    holder.imgIsAnswer.setVisibility(View.VISIBLE);
                    holder.imgIsAnswer.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(
                            holder.itemView.getContext().getResources(),
                            R.drawable.answer_on, 85, 70));
                    holder.imgIsAnswer.setOnClickListener(holder);
                } else {
                    holder.imgIsAnswer.setVisibility(View.GONE);
                }
            } else {
                holder.imgIsAnswer.setVisibility(View.VISIBLE);
                holder.imgIsAnswer.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(
                        holder.itemView.getContext().getResources(),
                        R.drawable.answer_off, 85, 70));
                holder.imgIsAnswer.setOnClickListener(holder);
            }

            if (mDataSet.get(position).getAnswer().getLiked() == 1) {
                holder.checkboxLike.setBackgroundResource(R.drawable.heart_on);
                holder.isChecked = true;
            } else {
                holder.checkboxLike.setBackgroundResource(R.drawable.heart_off);
                holder.isChecked = false;
            }
            holder.checkboxLike.setOnClickListener(holder);
            holder.txtLikeNum.setText(String.valueOf(mDataSet.get(position).getAnswer().getLike()));

            holder.viewComment.setOnClickListener(holder);
        } else {
            CommentHolder holder = (CommentHolder) _holder;


        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
