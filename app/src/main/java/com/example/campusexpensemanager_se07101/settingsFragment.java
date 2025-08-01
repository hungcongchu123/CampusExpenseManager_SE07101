package com.example.campusexpensemanager_se07101;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class settingsFragment extends Fragment {
    // dung de luu du lieu vao Bundle va chuyen tham so vao Fragment
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // 2 bien nay de chua gia tri thuc te duoc luu vao Fragmment
    private String mParam1;
    private String mParam2;
    public settingsFragment() {
        // moi fragment can co contructor rong di tool androi can khoi chay khi can
    }
    public static settingsFragment newInstance(String param1, String param2) {
        settingsFragment fragment = new settingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
        // tao mot fragment de truyen du lieu vao Bundle
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            // khi fragment duoc tao ma co du lieu Bundle thi lay ra gan vao 2 bien 1 -2
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
