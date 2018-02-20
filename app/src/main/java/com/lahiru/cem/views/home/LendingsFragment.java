package com.lahiru.cem.views.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lahiru.cem.R;


public class LendingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.i("TEST", "onCreateView method of lendings");
        return inflater.inflate(R.layout.fragment_lendings, container, false);
    }

}
