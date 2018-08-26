package com.culture.inha.cultureline.Account;

import android.os.Bundle;
import android.support.v4.widget.AutoSizeableTextView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.culture.inha.cultureline.BaseActivity;
import com.culture.inha.cultureline.DataSet.Author;
import com.culture.inha.cultureline.Lib.AutoResizeTextView;
import com.culture.inha.cultureline.R;

public class ProfileActivity extends BaseActivity {

    AutoResizeTextView txtName;
    AutoResizeTextView txtPhone;
    AutoResizeTextView txtMajor;
    LinearLayout viewCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Author author = (Author) getIntent().getSerializableExtra("author");

        txtName = findViewById(R.id.txtName);
        txtMajor = findViewById(R.id.txtMajor);
        txtPhone = findViewById(R.id.txtPhone);
        viewCancel = findViewById(R.id.viewCancel);

        txtName.setText("이름: "+author.getName());
        txtPhone.setText("이메일: "+author.getEmail());
        txtMajor.setText("전공: "+author.getEmail());

        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
