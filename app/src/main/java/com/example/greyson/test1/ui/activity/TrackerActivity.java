package com.example.greyson.test1.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;

/**
 * Tis class is about tracker function
 *
 * @author Greyson, Carson
 * @version 1.0
 */
public class TrackerActivity extends BaseActivity {

    private Intent setting;
    private TextView desDisplay;
    private TextView timDisplay;
    private TextView notDisplay;
    private Button staButton;
    private Button canButton;


    /**
     * This method is to connect to layout
     *
     * @return
     */
    @Override
    protected int getLayoutRes() {
        return R.layout.frag_trackeractivity;
    }

    /**
     * This method is to initial view
     *
     * @return
     */
    @Override
    protected void initView() {

        desDisplay = (TextView) findViewById(R.id.desDisplay);
        timDisplay = (TextView) findViewById(R.id.timeDisplay);
        notDisplay = (TextView) findViewById(R.id.notDisplay);
        canButton = (Button) findViewById(R.id.canButton);
        staButton = (Button) findViewById(R.id.staButton);
        setting = getIntent();

        //  This method is to start ringing
        final MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(this, RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // This is timer
        final Handler handler = new Handler();
        final Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                mp.start();
            }
        };

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dialog(handler, this, mp, runnable1);
                handler.postDelayed(runnable1, 5000);
            }
        };

        staButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(runnable, countTime()*1000);
            }
        });

        canButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                finish();
            }
        });
    }

    /**
     * This method is to shoe and set dialog
     * @param handler
     * @param runnable
     * @param mp
     * @param runnable1
     */
    private void dialog(final Handler handler, final Runnable runnable, final MediaPlayer mp, final Runnable runnable1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you safe?\nYou have 5s to confirm");
        builder.setTitle("Alarm");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                handler.removeCallbacks(runnable);
                handler.removeCallbacks(runnable1);
                handler.postDelayed(runnable, countTime()*1000);
                mp.stop();
            }
        });
        builder.setNegativeButton("+ 5 min", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mp.stop();
                handler.removeCallbacks(runnable);
                handler.removeCallbacks(runnable1);
                handler.postDelayed(runnable, 5 * 60 * 1000);
            }
        });
        builder.create().show();
    }

    /**
     * This method is to receive time
     */
    private int countTime() {
        //String[] time = timDisplay.getText().toString().split("hours|mins|");
        //int hours = Integer.parseInt(time[0]);
        //int minss = Integer.parseInt(time[1]);
        int mins = Integer.parseInt(notDisplay.getText().toString());
        int seconds = mins*60;
        return seconds;
    }


    @Override
    protected void initData() {
        Bundle b = setting.getExtras();
        desDisplay.setText(b.getString("Des"));
        timDisplay.setText(b.getString("Tim"));
        notDisplay.setText(b.getString("Not"));
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void destroyView() {

    }


}
