package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.core.TimerListener;
import com.example.greyson.test1.ui.activity.MainActivity;
import com.example.greyson.test1.ui.activity.UserSettingActivity;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.example.greyson.test1.widget.CountDownView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * This class is button function
 *
 * @author Greyson, Carson
 * @version 1.0
 */


public class SafetyButtonFragment extends BaseFragment implements View.OnClickListener, android.location.LocationListener {
    private static final int RESULT_PICK_CONTACT = 111;
    private static final int REQUEST_SEND_SMS = 222;
    private static final int REQUEST_READ_CONTACT = 333;

    private LinearLayout mLLSetting;
    private LinearLayout mLLContact1;
    private LinearLayout mLLContact2;
    private LinearLayout mLLContact3;
    private LinearLayout mLLStartReset;

    private TextView mTVContactName1;
    private TextView mTVContactName2;
    private TextView mTVContactName3;
    private TextView mTVSettingButton;
    private TextView mTVStartButton;

    private CountDownView cdv;

    private SharedPreferences preferences;
    private boolean canSendMSM;
    private String phoneNumber;
    private String tiemrStatus;
    private boolean tipShow;

    private LocationManager locationManager;
    private String provider;
    private LocationProvider locationProvider;

    /**
     * Initial view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_safetybutton, container, false);
        mLLContact1 = (LinearLayout) view.findViewById(R.id.ll_sb_contact1);
        mLLContact2 = (LinearLayout) view.findViewById(R.id.ll_sb_contact2);
        mLLContact3 = (LinearLayout) view.findViewById(R.id.ll_sb_contact3);
        mLLSetting = (LinearLayout) view.findViewById(R.id.ll_sb_setting);
        mLLStartReset = (LinearLayout) view.findViewById(R.id.ll_sb_startReset);

        mTVSettingButton = (TextView) view.findViewById(R.id.tv_contactSetting);
        mTVStartButton = (TextView) view.findViewById(R.id.tv_startButton);
        mTVContactName1 = (TextView) view.findViewById(R.id.tv_contactName1);
        mTVContactName2 = (TextView) view.findViewById(R.id.tv_contactName2);
        mTVContactName3 = (TextView) view.findViewById(R.id.tv_contactName3);

        cdv = (CountDownView) view.findViewById(R.id.countdownview);
        cdv.setInitialTime(10000); // Initial time of 10 seconds.
        cdv.setListener(new TimerListener() {
            @Override
            public void timerElapsed() {
                cdv.stop();
                //checkSMSPermission();
                if (canSendMSM == true) {
                    sendMessageToContact();
                }
                canSendMSM = false;
                ///
            }
        });
        return view;
    }

    /**
     * Check permission of message
     */
    private boolean checkSMSPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
            return false;
        }
        return true;
    }

    private boolean checkReadContactPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                return false;
            }
        }
        return true;
    }

    /**
     * request permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTimer();
                }
            }
            break;
            case REQUEST_READ_CONTACT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    settingButton();
                }
            }
            break;
        }
    }

    /**
     * Save last location
     * @param name
     * @param phone
     * @return
     */
    private boolean saveLastContact(String name, String phone) {
        SharedPreferences preferences = mContext.getSharedPreferences("LastContact", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("contact", name + "," + phone);
        editor.commit();
        return true;
    }

    /**
     * This is method for sending message
     */
    private void sendMessageToContact() {
        //http://maps.google.com/maps?q=-37.886256,145.0543715

        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(SMS_DELIVERED), 0);


        BroadcastReceiver smsSent = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        //Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mContext.registerReceiver(smsSent, new IntentFilter(SMS_SENT));

        BroadcastReceiver smsDelivered = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mContext.registerReceiver(smsDelivered, new IntentFilter(SMS_DELIVERED));

        if (!checkEmergencyContactEmpty()) {
            SharedPreferences preferences = mContext.getSharedPreferences("LastLocation", MODE_PRIVATE);
            String lastLocation = preferences.getString("last location", null);
            if (lastLocation == null || lastLocation.isEmpty()) {
                lastLocation = getCurrentLocation();
            }
            String baseMapUrl = "http://maps.google.com/maps?q=";
            String eMessage = "This is an emergency message, please call me first, press this link to see my last location: "
                    + baseMapUrl + lastLocation;
            SmsManager smsManager = SmsManager.getDefault();

            List<String> ePhoneList = getPhoneList();
            Iterator<String> iterator = ePhoneList.iterator();
            while (iterator.hasNext()) {
                String phone = iterator.next();
                smsManager.sendTextMessage(phone, null, eMessage, sentPendingIntent, deliveredPendingIntent);
            }
            if (!checkDestroy()) {///////////////////////////////////////////
                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Message Sent Successfully.")
                        .setContentText(eMessage)
                        .show();
            }
        }
    }

    private boolean checkDestroy() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("destroy", MODE_PRIVATE);
        String destroy = sharedPreferences.getString("isDestroy", null);
        if (destroy == null || destroy.isEmpty()) {return false;}
        else if (destroy.equals("0")) {return false;}
        return true;
    }

    private List<String> getPhoneList() {
        List<String> phonelist = new ArrayList<>();
        preferences = mContext.getSharedPreferences("UserSetting", MODE_PRIVATE);
        String contact1 = preferences.getString("contact1", null);
        String contact2 = preferences.getString("contact2", null);
        String contact3 = preferences.getString("contact3", null);
        if (contact1 != null && !contact1.replace(";", " ").trim().isEmpty()) {
            phonelist.add(contact1.split(";")[1].trim());
        }
        if (contact2 != null && !contact2.replace(";", " ").trim().isEmpty()) {
            phonelist.add(contact2.split(";")[1].trim());
        }
        if (contact3 != null && !contact3.replace(";", " ").trim().isEmpty()) {
            phonelist.add(contact3.split(";")[1].trim());
        }
        return phonelist;
    }

    /**
     * Initial data
     */
    @Override
    protected void initData() {
        tipShow = true;
        canSendMSM = true;
        if (checkEmergencyContactEmpty()) {
            return;
        } else {
            loadEmergencyContact();
        }

        //mTVContactName.setText("Contact Name:" + lastContact.split(",")[0]);
        //mTVContactNumber.setText("Contact Number:" + lastContact.split(",")[1]);
    }

    @Override
    protected void initEvent() {
        mTVStartButton.setOnClickListener(this);
        mTVSettingButton.setOnClickListener(this);
    }

    /**
     * Change view listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        mTVSettingButton.setSelected(false);
        switch (v.getId()) {
            case R.id.tv_startButton:
                //mTVStartButton.setSelected(true);
                //mLLSettingButton.setSelected(false);
                //mLLCancelButton.setSelected(false);
                if (checkSMSPermission()) {
                    startTimer();
                    preferences = mContext.getSharedPreferences("dialog", MODE_PRIVATE);
                    String dialogShow = preferences.getString("buttonDialog", null);
                    if (dialogShow != null && dialogShow.equals("0")) {}
                    else {showInstructionDialog();}
                }
                break;
            case R.id.tv_contactSetting:
                mTVSettingButton.setSelected(true);
                //mLLStartButton.setSelected(false);
                //mLLCancelButton.setSelected(false);
                if (checkReadContactPermission()){
                    settingButton();
                }
                break;
        }
    }

    /**
     * reset time
     */
    private void resetTimer() {
        cdv.reset();
        canSendMSM = true;
    }

    private void startTimer() {
        if(!checkEmergencyContactEmpty() && checkSMSPermission()) {
            if (!mTVStartButton.isSelected()) {
                mTVStartButton.setBackgroundResource(R.drawable.buttonreset);
                mTVStartButton.setSelected(true);
                cdv.start();
                startNotification();
            } else if (mTVStartButton.isSelected()) {
                mTVStartButton.setBackgroundResource(R.drawable.buttonactivate);
                mTVStartButton.setSelected(false);
                resetTimer();
            }
        }
    }

    private void showInstructionDialog() {
        preferences = mContext.getSharedPreferences("dialog", MODE_PRIVATE);

        cdv.stop();
        new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Tips")
                .setContentText("You can also Start or Reset timer in the Notification without Unlock Screen.")
                .setCancelText("Don't ask me again")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        tipShow = false;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("buttonDialog", "0");
                        editor.commit();
                        if (mTVStartButton.isSelected()) {
                            cdv.start();
                        }
                    }
                })
                .setConfirmText("Continue")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        if (mTVStartButton.isSelected()) {
                            cdv.start();
                        }
                    }
                })
                .show();

    }

    /**
     * This is to set notification button
     */
    private void startNotification() {
        String i1 = Long.toString(System.currentTimeMillis()) + "qwe";
        String i2 = Long.toString(System.currentTimeMillis()) + "asd";
        String i3 = Long.toString(System.currentTimeMillis()) + "zxc";

        SharedPreferences preferences = mContext.getSharedPreferences("notification", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("i1", i1);
        editor.putString("i2", i2);
        editor.putString("i3", i3);
        editor.commit();

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("notification",i1);
        intent.setAction("qwe");
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent1 = new Intent(mContext, MainActivity.class);
        intent1.putExtra("notification",i2);
        intent1.setAction("asd");
        PendingIntent pIntent1 = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(mContext, MainActivity.class);
        intent2.putExtra("notification",i3);
        intent2.setAction("zxc");
        PendingIntent pIntent2 = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(R.drawable.ic_alarm_on_black_24dp,"Start Timer",pIntent1).build();

        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(R.drawable.ic_alarm_off_black_24dp,"Reset Timer",pIntent2).build();

        Notification n  = new NotificationCompat.Builder(mContext)
                .setContentTitle("Secure-Trip")
                .setContentText("Click to enter application")
                .setSmallIcon(R.drawable.ic_security_black_24dp)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setPriority(2)
                .setTicker("Timer Start")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .addAction(action1)
                .addAction(action2)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify("usafe",666,n);
    }

    private boolean checkNotificaionExsist() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] s = notificationManager.getActiveNotifications();///Min 23 API
        int nLength = s.length;
        for (int i = 0;i< nLength; i++) {
            if(s[i].getTag().equals("usafe"))
                return true;
        }
        return false;
    }

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

    private String getCurrentLocation() {
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
        saveLastLocationToSharedPreference(lat + "," + lng);
        return lat + "," + lng;
    }

    private void saveLastLocationToSharedPreference(String latitude) {
        String lat = latitude.split(",")[0];
        String lng = latitude.split(",")[1];
        SharedPreferences preferences1 = mContext.getSharedPreferences("LastLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences1.edit();
        editor.putString("last location", lat + "," + lng);
        editor.commit();
    }


    /**
     * Setting button
     */
    private void settingButton() {
        Intent contactPickerIntent = new Intent(mContext, UserSettingActivity.class);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    clearEmergencyContact();
                    loadEmergencyContact();
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private boolean checkEmergencyContactEmpty() {
        preferences = mContext.getSharedPreferences("UserSetting",MODE_PRIVATE);
        String contact1 = preferences.getString("contact1",null);
        String contact2 = preferences.getString("contact2",null);
        String contact3 = preferences.getString("contact3",null);


        if (contact1 == null && contact2 == null && contact3 == null) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Warning!")
                    .setContentText("You need choose at least one emergency contact.")
                    .show();
            canSendMSM = false;
            return true;
        } else if (contact1 != null) {
            if (!contact1.replace(";", " ").trim().isEmpty()) {
                return false;
            }
        } else if (contact2 != null) {
            if (!contact2.replace(";", " ").trim().isEmpty()) {
                return false;
            }
        } else if (contact3 != null) {
            if (!contact3.replace(";", " ").trim().isEmpty()) {
                return false;
            }
        } else {
            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("You need choose at least one emergency contact.")
                    .show();
            canSendMSM = false;
            return true;
        }

        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error!")
                .setContentText("You need choose at least one emergency contact.")
                .show();
        canSendMSM = false;
        return true;
    }

    private void clearEmergencyContact() {
        mTVContactName1.setText("");
        mTVContactName2.setText("");
        mTVContactName3.setText("");
    }

    private void loadEmergencyContact() {
        preferences = mContext.getSharedPreferences("UserSetting",MODE_PRIVATE);
        String contact1 = preferences.getString("contact1",null);
        String contact2 = preferences.getString("contact2",null);
        String contact3 = preferences.getString("contact3",null);
        if (contact1 != null && !contact1.replace(";"," ").trim().isEmpty()) {
            mTVContactName1.setText(contact1.split(";")[0]);
        }
        if (contact2 != null && !contact2.replace(";"," ").trim().isEmpty()) {
            mTVContactName2.setText(contact2.split(";")[0]);
        }
        if (contact3 != null && !contact3.replace(";"," ").trim().isEmpty()) {
            mTVContactName3.setText(contact3.split(";")[0]);
        }
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle b = getArguments();
        if (b != null) {
            int extra = b.getInt("notification");
            switch (extra) {
                case 1:
                    startTimer();
                    getArguments().clear();
                    break;
                case 2:
                    resetTimer();
                    getArguments().clear();
                    break;
            }
        }
    }
}
