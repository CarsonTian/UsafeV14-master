package com.example.greyson.test1.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by greyson on 5/5/17.
 */

public class MenuActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mLLEmergencyMenu;
    private LinearLayout mLLPanicButtonMenu;
    private LinearLayout mLLSafetyTrackMenu;
    private LinearLayout mLLSafetyMapMenu;
    private LinearLayout mLLSettingMenu;


    private static final int REQUEST_COARSE_LOCATION = 000;
    private static final int REQUEST_FINE_LOCATION = 001;
    private static final int REQUEST_SEND_SMS = 002;
    private static final int REQUEST_READ_CONTACT = 003;
    private static final int REQUEST_CALL_PHONE = 004;

    private static final int RESULT_PICK_CONTACT = 111;

    @Override
    protected int getLayoutRes() {
        return R.layout.act_menu;
    }

    @Override
    protected void initView() {
        mLLEmergencyMenu = (LinearLayout) findViewById(R.id.ll_emergencyCallMenu);
        mLLPanicButtonMenu = (LinearLayout) findViewById(R.id.ll_panicButtonMenu);
        mLLSafetyTrackMenu = (LinearLayout) findViewById(R.id.ll_startTrackMenu);
        mLLSafetyMapMenu = (LinearLayout) findViewById(R.id.ll_safetyMapMenu);
        mLLSettingMenu = (LinearLayout) findViewById(R.id.ll_settingMenu);
    }

    @Override
    protected void initData() {
        checkCoarseLocationPermission();
        checkFineLocationPermission();
        checkSMSPermission();
        checkReadContactPermission();
        checkCallPermission();
    }


    @Override
    protected void initEvent() {
        mLLEmergencyMenu.setOnClickListener(this);
        mLLPanicButtonMenu.setOnClickListener(this);
        mLLSafetyTrackMenu.setOnClickListener(this);
        mLLSafetyMapMenu.setOnClickListener(this);
        mLLSettingMenu.setOnClickListener(this);
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
            case R.id.ll_settingMenu:
                Intent intent1 = new Intent(MenuActivity.this, UserSettingActivity.class);
                startActivity(intent1);/////
                break;
        }
    }



    @Override
    protected void destroyView() {

    }

    private void showCheckDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure Call 000 ?")
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
                        intent0.setData(Uri.parse("tel:0123456"));
                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            checkCallPermission();
                            return;
                        }
                        startActivity(intent0);
                    }
                })
                .show();
    }

    private boolean checkCoarseLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                return false;
            }
        }
        return true;
    }

    private boolean checkFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                return false;
            }
        }
        return true;
    }

    private boolean checkSMSPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                return false;
            }
        }
        return true;
    }

    private boolean checkReadContactPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                return false;
            }
        }
        return true;
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
            case REQUEST_COARSE_LOCATION:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Safety Map function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
            case REQUEST_FINE_LOCATION:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Safety Map function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;

            case REQUEST_SEND_SMS:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Emergency Contact function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
            case REQUEST_READ_CONTACT:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Emergency Contact function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
            case REQUEST_CALL_PHONE:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Emergency Call function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }

}
