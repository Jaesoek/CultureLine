package com.culture.inha.cultureline.Home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.culture.inha.cultureline.R;

public class FreeBoardFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "freeBoard";

    public FreeBoardFragment(){}

    public static FreeBoardFragment newInstance(int sectionNumber) {
        FreeBoardFragment fragment = new FreeBoardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_freeboard, container, false);
        return view;
    }
}
