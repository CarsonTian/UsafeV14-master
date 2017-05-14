package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.greyson.test1.R;
import com.example.greyson.test1.core.TimerListener;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.example.greyson.test1.widget.CountDownView2;

import java.text.SimpleDateFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;


/**
 * This class is the method which trail tracker.
 * System can remind user regularly or at the end of travel to check if user is safe.
 * If user is not safe, server will send message to user's friend who set as emergence contact by user
 * @author Greyson, Carson
 * @version 1.0
 */
public class SafetyTrackFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private static final int REQUEST_GET_DEVICEID = 222;
    private String id, tStamp, cusTime, number,cLatitude, cLngtitude;
    private Button buttonStartTime, buttonStopTime;
    private EditText edtTimerValue;
    private TextView durTitle;
    private WebView upWeb;
    private LinearLayout time0;
    private LinearLayout time1;
    private long totalTimeCountInMilliseconds;
    private Runnable wTimer;
    private Handler mHandler;
    private MediaPlayer mp;
    private CountDownView2 cdv;                  // Count down timer service
    private SharedPreferences preferences;       // Receive data from other fragments
    private SweetAlertDialog sweetAlertDialog;
    private String saveTime="";                  // The state data of timer
    private boolean modeState = true;
    private boolean tipShow;

    /**
     * This method will be called when this fragment created.
     * It is used to initial all elements.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_safetytrack, container, false);

        mHandler = new Handler();                                                                        // The timer of sending message
        durTitle = (TextView) view.findViewById(R.id.durTitle);
        upWeb = (WebView) view.findViewById(R.id.upWeb);
        edtTimerValue = (EditText) view.findViewById(R.id.edtTimerValue);
        buttonStartTime = (Button) view.findViewById(R.id.btnStartTime);
        buttonStopTime = (Button) view.findViewById(R.id.btnStopTime);
        time0 = (LinearLayout) view.findViewById(R.id.time0);
        time1 = (LinearLayout) view.findViewById(R.id.time1);

        buttonStartTime.setOnClickListener(this);
        buttonStopTime.setOnClickListener(this);

        // Spinner setting
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner modeSpinner = (Spinner) view.findViewById(R.id.mode_spinner);
        modeSpinner.setAdapter(adapter);
        modeSpinner.setOnItemSelectedListener(this);

        cdv = (CountDownView2) view.findViewById(R.id.countdownview2);                                    // Count down timer

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timeResume", MODE_PRIVATE);  // Safe the data when fragment is destroyed
        edtTimerValue.setText(sharedPreferences.getString("time",""));                                    // Display the data saved

        // Check if there is saved data from last timer to judge if a timer need to be started
        if (!sharedPreferences.getString("time", "").trim().equals("")) {
            saveTime = edtTimerValue.getText().toString().trim();
            buttonStartTime.setVisibility(View.GONE);
            buttonStopTime.setVisibility(View.VISIBLE);
            edtTimerValue.setVisibility(View.GONE);
            startTimer();
            cdv.start();
            time0.setVisibility(View.GONE);
            time1.setVisibility(View.VISIBLE);
            tStamp = sharedPreferences.getString("tId", "");
        }

        // Sending message timer
        wTimer = new Runnable() {
            @Override
            public void run() {
                warningDialog();
            }
        };
        return view;
    }

    /**
     * This method is used to initial data from other fragments
     */
    @Override
    protected void initData() {
        tipShow = true;
        getCurrentLocation();               // Get current location                   
        if (checkDeviceIDPermission()) {    // Check permission of getting state of phone
            getMobileIMEI();                // Get IMEI and number of phone                      
        }
        preferences = mContext.getSharedPreferences("UserSetting",MODE_PRIVATE);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void destroyView() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timeResume", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time", saveTime);
        editor.putString("tId", tStamp);
        editor.commit();
    }

    /**
     * request permission
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GET_DEVICEID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMobileIMEI();
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnStartTime) {
            if (setNamCan()) {
                if (setTimer()) {
                    if (!number.trim().equals("")) {
                        saveTime = edtTimerValue.getText().toString().trim();
                        buttonStartTime.setVisibility(View.GONE);
                        buttonStopTime.setVisibility(View.VISIBLE);
                        edtTimerValue.setVisibility(View.GONE);
                        startUpload();
                        startTimer();
                        cdv.start();
                        time0.setVisibility(View.GONE);
                        time1.setVisibility(View.VISIBLE);
                    } else {
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Notice")
                                .setContentText(String.valueOf(R.string.no_phone_number))
                                .show();
                    }
                }
            } else {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Notice")
                        .setContentText("Please Complete User Setting Before You Start The Trail Tracker.")
                        .setConfirmText("OK")
                        .show();
            }
        } else if (v.getId() == R.id.btnStopTime) {
            saveTime = "";
            buttonStartTime.setVisibility(View.VISIBLE);
            buttonStopTime.setVisibility(View.GONE);
            edtTimerValue.setVisibility(View.VISIBLE);
            edtTimerValue.setText("");
            finishUpload();
            mHandler.removeCallbacks(wTimer);
            cdv.reset();
            time0.setVisibility(View.VISIBLE);
            time1.setVisibility(View.GONE);
        }
    }

    private void checkMediaPlayerPermission() {
        mp = new MediaPlayer();
            try {
                mp.setDataSource(mContext, RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                mp.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private boolean checkDeviceIDPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_GET_DEVICEID);
            return false;
        }
        return true;
    }

    private void getMobileIMEI() {
        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        id = tManager.getDeviceId();
        number = tManager.getLine1Number();

    }

    private void getCurrentLocation() {
        SharedPreferences preferences1 = mContext.getSharedPreferences("LastLocation", MODE_PRIVATE);
        String[] array = preferences1.getString("last location", "0,0").split(",");
        cLatitude = array[0];
        cLngtitude = array[1];
    }

    private boolean setNamCan() {
        if (preferences.getString("contact1", "").trim().equals("") ||  preferences.getString("userName", "").trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean setTimer() {
        if (edtTimerValue.getText().toString().trim().equals("")) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Please Enter a Time")
                    .setConfirmText("OK")
                    .show();
            return false;
        } else if (Integer.parseInt(edtTimerValue.getText().toString().trim()) < 1 ){
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Please make sure time is longer than 5 min.")
                    .setConfirmText("OK")
                    .show();
            return false;
        } else if (Integer.parseInt(edtTimerValue.getText().toString().trim()) > 30) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Please make sure time is shorter than 30 min")
                    .setConfirmText("OK")
                    .show();
            return false;
        } else {
            totalTimeCountInMilliseconds = 60 * Integer.parseInt(edtTimerValue.getText().toString().trim()) * 1000;
            cusTime = edtTimerValue.getText().toString().trim();
            return true;
        }
    }

    private void startTimer() {
        cdv.setInitialTime(totalTimeCountInMilliseconds); // Initial time of 5 seconds.
        cdv.setListener(new TimerListener() {
            @Override
            public void timerElapsed() {
                cdv.stop();
                dialog();
                checkMediaPlayerPermission();
                mp.start();
                mHandler.postDelayed(wTimer, 60000);
            }
        });
    }

    private void dialog() {
        sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Alarm")
                .setContentText("Are you safe? you have 1 min to confirm");
        if (modeState) {
            sweetAlertDialog.setConfirmText("Finish");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    mp.stop();
                    mHandler.removeCallbacks(wTimer);
                    finishUpload();
                    sweetAlertDialog.dismiss();
                }
            });
        } else {
            sweetAlertDialog.setConfirmText("Continue");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    cdv.setInitialTime(totalTimeCountInMilliseconds);
                    cdv.start();
                    mHandler.removeCallbacks(wTimer);
                    mp.stop();
                    uploadData();
                    sweetAlertDialog.dismiss();
                }
            });
        }

        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
    }

    private void warningDialog() {
        SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Alarm")
                .setContentText("We have sent warning messages, please contact " + preferences.getString("contact1", "").trim().split(";")[1] + ", " + preferences.getString("contact2", "").trim().split(";")[1] + ", " + preferences.getString("contact3", "").trim().split(";")[1])
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog1) {
                        sweetAlertDialog1.dismiss();
                        sweetAlertDialog.dismiss();
                        saveTime = "";
                        buttonStartTime.setVisibility(View.VISIBLE);
                        buttonStopTime.setVisibility(View.GONE);
                        edtTimerValue.setVisibility(View.VISIBLE);
                        edtTimerValue.setText("");
                        mHandler.removeCallbacks(wTimer);
                        cdv.reset();
                    }
                });

        sweetAlertDialog1.setCanceledOnTouchOutside(false);
        sweetAlertDialog1.show();
    }

    private void timeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = Long.valueOf(System.currentTimeMillis());
        tStamp = format.format(time);
    }

    private void uploadData() {
        upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/update/?deviceid=" + id + tStamp + "&status=safe&lat=" + cLatitude + "&lng=" + cLngtitude);
    }

    private void startUpload() {
        timeStamp();
        upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/create/?deviceid=" + id + tStamp + "&name=" + preferences.getString("userName" , "") + "&uphone=" + number + "&c1=" + preferences.getString("contact1", "").trim().split(";")[1] + "&c2=" + preferences.getString("contact2", "").trim().split(";")[1] + "&c3=" + preferences.getString("contact3", "").trim().split(";")[1] + "&status=start&period=" + cusTime + "&lat=" + cLatitude + "&lng=" + cLngtitude);
    }

    private void finishUpload() {
        upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/update/?deviceid=" + id + tStamp + "&status=reached&lat=" + cLatitude + "&lng=" + cLngtitude);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getSelectedItemPosition()) {
            case 0:
                durTitle.setText(R.string.one_trip);
                modeState = true;
                break;
            case 1:
                durTitle.setText(R.string.period);
                modeState = false;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onStart() {
        super.onStart();
        new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Tips")
                .setContentText("Two Mode.")
                .setCancelText("Don't ask me again")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        tipShow = false;
                    }
                })
                .setConfirmText("Continue")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
