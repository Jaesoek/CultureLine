package com.culture.inha.cultureline.QuestionPage;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.Lib.DecodeBitMap;
import com.culture.inha.cultureline.R;

import java.util.ArrayList;


public class MyQuestionAdapter extends RecyclerView.Adapter<MyQuestionAdapter.QuestionViewHolder> {
    private final String TAG = "MyQuestionAdapter";

    OnMyQuestionListener mListener;

    public interface OnMyQuestionListener {
        void onDetailClick(Question question);
        void onUpdateClick(Question question);
        void onDeleteClick(Question question);
    }

    private ArrayList<Question> mQuestionSet;

    public MyQuestionAdapter(OnMyQuestionListener myQuestionListener) {
        mQuestionSet = new ArrayList<>();
        mListener = myQuestionListener;
    }

    public void changeItem(int position, Question question) {
        mQuestionSet.set(position, question);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        if (mQuestionSet != null && mQuestionSet.size() > 0) {
            mQuestionSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addAll(ArrayList<Question> dataSet) {
        mQuestionSet.clear();
        mQuestionSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_myquestion, parent, false);
        QuestionViewHolder vh = new QuestionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        holder.mQuestion = mQuestionSet.get(position);
        holder.mListener = mListener;

        holder.txtTitle.setText(mQuestionSet.get(position).getTitle());
        holder.txtCategory.setText(mQuestionSet.get(position).getCategoryId());
        holder.txtContent.setText(mQuestionSet.get(position).getContents());

        if (mQuestionSet.get(position).getSelected() != 0) {
            holder.imgIsAnswer.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(
                    holder.itemView.getContext().getResources(),
                    R.drawable.answer_on, 85, 70));
        } else {
            holder.imgIsAnswer.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(
                    holder.itemView.getContext().getResources(),
                    R.drawable.answer_off, 85, 70));
        }


        holder.viewOption.setOnClickListener(holder);
        holder.viewAll.setOnClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return mQuestionSet.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String TAG = "MyQuestionAdapter";

        public AppCompatTextView txtTitle;
        public AppCompatTextView txtCategory;
        public AppCompatImageView imgIsAnswer;
        public RelativeLayout viewOption;
        public AppCompatTextView txtContent;
        public CardView viewAll;

        Question mQuestion;
        private OnMyQuestionListener mListener;

        QuestionViewHolder(View view) {
            super(view);
            viewAll = view.findViewById(R.id.viewAll);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtCategory = view.findViewById(R.id.txtCategory);
            imgIsAnswer = view.findViewById(R.id.imgIsAnswer);
            viewOption = view.findViewById(R.id.viewOption);
            txtContent = view.findViewById(R.id.txtContent);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.viewAll:
                    mListener.onDetailClick(mQuestion);
                    break;
                case R.id.viewOption:
                    PopupMenu popup = new PopupMenu(view.getContext(), viewOption);
                    popup.inflate(R.menu.menu_modify);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menuUpdate:
                                    mListener.onUpdateClick(mQuestion);
                                    return true;
                                case R.id.menuDelete:
                                    mListener.onDeleteClick(mQuestion);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                    break;
                default:
                    break;
            }

        }
    }
}
