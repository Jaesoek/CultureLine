package com.culture.inha.cultureline.Home;

import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.culture.inha.cultureline.DataSet.Author;
import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.Lib.AutoResizeTextView;

import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.Lib.DecodeBitMap;
import com.culture.inha.cultureline.R;

import java.util.ArrayList;

/**
 * Created by Jaeseok on 2018-07-08.
 */

public class MainBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private boolean isMoreLoading = true;

    private ArrayList<Question> mDataset;

    private OnItemClickListener mListener;
    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnItemClickListener {
        void onQuestionClick(Question question);

        void onProfileClick(Author author);
    }

    public MainBoardAdapter(OnItemClickListener listener, OnLoadMoreListener onLoadMoreListener) {
        mDataset = new ArrayList<>();
        mListener = listener;
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_mainboard, parent, false));
        } else {
            return new ProgessViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }
    }

    public void showLoading() {
        if (isMoreLoading && mDataset != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mDataset.add(null);
                    notifyItemInserted(mDataset.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (mDataset != null && mDataset.size() > 0) {
            mDataset.remove(mDataset.size() - 1);
            notifyItemRemoved(mDataset.size());
        }
    }

    public void changeItem(int position, Question question) {
        mDataset.set(position, question);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        if (mDataset != null && mDataset.size() > 0) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addAll(ArrayList<Question> dataSet) {
        mDataset.clear();
        mDataset.addAll(dataSet);
        notifyDataSetChanged();
    }

    public void addItemMore(Question question) {
        mDataset.add(0, question);
        notifyItemInserted(0);
    }

    public void addItemMore(ArrayList<Question> dataSet) {
        int sizeInit = mDataset.size();
        mDataset.addAll(dataSet);
        notifyItemRangeChanged(sizeInit, mDataset.size());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        if(mDataset.get(position).getAnswers().size() > 0)
//            holder.imgIsAnswer.setBackgroundResource(R.drawable.answer_on);
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).txtProfile.setText(mDataset.get(position).getAuthor().getName());
            ((ItemViewHolder) holder).txtCategory.setText(GlobalDataSet.getCategoryName(mDataset.get(position).getCategoryId()));
            ((ItemViewHolder) holder).txtTitle.setText(mDataset.get(position).getTitle());
            ((ItemViewHolder) holder).txtDate.setText(mDataset.get(position).getUpdatedAt());
            ((ItemViewHolder) holder).txtContents.setText(" " + mDataset.get(position).getContents());
            if (mDataset.get(position).getSelected() != 0) {
                ((ItemViewHolder) holder).imgIsAnswer.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(
                        ((ItemViewHolder) holder).itemView.getContext().getResources(),
                        R.drawable.answer_on, 85, 70));
                ((ItemViewHolder) holder).txtNumAnswer.setText(String.valueOf(mDataset.get(position).getAnswers().size()));
                ((ItemViewHolder) holder).txtNumAnswer.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).imgIsAnswer.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(
                        holder.itemView.getContext().getResources(),
                        R.drawable.answer_off, 85, 70));
                if (mDataset.get(position).getAnswers().size() > 0) {
                    ((ItemViewHolder) holder).txtNumAnswer.setText(String.valueOf(mDataset.get(position).getAnswers().size()));
                    ((ItemViewHolder) holder).txtNumAnswer.setVisibility(View.VISIBLE);
                } else {
                    ((ItemViewHolder) holder).txtNumAnswer.setText(String.valueOf(0));
                    ((ItemViewHolder) holder).txtNumAnswer.setVisibility(View.INVISIBLE);
                }
            }

            ((ItemViewHolder) holder).viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onProfileClick(mDataset.get(position).getAuthor());
                }
            });
            ((ItemViewHolder) holder).viewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onQuestionClick(mDataset.get(position));
                }
            });
            ((ItemViewHolder) holder).txtContents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onQuestionClick(mDataset.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        //public CircleImageView imgProfile;

        public LinearLayout viewTitle;
        public LinearLayout viewProfile;
        public AutoResizeTextView txtProfile;
        public TextView txtCategory;
        public AutoResizeTextView txtTitle;
        public AppCompatTextView txtDate;
        public AppCompatTextView txtContents;
        public AppCompatImageView imgIsAnswer;
        public AppCompatTextView txtNumAnswer;

        public ItemViewHolder(View view) {
            super(view);
            viewTitle = view.findViewById(R.id.viewTitle);
            viewProfile = view.findViewById(R.id.viewProfile);
//            imgProfile =  view.findViewById(R.id.imgProfile);
            txtProfile = view.findViewById(R.id.txtProfile);
            txtCategory = view.findViewById(R.id.txtCategory);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtDate = view.findViewById(R.id.txtDate);
            txtContents = view.findViewById(R.id.txtContent);
            imgIsAnswer = view.findViewById(R.id.imgIsAnswer);
            txtNumAnswer = view.findViewById(R.id.txtNumAnswer);
        }
    }

    public static class ProgessViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;

        public ProgessViewHolder(View itemView) {
            super(itemView);
            pBar = itemView.findViewById(R.id.pBar);
        }
    }
}
