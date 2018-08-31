package com.culture.inha.cultureline.BoardMain;

import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.DataSet.Comment;

import static com.culture.inha.cultureline.BoardMain.DetailBoardAdapter.ANSWER;
import static com.culture.inha.cultureline.BoardMain.DetailBoardAdapter.COMMENT;

public class ItemForDetail {
    private int type;
    private Answer answer;
    private Comment comment;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }


    public ItemForDetail(Answer answer) {
        type = ANSWER;
        this.answer = answer;
        this.comment = null;
    }

    public ItemForDetail(Comment comment) {
        type = COMMENT;
        this.answer = null;
        this.comment = comment;
    }
}
