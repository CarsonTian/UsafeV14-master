package com.example.greyson.test1.ui.activity;

import android.view.View;
import android.widget.Button;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;

public class UserGuide extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.user_guide;
    }

    @Override
    protected void initView() {
        Button back = (Button) findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
