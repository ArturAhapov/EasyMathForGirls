package com.arturagapov.easymathforgirls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.*;
import android.widget.*;


public class StartActivity extends Activity {

    //Задержка времени
    private android.os.Handler mHandler = new android.os.Handler();
    private android.os.Handler sHandler = new android.os.Handler();

    private SoundPool rSoundPool;
    private int rSoundId = 1;
    private int rStreamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setSystemSound();
        setScreenSize();//Усранавливаем размер иконки
    }

    private void setScreenSize() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        int screenWidth = p.x;
        int cornerCloudWidth = (int)(screenWidth / 2.3f);
        int sunWidth = (int)(screenWidth / 3.5f);

        final LinearLayout buttons =(LinearLayout)findViewById(R.id.start_activity_buttons);
        final Button buttonStart = (Button)findViewById(R.id.game_start);
        final Button buttonLogin = (Button)findViewById(R.id.login);

        final ImageView sun = (ImageView) findViewById(R.id.image_sun);
        ViewGroup.LayoutParams sunParams = sun.getLayoutParams();
        setImageSizes(sunParams, sunWidth);

        final ImageView leftTop = (ImageView) findViewById(R.id.image_left);
        ViewGroup.LayoutParams leftTopParams = leftTop.getLayoutParams();
        setImageSizes(leftTopParams, cornerCloudWidth);


        final ImageView rightTop = (ImageView) findViewById(R.id.image_right);
        ViewGroup.LayoutParams rightTopParams = rightTop.getLayoutParams();
        setImageSizes(rightTopParams, cornerCloudWidth);

        Typeface type = Typeface.createFromAsset(getAssets(),"gretoon_highlight.ttf");

        final TextView text1 = (TextView)findViewById(R.id.logo1);
        final TextView text2 = (TextView)findViewById(R.id.logo2);
        final TextView text3 = (TextView)findViewById(R.id.logo3);
        text1.setTextColor(Color.GREEN);
        text2.setTextColor(Color.MAGENTA);
        text3.setTextColor(Color.CYAN);

        text1.setTypeface(type);
        text2.setTypeface(type);
        text3.setTypeface(type);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                setButtonsAnim(buttons, new BounceInterpolator(), 1500,0);
                setCloudsAnim(sun, 1000, 100);
                setCloudsAnim(leftTop, 1400, 400);
                setCloudsAnim(rightTop, 1400, 500);
                setLogoAnim(text1, 500);
                setLogoAnim(text2, 700);
                setLogoAnim(text3, 1000);
                setLogoAnim(buttonStart,800);
                setLogoAnim(buttonLogin,1200);
            }
        }, 300);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                playSystemSound(rSoundPool, rStreamId, rSoundId);
            }
        },500);
    }

    private void setImageSizes(ViewGroup.LayoutParams layoutParams, int width) {
        layoutParams.width = width;
        layoutParams.height = width * 2;
    }

    private void setButtonsAnim(final View view, Interpolator interpolator,long mDuration, long mOffset) {
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_bottom_to_up_accelerate);
        anim.setInterpolator(interpolator);
        anim.setStartOffset(mOffset);
        anim.setDuration(mDuration);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void setCloudsAnim(final ImageView view, long mDuration, long mOffset) {
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_up_to_bottom);
        anim.setStartOffset(mOffset);
        anim.setDuration(mDuration);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void setLogoAnim(View view, int mOffset) {
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_boolb_over_anim);
        anim.setStartOffset(mOffset);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void setSystemSound() {
        try {
            rSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            rSoundPool.load(this, R.raw.app_tone_success, 1);


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

    public void onClickStart(View view) {
        Intent intent = new Intent(this, Activity_1st.class);
        startActivity(intent);
    }
}
