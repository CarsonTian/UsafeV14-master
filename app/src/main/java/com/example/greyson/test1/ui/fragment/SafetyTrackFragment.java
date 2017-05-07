package com.example.greyson.test1.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.entity.TrackerRes;
import com.example.greyson.test1.ui.activity.TrackerActivity;
import com.example.greyson.test1.ui.base.BaseFragment;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

/**
 * Tis class is about tracker function
 * @author Greyson, Carson
 * @version 1.0
 */
public class SafetyTrackFragment extends BaseFragment{

    /**
     * This method is used to initialize the map view and request the current location
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_safetytrack, container, false);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void destroyView() {

    }
}
