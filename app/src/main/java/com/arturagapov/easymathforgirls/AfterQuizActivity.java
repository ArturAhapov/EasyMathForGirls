package com.arturagapov.easymathforgirls;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.ads.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.*;

public class AfterQuizActivity extends Activity{
    //Serializable
    private static String fileNameData = "EasyMathData.ser";
    //Задержка времени
    private Handler mHandler = new Handler();

    //Firebase EventLog
    private FirebaseAnalytics mFirebaseAnalytics;

    //GoogleApiClient
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;


    //Данные интента
    private int rightQuantity;
    private int wrongQuantity;
    private int score;
    private char unitChar;
    private int time;
    private String unit;
    private String diffucultyString;
    public static final String EXTRA_MESSAGE_DIFFICULTY = null;
    private int totalScore;

    private int leaderboardScore;
    private int countWithoutWrong = 0;

    //Подключаем звуки
    private SoundPool wwSoundPool;
    private int wwSoundId = 1;
    private int wwStreamId;

    //NativeAd by Admob
    NativeExpressAdView nativeAdmob;
    AdRequest requestNativeAdmob;
    //NativeAd by Facebook
    private NativeAd nativeAd;
    private NativeAdViewAttributes viewAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Восстанавливаем данные из сохраненного файла
        Data.userData = readFromFileData();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setAds();

        //Получаем интент
        Intent intent = getIntent();
        unit = intent.getStringExtra("Unit");// + - * /
        diffucultyString = intent.getStringExtra(EXTRA_MESSAGE_DIFFICULTY);
        rightQuantity = intent.getIntExtra("rights", -1);
        wrongQuantity = intent.getIntExtra("wrongs", -1);
        score = intent.getIntExtra("score", -1);
        totalScore = score;
        time = intent.getIntExtra("time", -1);

        //Подключаем звуки
        try {
            wwSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            wwSoundPool.load(this, R.raw.notif2, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playCongrats();
        setData();
        waiting();
    }

    private void waiting() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                RelativeLayout fon = (RelativeLayout) findViewById(R.id.after_quiz_fon);
                fon.setVisibility(View.INVISIBLE);
            }
        }, 1000);
    }

    private void setAds() {
        if (!Data.userData.isPremium()) {
            //Подключаем NativeAd by Admob
            nativeAdmob = (NativeExpressAdView) findViewById(R.id.nativeAdmobAds_after_quiz_activity);
            requestNativeAdmob = new AdRequest.Builder().build();//можно добавить фильтр для детей AdRequest request = new AdRequest.Builder().setLocation(location).setGender(AdRequest.GENDER_FEMALE).setBirthday(new GregorianCalendar(1985, 1, 1).getTime()).tagForChildDirectedTreatment(true).build();
            nativeAdmob.loadAd(requestNativeAdmob);
        }
    }

    private void setData() {

        TextView youScore = (TextView) findViewById(R.id.after_quiz_score);
        youScore.setText("" + score);

        TextView timeBonus = (TextView) findViewById(R.id.after_quiz_time_bonus);
        TextView total = (TextView) findViewById(R.id.total_score);
        TextView weRecommend = (TextView) findViewById(R.id.after_quiz_we_recommend);

        ImageView smile = (ImageView) findViewById(R.id.after_quiz_image_smile);
        int sm = (int) (Math.random() * 10);

        if (wrongQuantity == 0) {
            Data.userData.setUnitsComplete(Data.userData.getUnitsComplete() + 1);
            countWithoutWrong = Data.userData.getUnitsComplete();
            playCongrats();
            int sc = setTimeBonus(time);
            timeBonus.setText("" + sc);
            weRecommend.setTextSize(2.0f);

            if (sm < 3) {
                smile.setImageResource(R.drawable.smile_happy_2);
            } else if (sm < 6) {
                smile.setImageResource(R.drawable.smile_happy_3);
            } else {
                smile.setImageResource(R.drawable.smile_happy_1);
            }
        } else {

            if (sm < 5) {
                smile.setImageResource(R.drawable.smile_sad_1);
            } else {
                smile.setImageResource(R.drawable.smile_sad_2);
            }

            TextView withWrong = (TextView) findViewById(R.id.you_complete_this_unit);
            withWrong.setText(getResources().getString(R.string.you_complete_practice_without_wrong));
            weRecommend.setText(getResources().getString(R.string.you_complete_unit_with_wrong));
        }
        //countWithoutWrong = Data.userData.getUnitsComplete();
        total.setText("" + totalScore);
    }

    private int setCurrentScore() {
        int currentScore = Data.userData.getScore();
        currentScore = currentScore + totalScore;
        Data.userData.setScore(currentScore);
        leaderboardScore = currentScore;
        return currentScore;
    }

    private int setTimeBonus(int t) {
        int timeBonus = (int) (1000 / t);
        totalScore = totalScore + timeBonus;
        return timeBonus;
    }

    private void rateDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_rate, null);
        //AlertDialog
        String positiveButtonText = getResources().getString(R.string.rate);
        String negativeButtonText = getResources().getString(R.string.rate_later);
        AlertDialog.Builder builder = new AlertDialog.Builder(AfterQuizActivity.this);
        builder.setCancelable(false)
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        eventLogsRate("Rate the App", "Rate the App_NOT NOW");
                        dialog.cancel();
                        continueAfterAds();
                    }
                })
                .setPositiveButton(positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                eventLogsRate("Rate the App", "Rate the App_YES");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.arturagapov.easymathpr"));
                                Data.userData.setRate(true);
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id=com.arturagapov.easymathpr")));
                                }

                            }
                        });
        builder.setView(dialoglayout);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void playCongrats() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        wwStreamId = wwSoundPool.play(wwSoundId, leftVolume, rightVolume, priority, no_loop,
                normal_playback_rate);
    }

    public void onClickContinueAfterQuiz(View view) {
        if(!Data.userData.isPremium() && (Data.userData.getSessionCount() % 9) == 0){
            continueToPurchase();
        }else if ((Data.userData.getSessionCount() % 7) == 0 && !Data.userData.isRate()) {
            rateDialog();
        } else {
            continueAfterAds();
        }
    }

    public void onClickTryAgain(View view) {
        saveToFileData();
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("mathAction", unit);
        intent.putExtra(QuizActivity.EXTRA_MESSAGE_DIFFICULTY, diffucultyString);
        startActivity(intent);
    }

    private void continueAfterAds() {
        saveToFileData();
        Intent intent = new Intent(this, Activity_1st.class);
        startActivity(intent);
    }

    private void continueToPurchase() {
        saveToFileData();
        Intent intent = new Intent(this, BuyPremiumActivity.class);
        startActivity(intent);
    }

    private void eventLogsRate(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.GENERATE_LEAD, bundle);
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
    private Data readFromFileData() {//Context context) {
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
        return Data.userData;
    }

    //Отключаем кнопку Назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
        //return super.onKeyDown(keyCode, event);
    }

}
