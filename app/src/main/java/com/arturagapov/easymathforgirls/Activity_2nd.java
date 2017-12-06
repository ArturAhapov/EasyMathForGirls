package com.arturagapov.easymathforgirls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.arturagapov.easymathforgirls.Lessons.Lesson1Activity;

public class Activity_2nd extends Activity {
    //Задержка времени
    private Handler mHandler = new Handler();
    private Handler bHandler;// = new Handler();//шарики
    private Runnable myRunnable;
    private long runnableTime=2000;

    private char unit;

    private SoundPool rSoundPool;
    private int rSoundId = 1;
    private int rStreamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2nd);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setSystemSound();

        Intent intent = getIntent();
        String action = intent.getStringExtra("Unit");// + - * /
        setUnitChar(action);
        setButtonListener();

        setBallonsAnimHandler();
    }

    private void setUnitChar(String a) {
        if (a.equals("Plus")) {
            unit = '+';
        } else if (a.equals("Minus")) {
            unit = '-';
        } else if (a.equals("Multiply")) {
            unit = 'x';
        } else if (a.equals("Division")) {
            unit = '/';
            Button btn0 = (Button) findViewById(R.id.btn0);
            btn0.setVisibility(View.INVISIBLE);
        }
    }

    private void setButtonListener() {
        final Intent intent = new Intent(this, Lesson1Activity.class);
        Button[] button = {
                (Button) findViewById(R.id.btn0),
                (Button) findViewById(R.id.btn1),
                (Button) findViewById(R.id.btn2),
                (Button) findViewById(R.id.btn3),
                (Button) findViewById(R.id.btn4),
                (Button) findViewById(R.id.btn5),
                (Button) findViewById(R.id.btn6),
                (Button) findViewById(R.id.btn7),
                (Button) findViewById(R.id.btn8),
                (Button) findViewById(R.id.btn9),
                (Button) findViewById(R.id.btn10),
        };

        Typeface type = Typeface.createFromAsset(getAssets(), "gretoon_highlight.ttf");

        for (int i = 0; i < button.length; i++) {
            String buttonText = ("" + unit + " " + button[i].getText() + " ");
            final int keyNumber = i;
            //Test
            button[i].setWidth(R.dimen.choice_button_height);
            button[i].setHeight(R.dimen.choice_button_height);
            button[i].setTypeface(type);

            button[i].setText(buttonText);
            button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("Unit", unit);
                    intent.putExtra("keyNumber", keyNumber);
                    startActivity(intent);
                }
            });
            long animDuration = (long) (800 + i * 30);
            long animOffset = (long) (i * 50);
            if (i < button.length - 1) {
                setAnim(button[i], animDuration, animOffset);
            } else {
                setAnim(button[i], 900, 0);
            }
        }
        mHandler.postDelayed(new Runnable() {
            public void run() {
                playSystemSound(rSoundPool, rStreamId, rSoundId);
            }
        }, 200);
    }

    private void setAnim(View view, long mDuration, long mOffset) {
        float scale = (float) (0.5f * Math.random());
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_boolb_bounce_anim);
        anim.scaleCurrentDuration(scale);
        anim.setStartOffset(mOffset);
        anim.setDuration(mDuration);
        view.startAnimation(anim);
    }

    private void setBallonsAnimHandler() {

        final ImageView imageView01 = (ImageView) findViewById(R.id.ballon01);
        final ImageView imageView02 = (ImageView) findViewById(R.id.ballon02);

        myRunnable = new Runnable() {
            @Override
            public void run() {
                imageView01.setImageResource(getBallons());
                imageView02.setImageResource(getBallons());

                setBallonsAnim(imageView01, 4000, 0);
                setBallonsAnim(imageView02, 3000, 2000);
                bHandler.postDelayed(this, runnableTime);
            }
        };
    }

    private void setBallonsAnim(final ImageView view, long mDuration, long mOffset) {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        final float xScale = p.x;
        final float yScale = p.y * 8;
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_bottom_to_up_accelerate);
        anim.scaleCurrentDuration(yScale);
        anim.setStartOffset(mOffset);
        anim.setDuration(mDuration);
        view.startAnimation(anim);
        view.setVisibility(View.GONE);

        runnableTime=10000;
    }

    private int getBallons(){
        int[] ic = {
                R.drawable.ballon_blue,
                R.drawable.ballon_green,
                R.drawable.ballon_red,
                R.drawable.ballon_violet,
                R.drawable.ballon_yellow,
                };

        int k = (int) (Math.random() * ic.length);

        return ic[k];
    }

    private void setSystemSound() {
        try {
            rSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            rSoundPool.load(this, R.raw.app_tone_popup, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSystemSound(SoundPool soundPool, int streamId, int soundId) {

        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float leftVolume = curVolume / maxVolume;
            float rightVolume = curVolume / maxVolume;
            int priority = 1;
            int no_loop = 0;
            float normal_playback_rate = 1f;
            streamId = soundPool.play(soundId, leftVolume, rightVolume, priority, no_loop,
                    normal_playback_rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickMoreBtn(View view) {
        LinearLayout getMoreBtn = (LinearLayout) findViewById(R.id.get_more_btn);
        TextView moreBtn = (TextView) findViewById(R.id.more);
        moreBtn.setVisibility(View.INVISIBLE);
        getMoreBtn.setVisibility(View.VISIBLE);
        playSystemSound(rSoundPool, rStreamId, rSoundId);
    }

    protected void onStart() {
        super.onStart();
        bHandler = new Handler();
        bHandler.post(myRunnable);
    }

    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(myRunnable);
    }
}
