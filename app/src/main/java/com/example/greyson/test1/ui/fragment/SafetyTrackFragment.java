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
import com.example.greyson.test1.net.WSNetService3;
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

    private Button okButton;
    private EditText desInput;
    private EditText cusTime;
    private Spinner timSelection;
    private LinearLayout cusSetting;
    private TextView box;

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

        okButton = (Button) view.findViewById(R.id.okButton);
        desInput = (EditText) view.findViewById(R.id.desInput);
        cusTime = (EditText) view.findViewById(R.id.cusTime);
        timSelection = (Spinner) view.findViewById(R.id.timSelection);
        cusSetting = (LinearLayout) view.findViewById(R.id.cusSetting);
        box = (TextView) view.findViewById(R.id.box);

        // Tis is spinner listener
        timSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] languages = getResources().getStringArray(R.array.time_selection);
                if (languages[position].toString().equals("At the end")) {
                    cusSetting.setVisibility(View.INVISIBLE);
                }
                if (languages[position].equals("customize")) {
                    cusSetting.setVisibility(VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // This is ok button function
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDuration();
                String destination = desInput.getText().toString().trim();
                String time = box.getText().toString();
                //String time = "no time";
                String notice = cusTime.getText().toString().trim();
                if (destination.equals("")) {
                    destination = "no destination";
                    time = "no duration";
                }
                if (notice.equals("")) {
                    Toast.makeText(mContext,"please fill out notice time",Toast.LENGTH_SHORT).show();
                } else {
                    passTravelSetting(destination, time, notice);
                }
            }
        });
        return view;
    }

    protected void getDuration() {
        Map<String, String> params = new HashMap<>();
        //String ori = "";
        String ori = getCurrentLocation();
        String des = String.valueOf(desInput.getText().toString());
        params.put("origin", ori);
        params.put("destination", des);
        params.put("mode", "walking");
        params.put("key", "AIzaSyAYPtaZmfpFdvNd3_-ur4X2Bvn-35uVoAQ");

        mRetrofit3.create(WSNetService3.class)
                .getDuration(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<TrackerRes>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrackerRes>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TrackerRes tracker) {
                        try {
                                for (TrackerRes.RoutesBean rRes : tracker.getRoutes()) {
                                    for (TrackerRes.RoutesBean.LegsBean lRes : rRes.getLegs()) {
                                        TrackerRes.RoutesBean.LegsBean.DurationBean dRes = lRes.getDuration();
                                        box.setText(dRes.getText().toString().toString());
                                    }
                                }
                            } catch (Exception e) {}
                    }
                });
    }

    private String getCurrentLocation() {
        SharedPreferences preferences1 = mContext.getSharedPreferences("LastLocation",MODE_PRIVATE);
        String currentLocation = preferences1.getString("last location","0,0");
        return currentLocation;
    }

    /**
     * This method is to send setting of tracker
     * @param a
     * @param b
     * @param c
     */
    private void passTravelSetting(String a, String b, String c) {
        Intent intent = new Intent();
        intent.setClass(mContext, TrackerActivity.class);
        intent.putExtra("Des", a);
        intent.putExtra("Tim", b);
        intent.putExtra("Not", c);
        startActivityForResult(intent,1);
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
