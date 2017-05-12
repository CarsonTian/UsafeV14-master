package com.example.greyson.test1.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

/**
 * Created by greyson on 5/5/17.
 */

public class MenuActivity extends BaseActivity implements View.OnClickListener {

    private TextView mLLEmergencyMenu;
    private TextView mLLPanicButtonMenu;
    private TextView mLLSafetyTrackMenu;
    private TextView mLLSafetyMapMenu;
    private TextView mLLSettingMenu;
    private TextView logo;
    private TextView help;


    private static final int REQUEST_COARSE_LOCATION = 000;
    private static final int REQUEST_FINE_LOCATION = 001;
    private static final int REQUEST_SEND_SMS = 002;
    private static final int REQUEST_READ_CONTACT = 003;
    private static final int REQUEST_CALL_PHONE = 004;
    private static final int REQUEST_ALL_PERMISSION = 006;
    private static final int RESULT_PICK_CONTACT = 111;

    @Override
    protected int getLayoutRes() {
        return R.layout.act_menu;
    }

    @Override
    protected void initView() {

        mLLEmergencyMenu = (TextView) findViewById(R.id.ll_emergencyCallMenu);
        mLLPanicButtonMenu = (TextView) findViewById(R.id.ll_panicButtonMenu);
        mLLSafetyTrackMenu = (TextView) findViewById(R.id.ll_startTrackMenu);
        mLLSafetyMapMenu = (TextView) findViewById(R.id.ll_safetyMapMenu);
        mLLSettingMenu = (TextView) findViewById(R.id.tv_userSetting);
        logo = (TextView) findViewById(R.id.textlogo);
        help = (TextView) findViewById(R.id.tv_userHelp);

    }

    @Override
    protected void initData() {
        if(checkAllPermission()){}
        else {requestAllPermission();}
        checkEmergencyContact();

        //checkCoarseLocationPermission();
        //checkFineLocationPermission();
        //checkSMSPermission();
        //checkReadContactPermission();
        //checkCallPermission();
    }

    private void checkEmergencyContact() {
        if (checkEmergencyContactEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice!")
                    .setContentText("Add Emergency Contacts Make Yourself More Safe.")
                    .setConfirmText("Go to User Setting.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            Intent intent1 = new Intent(MenuActivity.this, UserSettingActivity.class);
                            startActivity(intent1);/////
                        }
                    })
                    .show();
        }
    }
    private boolean checkEmergencyContactEmpty() {
        SharedPreferences preferences = this.getSharedPreferences("UserSetting",MODE_PRIVATE);
        String userName = preferences.getString("userName",null);
        String contact1 = preferences.getString("contact1",null);
        String contact2 = preferences.getString("contact2",null);
        String contact3 = preferences.getString("contact3",null);
        if (contact1 == null && contact2 == null && contact3 == null) {
            new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("You need choose at least one emergency contact.")
                    .show();
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
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("You need choose at least one emergency contact.")
                    .show();
            return true;
        }
        return true;
    }

    private void requestAllPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        SEND_SMS,
                        CALL_PHONE
                }, REQUEST_ALL_PERMISSION);
    }

    private boolean checkAllPermission() {
            int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
            int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
            int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
            int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
            int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
            int SixthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
            return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    SixthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void initEvent() {
        mLLEmergencyMenu.setOnClickListener(this);
        mLLPanicButtonMenu.setOnClickListener(this);
        mLLSafetyTrackMenu.setOnClickListener(this);
        mLLSafetyMapMenu.setOnClickListener(this);
        mLLSettingMenu.setOnClickListener(this);
        logo.setOnClickListener(this);
        help.setOnClickListener(this);
        //checkEmergencyContact();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        switch (v.getId()) {
            case R.id.ll_emergencyCallMenu:
                showCheckDialog();
                break;
            case R.id.ll_panicButtonMenu:
                intent.putExtra("menu","button");
                startActivity(intent);
                break;
            case R.id.ll_startTrackMenu:
                intent.putExtra("menu","track");
                startActivity(intent);
                break;
            case R.id.ll_safetyMapMenu:
                intent.putExtra("menu","map");
                startActivity(intent);
                break;
            case R.id.tv_userSetting:
                Intent intent1 = new Intent(MenuActivity.this, UserSettingActivity.class);
                startActivity(intent1);/////
                break;
            case R.id.textlogo:
                aboutUs();
                break;
            case R.id.tv_userHelp:
                Intent intent2 = new Intent(MenuActivity.this, UserGuide.class);
                startActivity(intent2);
                break;
        }
    }



    @Override
    protected void destroyView() {

    }

    private void aboutUs() {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setTitleText("About Us")
                .setContentText("Team Name: HexTech?")
                .show();
    }

    private void showCheckDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Call 000 ?")
                .setCancelText("No")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent intent0 = new Intent(Intent.ACTION_CALL);
                        intent0.setData(Uri.parse("tel:0"));
                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            checkCallPermission();
                            return;
                        }
                        startActivity(intent0);
                    }
                })
                .show();
    }



    private boolean checkCallPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_ALL_PERMISSION:
                if (grantResults.length > 0) {

                    boolean CorseLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean FineLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContactPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadPhoneStatePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean SendSMSPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean CallPhonePermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (CorseLocationPermission && FineLocationPermission && ReadContactPermission
                            && ReadPhoneStatePermission && SendSMSPermission && CallPhonePermission) {
                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good")
                                .setContentText("All Permissions Granted Successfully.")
                                .show();

                    }
                    else {
                        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Notice")
                                .setContentText("Some Functions Rely These Permissions.")
                                .show();
                    }
                }
                break;
        }
    }
}
