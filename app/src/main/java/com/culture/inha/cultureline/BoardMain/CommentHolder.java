package com.culture.inha.cultureline.BoardMain;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.culture.inha.cultureline.R;

class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public AppCompatTextView txtCommentName;
    public AppCompatTextView txtCommentContent;
    public AppCompatTextView txtUpdateComment;
    public AppCompatTextView txtDeleteComment;
    public AppCompatTextView txtCommentDate;

    private String mToken;

    public CommentHolder(View itemView, String token) {
        super(itemView);
        txtCommentName = itemView.findViewById(R.id.txtCommentName);
        txtCommentContent = itemView.findViewById(R.id.txtCommentContent);
        txtUpdateComment = itemView.findViewById(R.id.txtUpdateComment);
        txtDeleteComment = itemView.findViewById(R.id.txtDeleteComment);
        txtCommentDate = itemView.findViewById(R.id.txtCommentDate);

        mToken = token;
    }

    @Override
    public void onClick(View view) {

    }
}
