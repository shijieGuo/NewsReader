package com.example.guoshijie.newsreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment2 extends Fragment {

    private View layoutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(layoutView != null){
            return layoutView; }
        layoutView = inflater.inflate(R.layout.fragment2, null);
        return layoutView;
    }
}

