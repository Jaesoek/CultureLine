package com.culture.inha.cultureline.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.R;
import com.culture.inha.cultureline.SignUpIn.LoginActivity;

public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "profile";

    private AppCompatButton btnSignOut;
    private AppCompatTextView txtName;
    private AppCompatTextView txtMajor;
    private AppCompatTextView txtPhone;
    private AppCompatTextView txtEmail;

    public ProfileFragment(){}

    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSharedPreferences(
                        "user",0).edit().remove("token").remove("isToken").remove("userId").apply();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        txtName = view.findViewById(R.id.txtName);
        txtMajor = view.findViewById(R.id.txtMajor);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);

        txtName.setText(GlobalDataSet.user.getName());
        txtMajor.setText(GlobalDataSet.user.getMajor());

        if(GlobalDataSet.user.getEmail() == null) txtEmail.setVisibility(View.GONE);
        else txtEmail.setText(GlobalDataSet.user.getEmail());
        txtPhone.setVisibility(View.GONE);

        return view;
    }
}
