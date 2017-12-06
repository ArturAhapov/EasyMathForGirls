package com.arturagapov.easymathforgirls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Activity_1st extends Activity {
    //Serializable
    private static String fileNameData = "EasyMathData.ser";

    //Задержка времени
    private Handler mHandler = new Handler();

    //Sound
    private SoundPool rSoundPool;
    private int rSoundId = 1;
    private int rStreamId;

    //Firebase EventLog
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1st);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Восстанавливаем данные из сохраненного файла
        readFromFileData();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setSystemSound();
        setTexts();
        setSessionCount();
        setWhiteButtons();
    }

    private void setTexts() {
        TextView[] textViewChapters = {
                (TextView) findViewById(R.id.text_plus),
                (TextView) findViewById(R.id.text_minus),
                (TextView) findViewById(R.id.text_multiplication),
                (TextView) findViewById(R.id.text_division)
        };
        for (TextView i : textViewChapters) {
            setFont(i);
        }

        TextView[] textViewPlus = {(TextView) findViewById(R.id.sample_plus_1),
                (TextView) findViewById(R.id.sample_plus_2),
                (TextView) findViewById(R.id.sample_plus_3),
                (TextView) findViewById(R.id.sample_plus_4),
                (TextView) findViewById(R.id.sample_plus_5),
        };
        TextView[] textViewMinus = {(TextView) findViewById(R.id.sample_minus_1),
                (TextView) findViewById(R.id.sample_minus_2),
                (TextView) findViewById(R.id.sample_minus_3),
                (TextView) findViewById(R.id.sample_minus_4),
                (TextView) findViewById(R.id.sample_minus_5)
        };
        TextView[] textViewMulti = {(TextView) findViewById(R.id.sample_multiplication_1),
                (TextView) findViewById(R.id.sample_multiplication_2),
                (TextView) findViewById(R.id.sample_multiplication_3),
                (TextView) findViewById(R.id.sample_multiplication_4),
                (TextView) findViewById(R.id.sample_multiplication_5)
        };
        TextView[] textViewDivision = {(TextView) findViewById(R.id.sample_division_1),
                (TextView) findViewById(R.id.sample_division_2),
                (TextView) findViewById(R.id.sample_division_3),
                (TextView) findViewById(R.id.sample_division_4),
                (TextView) findViewById(R.id.sample_division_5)
        };
        setSampleColors(textViewPlus);
        setSampleColors(textViewMinus);
        setSampleColors(textViewMulti);
        setSampleColors(textViewDivision);
    }

    private void setSampleColors(TextView[] text) {
        //Colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(getResources().getColor(R.color.new_color_yellow_SOFT));
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);

        Collections.shuffle(colors);

        for (int i = 0; i < text.length; i++) {
            setFont(text[i], colors.get(i));
        }

    }

    private void setFont(TextView view, int color) {
        Typeface type = Typeface.createFromAsset(getAssets(), "gretoon_highlight.ttf");
        view.setTypeface(type);

        view.setTextColor(color);
    }

    private void setFont(TextView view) {
        Typeface type = Typeface.createFromAsset(getAssets(), "gretoon_highlight.ttf");
        view.setTypeface(type);
    }

    private void setSessionCount() {
        Data.userData.setSessionCount(Data.userData.getSessionCount() + 1);
        saveToFileData();
    }

    private void eventLogs(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void setAnim(View view, long mDuration, long mOffset, long deley) {
        float scale = (float) (0.5f * Math.random());
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_boolb_bounce_anim);
        anim.scaleCurrentDuration(scale);
        anim.setStartOffset(mOffset);
        anim.setDuration(mDuration);
        view.startAnimation(anim);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                playSystemSound(rSoundPool, rStreamId, rSoundId);
            }
        }, deley);
    }

    private void setWhiteButtons() {
        int k = (int) (Math.random() * 100);

        RelativeLayout message = (RelativeLayout) findViewById(R.id.message);

        LinearLayout inviteFriends = (LinearLayout) findViewById(R.id.invite_friends);
        LinearLayout tryMoreApps = (LinearLayout) findViewById(R.id.try_more_apps);
        LinearLayout rateApp = (LinearLayout) findViewById(R.id.rate_app);
        inviteFriends.setVisibility(View.INVISIBLE);
        tryMoreApps.setVisibility(View.INVISIBLE);
        rateApp.setVisibility(View.INVISIBLE);

        if (k < 25) {
            setInviteIcons(inviteFriends);
            TextView textViewInvite = (TextView) findViewById(R.id.invite_friends_text);
            setFont(textViewInvite, Color.RED);
            setMessageAreaNullSize(tryMoreApps);
            setMessageAreaNullSize(rateApp);
            setAnim(message, 900, 200, 400);
        } else if (k < 50) {
            setMoreAppsIcons(tryMoreApps);
            TextView textViewMoreApps = (TextView) findViewById(R.id.try_more_apps_text);
            setFont(textViewMoreApps, Color.GREEN);
            setMessageAreaNullSize(inviteFriends);
            setMessageAreaNullSize(rateApp);
            setAnim(message, 900, 200, 400);
        } else if (k < 75) {
            setRate(rateApp);
            TextView textViewRate = (TextView) findViewById(R.id.rate_app_text);
            setFont(textViewRate, Color.BLUE);
            setMessageAreaNullSize(tryMoreApps);
            setMessageAreaNullSize(inviteFriends);
            setAnim(message, 900, 200, 400);
        } else {
            setMessageAreaNullSize((LinearLayout) findViewById(R.id.message_area));
        }
    }

    private void setInviteIcons(LinearLayout area) {

        ImageView icon1 = (ImageView) findViewById(R.id.invite_1_learn);
        ImageView icon2 = (ImageView) findViewById(R.id.invite_2_learn);
        ImageView icon3 = (ImageView) findViewById(R.id.invite_3_learn);

        int[] ic = {
                R.drawable.ic_invite_1,
                R.drawable.ic_invite_2,
                R.drawable.ic_invite_3,
                R.drawable.ic_invite_4,
                R.drawable.ic_invite_5,
                R.drawable.ic_invite_6,
                R.drawable.ic_invite_7,
                R.drawable.ic_invite_8,
                R.drawable.ic_invite_0};

        int k;
        int l;
        int m;

        do {
            k = (int) (Math.random() * ic.length);
            l = (int) (Math.random() * ic.length);
            m = (int) (Math.random() * ic.length);
        } while (k == l || k == m || l == m);

        icon1.setImageResource(ic[k]);
        icon2.setImageResource(ic[l]);
        icon3.setImageResource(ic[m]);

        area.setVisibility(View.VISIBLE);
    }

    private void setMoreAppsIcons(LinearLayout area) {
        ImageView icon1 = (ImageView) findViewById(R.id.try_more_apps_icon);

        int[] ic = {
                R.drawable.ic_more_apps_eathy_math,
                R.drawable.ic_more_apps_oxford,
                R.drawable.ic_more_apps_geo_quiz};

        int k = (int) (Math.random() * ic.length);

        icon1.setImageResource(ic[k]);
        area.setVisibility(View.VISIBLE);
    }

    private void setRate(LinearLayout area) {

        int k = (int) (Math.random() * 10);

        if (Data.userData.isRate() && k != 7) {
            area.setVisibility(View.INVISIBLE);
            setMessageAreaNullSize((LinearLayout) findViewById(R.id.message_area));
        } else {
            area.setVisibility(View.VISIBLE);
        }
    }

    private void setMessageAreaNullSize(LinearLayout area) {
        ViewGroup.LayoutParams areaParams = area.getLayoutParams();
        areaParams.height = 0;
    }

    public void onClickMoreApps(View view) {
        goMoreApps();
    }

    public void onClickInviteFriends(View view) {
        inviteFriends();
    }

    public void onClickRate(View view) {
        rateApp();
    }

    private void goMoreApps() {
        Intent intent = new Intent(this, MoreAppsActivity.class);
        startActivity(intent);
    }

    private void inviteFriends() {
        final String infoAboutForShare = (getResources().getString(R.string.invite_friends_message)
                + "\n" + Uri.parse("https://play.google.com/store/apps/details?id=com.arturagapov.easymathforgirls"));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, infoAboutForShare);
        startActivity(shareIntent);
    }

    private void rateApp() {
        final Dialog dialog = new Dialog(Activity_1st.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rate_app);

        TextView notNow = (TextView) dialog.findViewById(R.id.not_now_button);
        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        TextView rate = (TextView) dialog.findViewById(R.id.rate_5_star_button);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.userData.setRate(true);
                saveToFileData();
                dialog.cancel();
            }
        });

        dialog.show();
        saveToFileData();
    }

    public void onClickLearnAdding(View view) {
        Intent intent = new Intent(this, Activity_2nd.class);
        eventLogs("MathAction", "ADDITION");
        intent.putExtra("Unit", "Plus");
        intent.putExtra("Level", "learn");
        startActivity(intent);
    }

    public void onClickLearnSubtraction(View view) {
        Intent intent = new Intent(this, Activity_2nd.class);
        eventLogs("MathAction", "SUBTRACTION");
        intent.putExtra("Unit", "Minus");
        intent.putExtra("Level", "learn");
        startActivity(intent);
    }

    public void onClickLearnMultiplication(View view) {
        Intent intent = new Intent(this, Activity_2nd.class);
        eventLogs("MathAction", "MULTIPLICATION");
        intent.putExtra("Unit", "Multiply");
        intent.putExtra("Level", "learn");
        startActivity(intent);
    }

    public void onClickLearnDivision(View view) {
        Intent intent = new Intent(this, Activity_2nd.class);
        eventLogs("MathAction", "DIVISION");
        intent.putExtra("Unit", "Division");
        intent.putExtra("Level", "learn");
        startActivity(intent);
    }

    private void setSystemSound() {
        try {
            rSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            rSoundPool.load(this, R.raw.app_tone_facebook_chpook, 1);

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

    // Serializes an object and saves it to a file
    private void saveToFileData() {//Context context) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(fileNameData, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(Data.userData);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Creates an object by reading it from a file
    private void readFromFileData() {//Context context) {
        try {
            FileInputStream fileInputStream = openFileInput(fileNameData);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Data.userData = (Data) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
