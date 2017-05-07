package com.example.greyson.test1.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.example.greyson.test1.config.WSAppContext;
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
    private FragmentManager mFragmentManager;

    private List<BaseFragment> mFragments = new ArrayList<>();
    private SafetyMapFragment mSafetyMapFragment;
    private SafetyButtonFragment mSafetyButtonFragment;
    private SafetyTrackFragment mSafetyTrackFragment;
    private SafetyMoreFragment mSafetyMoreFragment;

    private static final int REQUEST_CALL_PHONE = 004;
    private int mCurrentIndex;

    @Override
    protected int getLayoutRes() {
        return R.layout.act_main;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = findView(R.id.toolbar);
        AppCompatTextView tvTitle = findView(R.id.tv_title);
        tvTitle.setText("U-Safe");
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
        //mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyMapFragment, "0").commitAllowingStateLoss();
        //mCurrentIndex = 0;
        //mLLSafetyMap.setSelected(true);

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_SELECT_FRAG_BUTTON);
        filter.addAction(Constants.INTENT_ACTION_USER_LOGIN);
        filter.addAction(Constants.INTENT_ACTION_USER_LOGOUT);
        registerReceiver(mReceiver, filter);
    }

    private void checkMenuIntent () {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String str = bundle.getString("menu");
        switch (str) {
            case "map":
                mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyMapFragment, "0").commitAllowingStateLoss();
                mCurrentIndex = 0;
                mLLSafetyMap.setSelected(true);
                break;
            case "button":
                mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyButtonFragment, "1").commitAllowingStateLoss();
                mCurrentIndex = 0;
                mLLSafetyMap.setSelected(true);
                break;
            case "track":
                mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyTrackFragment, "2").commitAllowingStateLoss();
                mCurrentIndex = 0;
                mLLSafetyMap.setSelected(true);
                break;
        }
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            switch (intent.getAction()) {
                case Constants.INTENT_ACTION_USER_LOGIN:
                    WSAppContext.getInstance();
                    mFragments.remove(2);
                    mFragments.add(2, mSafetyTrackFragment);
                    if (mSafetyTrackFragment.isAdded()) {
                        fragmentTransaction.show(mSafetyTrackFragment);
                    } else {
                        mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyTrackFragment, "2")
                                .commitAllowingStateLoss();
                        fragmentTransaction.show(mSafetyTrackFragment);
                    }
                    mCurrentIndex = 2;
                    mLLSafetyTrack.setSelected(true);

                    fragmentTransaction.hide(mSafetyMoreFragment);

                    fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commitAllowingStateLoss();

                    break;
                case Constants.INTENT_ACTION_USER_LOGOUT:
                    WSAppContext.getInstance();
                    mFragments.remove(2);
                    mFragments.add(2, mSafetyTrackFragment);
                    //选中loginFragment
                    if (mSafetyTrackFragment.isAdded()) {
                        fragmentTransaction.show(mSafetyTrackFragment);
                    } else {
                        mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyTrackFragment, "2")
                                .commitAllowingStateLoss();
                        fragmentTransaction.show(mSafetyTrackFragment);
                    }
                    mCurrentIndex = 2;
                    mLLSafetyTrack.setSelected(true);

                    fragmentTransaction.hide(mSafetyTrackFragment);

                    fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commitAllowingStateLoss();

                    break;
                case Constants.INTENT_ACTION_SELECT_FRAG_BUTTON:
                    if (mSafetyButtonFragment.isAdded()) {
                        fragmentTransaction.show(mSafetyButtonFragment);
                    } else {
                        mFragmentManager.beginTransaction().add(R.id.fl_main, mSafetyButtonFragment, "1")
                                .commitAllowingStateLoss();
                        fragmentTransaction.show(mSafetyButtonFragment);
                    }
                    mCurrentIndex = 1;
                    mLLSafetyButton.setSelected(true);

                    if (mSafetyMapFragment.isVisible()) {
                        fragmentTransaction.hide(mSafetyMapFragment);
                        mLLSafetyMap.setSelected(false);
                    } else if (mSafetyTrackFragment.isVisible()) {
                        fragmentTransaction.hide(mSafetyTrackFragment);
                        mLLSafetyTrack.setSelected(false);
                    } else if (mSafetyMoreFragment.isVisible()) {
                        fragmentTransaction.hide(mSafetyMoreFragment);
                        mLLSafetyMore.setSelected(false);
                    }

                    fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commitAllowingStateLoss();
                    break;
            }
        }
    };

    @Override
    protected void initEvent() {
        mLLSafetyMap.setOnClickListener(this);
        mLLSafetyButton.setOnClickListener(this);
        mLLSafetyTrack.setOnClickListener(this);
        mLLSafetyMore.setOnClickListener(this);
    }

    @Override
    protected void destroyView() {
        unregisterReceiver(mReceiver);
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
                index = 0;
                mLLSafetyMap.setSelected(true);
                break;
            case R.id.ll_safetybutton:
                index = 3;
                mLLSafetyButton.setSelected(true);
                break;
            case R.id.ll_safetytrack:
                index = 1;
                mLLSafetyTrack.setSelected(true);
                break;
            case R.id.ll_safetymore:
                index = mCurrentIndex;
                showCheckDialog();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle b = intent.getExtras();
        int extra = b.getInt("notification");
        switch (extra){
            case 0:
                onClick(mLLSafetyButton);
                break;
            case 1:
                setSafetyMapNotificationArg(1);
                break;
            case 2:
                setSafetyMapNotificationArg(2);
                break;
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
        fragmentTransaction.show(newSafeButtonFragment);
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commitAllowingStateLoss();
        mCurrentIndex = 3;
    }
}
