package com.example.greyson.test1.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.greyson.test1.R;
import com.example.greyson.test1.config.Constants;
import com.example.greyson.test1.ui.base.BaseActivity;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.example.greyson.test1.ui.fragment.SafetyButtonFragment;
import com.example.greyson.test1.ui.fragment.SafetyMapFragment;
import com.example.greyson.test1.ui.fragment.SafetyMoreFragment;
import com.example.greyson.test1.ui.fragment.SafetyTrackFragment;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by greyson on 22/3/17.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mLLSafetyMap;
    private LinearLayout mLLSafetyButton;
    private LinearLayout mLLSafetyTrack;
    private LinearLayout mLLSafetyMore;
    private Toolbar toolbar;
    private FragmentManager mFragmentManager;

    private List<BaseFragment> mFragments = new ArrayList<>();
    private SafetyMapFragment mSafetyMapFragment;
    private SafetyButtonFragment mSafetyButtonFragment;
    private SafetyTrackFragment mSafetyTrackFragment;
    private SafetyMoreFragment mSafetyMoreFragment;

    private static final int REQUEST_FINE_LOCATION = 001;
    private static final int REQUEST_CALL_PHONE = 004;
    private static final int REQUEST_GET_DEVICEID = 007;
    private int mCurrentIndex;

    @Override
    protected int getLayoutRes() {
        return R.layout.act_main;
    }

    @Override
    protected void initView() {
        toolbar = findView(R.id.toolbar);
        AppCompatTextView tvTitle = findView(R.id.tv_title);
        tvTitle.setText(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FrameLayout flMain = findView(R.id.fl_main);
        mLLSafetyMap = findView(R.id.ll_safetymap);
        mLLSafetyButton = findView(R.id.ll_safetybutton);
        mLLSafetyTrack = findView(R.id.ll_safetytrack);
        mLLSafetyMore = findView(R.id.ll_safetymore);
    }

    @Override
    protected void initData() {
        mFragmentManager = getSupportFragmentManager();

        mSafetyMapFragment = new SafetyMapFragment();
        mSafetyButtonFragment = new SafetyButtonFragment();
        mSafetyTrackFragment = new SafetyTrackFragment();
        mSafetyMoreFragment = new SafetyMoreFragment();

        mFragments.add(mSafetyMapFragment);
        mFragments.add(mSafetyTrackFragment);
        mFragments.add(mSafetyMoreFragment);
        mFragments.add(mSafetyButtonFragment);

        checkMenuIntent();
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_SELECT_FRAG_BUTTON);
        filter.addAction(Constants.INTENT_ACTION_USER_LOGIN);
        filter.addAction(Constants.INTENT_ACTION_USER_LOGOUT);

        SharedPreferences sharedPreferences = this.getSharedPreferences("destroy", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("isDestroy", "0");
        editor.commit();

        Intent intent = getIntent();
        String extra = intent.getAction();
        if (extra != null && extra.equals("qwe")) {
            setSafetyMapNotificationArg(1);
        }
    }

    private void checkMenuIntent () {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String str = bundle.getString("menu");
        if (str == null) {
            str = "button";
        }
        switch (str) {
            case "map":
                toolbar.setBackgroundColor(getResources().getColor(R.color.mapMenuBg));
                mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyMapFragment, "0").commitAllowingStateLoss();
                mCurrentIndex = 0;
                mLLSafetyMap.setSelected(true);
                break;
            case "button":
                toolbar.setBackgroundColor(getResources().getColor(R.color.buttonMenuBg));
                mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyButtonFragment, "3").commitAllowingStateLoss();
                mCurrentIndex = 3;
                mLLSafetyMap.setSelected(true);
                break;
            case "track":
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyTrackFragment, "1").commitAllowingStateLoss();
                mCurrentIndex = 1;
                mLLSafetyMap.setSelected(true);
                break;
        }
    }


    @Override
    protected void initEvent() {
        mLLSafetyMap.setOnClickListener(this);
        mLLSafetyButton.setOnClickListener(this);
        mLLSafetyTrack.setOnClickListener(this);
        mLLSafetyMore.setOnClickListener(this);
    }

    @Override
    protected void destroyView() {
        SharedPreferences preferences = this.getSharedPreferences("actID", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("actID", "0");
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        int index = 0;
        mLLSafetyMap.setSelected(false);
        mLLSafetyButton.setSelected(false);
        mLLSafetyTrack.setSelected(false);
        mLLSafetyMore.setSelected(false);
        switch (v.getId()) {
            case R.id.ll_safetymap:
                if (!checkMapPermission()) {return;}
                index = 0;
                toolbar.setBackgroundColor(getResources().getColor(R.color.mapMenuBg));
                mLLSafetyMap.setSelected(true);
                break;
            case R.id.ll_safetybutton:
                index = 3;
                toolbar.setBackgroundColor(getResources().getColor(R.color.buttonMenuBg));
                mLLSafetyButton.setSelected(true);
                break;
            case R.id.ll_safetytrack:
                if (!checkReadPhoneStatePermission()) {return;}
                index = 1;
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mLLSafetyTrack.setSelected(true);
                break;
            case R.id.ll_safetymore:
                index = mCurrentIndex;
                if (checkCallPermission()) {
                    showCheckDialog();
                }
                break;
        }
        if (index == mCurrentIndex) {
            return;
        }
        BaseFragment baseFragment = mFragments.get(index);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (baseFragment.isAdded()) {
            fragmentTransaction.show(baseFragment);
        } else {
            fragmentTransaction.add(R.id.fl_main, baseFragment, index + "");
            fragmentTransaction.show(baseFragment);
        }
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.hide(mFragments.get(mCurrentIndex));
        fragmentTransaction.commitAllowingStateLoss();
        mCurrentIndex = index;
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
                        intent0.setData(Uri.parse("tel:000"));
                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            checkCallPermission();
                            return;
                        }
                        //startActivity(intent0);
                    }
                })
                .show();
    }

    private boolean checkCallPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
            return false;
        }
        return true;
    }

    private boolean checkMapPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }
        return true;
    }

    private boolean checkReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_STATE}, REQUEST_GET_DEVICEID);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String extra = intent.getAction();
        if (extra == null) {
            onClick(mLLSafetyButton);
        } else if (extra.equals("qwe")) {
            setSafetyMapNotificationArg(1);
        }
    }

    private void tesmt(Intent intent) {
        SharedPreferences preferences = getSharedPreferences("notification", MODE_PRIVATE);
        String i1 = preferences.getString("i1",null);
        String i2 = preferences.getString("i2",null);
        String i3 = preferences.getString("i3",null);

        Bundle b = intent.getExtras();
        String extra = b.getString("notification");
        if (extra == null) {
            onClick(mLLSafetyButton);
        } else if (extra.equals(i1)) {
            onClick(mLLSafetyButton);
        } else if (extra.equals(i2)) {
            setSafetyMapNotificationArg(1);
        } else if (extra.equals(i3)) {
            setSafetyMapNotificationArg(2);
        }
    }


    private void setSafetyMapNotificationArg(int index) {
        Bundle args = new Bundle();
        args.putInt("notification", index);
        SafetyButtonFragment newSafeButtonFragment = new SafetyButtonFragment();
        newSafeButtonFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mFragments.get(mCurrentIndex));
        fragmentTransaction.remove(mFragments.get(3));
        mFragments.remove(mFragments.get(3));
        mFragments.add(newSafeButtonFragment);
        fragmentTransaction.add(R.id.fl_main, newSafeButtonFragment, "3");
        toolbar.setBackgroundColor(getResources().getColor(R.color.buttonMenuBg));
        fragmentTransaction.show(newSafeButtonFragment);
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commitAllowingStateLoss();
        mCurrentIndex = 3;
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyView();
    }
}
