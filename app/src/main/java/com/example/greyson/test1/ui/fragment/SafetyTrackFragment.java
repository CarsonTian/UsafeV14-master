package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
public class SafetyTrackFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
    private SharedPreferences preferences1;
    private SharedPreferences preferences2;       // Receive data from other fragments
    private SweetAlertDialog sweetAlertDialog;
    private String saveTime="";                  // The state data of timer
    private boolean modeState = true;
    private boolean tipShow;
    private LocationManager locationManager;
    private String provider;

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
        tipShow = true;
        return view;
    }

    /**
     * This method is used to initial data from other fragments
     */
    @Override
    protected void initData() {
        getCurrentLocation();               // Get current location
        if (checkDeviceIDPermission()) {    // Check permission of getting state of phone
            getMobileIMEI();                // Get IMEI and number of phone                      
        }
        preferences2 = mContext.getSharedPreferences("UserSetting",MODE_PRIVATE);
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
                        helpDialog();
                    } else {
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Notice")
                                .setContentText(getResources().getString(R.string.no_phone_number))
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
        String[] array;
        SharedPreferences preferences = mContext.getSharedPreferences("LastLocation", MODE_PRIVATE);
        String lastLocation = preferences.getString("last location", null);
        if (lastLocation == null || lastLocation.isEmpty()) {
            array = getCurrentLocationFromGPS().split(",");
        } else {
            array = preferences.getString("last location", "").split(",");
        }
        cLatitude = array[0];
        cLngtitude = array[1];
    }

    private String getCurrentLocationFromGPS() {
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);////
        provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
        Location location = locationManager.getLastKnownLocation(provider);
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        return lat + "," + lng;
    }


    private boolean setNamCan() {
        if (preferences2.getString("contact1", "").trim().equals("") ||  preferences2.getString("userName", "").trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean setTimer() {
        if (edtTimerValue.getText().toString().trim().equals("")) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Please set time")
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
        }
        if (modeState) {
                totalTimeCountInMilliseconds = 60 * Integer.parseInt(edtTimerValue.getText().toString().trim()) * 1000;
                cusTime = edtTimerValue.getText().toString().trim();
                return true;
        } else {
            if (Integer.parseInt(edtTimerValue.getText().toString().trim()) > 30) {
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
                    buttonStopTime.callOnClick();
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

    private void helpDialog() {
        SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Alarm")
                .setContentText("The timer has started. Please remember to confirm safe when the safety reminder shows up. Otherwise, we will send a message to your contact.");
        sweetAlertDialog1.show();
    }

    private void warningDialog() {
        String name;
        if (preferences2.getString("contact2", "").equals(";")) {
            name = preferences2.getString("contact1", "").trim().split(";")[1];
        } else if (preferences2.getString("contact3", "").equals(";")) {
            name = preferences2.getString("contact1", "").trim().split(";")[1] + ", " + preferences2.getString("contact2", "").trim().split(";")[1];
        } else {
            name = preferences2.getString("contact1", "").trim().split(";")[1] + ", " + preferences2.getString("contact2", "").trim().split(";")[1] + ", " + preferences2.getString("contact3", "").trim().split(";")[1];
        }
        SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Alarm")
                .setContentText("We have sent warning messages, please contact " + name)
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
        if (preferences2.getString("contact2", "").equals(";")) {
            upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/create/?deviceid=" + id + tStamp + "&name=" + preferences2.getString("userName" , "") + "&uphone=" + number + "&c1=" + preferences2.getString("contact1", "").trim().split(";")[1] + "&c2=&c3=&status=start&period=" + cusTime + "&lat=" + cLatitude + "&lng=" + cLngtitude);
        } else if (preferences2.getString("contact3", "").equals(";")) {
            upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/create/?deviceid=" + id + tStamp + "&name=" + preferences2.getString("userName" , "") + "&uphone=" + number + "&c1=" + preferences2.getString("contact1", "").trim().split(";")[1] + "&c2=" + preferences2.getString("contact2", "").trim().split(";")[1] + "&c3=&status=start&period=" + cusTime + "&lat=" + cLatitude + "&lng=" + cLngtitude);
        } else {
            upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/create/?deviceid=" + id + tStamp + "&name=" + preferences2.getString("userName" , "") + "&uphone=" + number + "&c1=" + preferences2.getString("contact1", "").trim().split(";")[1] + "&c2=" + preferences2.getString("contact2", "").trim().split(";")[1] + "&c3=" + preferences2.getString("contact3", "").trim().split(";")[1] + "&status=start&period=" + cusTime + "&lat=" + cLatitude + "&lng=" + cLngtitude);
        }
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
                edtTimerValue.setHint("(5-30)min");
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
        preferences1 = mContext.getSharedPreferences("dialog", MODE_PRIVATE);
        String dialogShow = preferences1.getString("trackerDialog",null);
        if (dialogShow != null && dialogShow.equals("0")) {
        } else {
            showInstruction();
        }
    }

    private void showInstruction(){
        new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Tips")
                .setContentText("Safety reminder will only show up after the time period.Safety reminder will show up every time period you preset.")
                .setCancelText("Don't ask me again")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        SharedPreferences.Editor editor = preferences1.edit();
                        editor.putString("trackerDialog","0");
                        editor.commit();
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

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putBoolean("s", tipShow);
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        if(bundle != null){
            tipShow = bundle.getBoolean("s");
        }
    }


}
