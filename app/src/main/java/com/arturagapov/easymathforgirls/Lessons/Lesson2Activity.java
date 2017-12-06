package com.arturagapov.easymathforgirls.Lessons;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.arturagapov.easymathforgirls.Data;
import com.arturagapov.easymathforgirls.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Lesson2Activity extends Activity {
    //Serializable
    private static String fileNameData = "EasyMathData.ser";

    private static final int VIBRATOR_TIME_IN_MILES = 50;

    private int[] x = new int[10];
    private int k = 0;
    private char unit;
    private int keyNumber;
    private int color;//цвет кнопки
    //ProgressBar
    private int progress = 0;
    private int maxProgress = x.length * 100;
    private ProgressBar pbHorizontal;
    //Подключаем звуки
    private SoundPool rSoundPool;
    private int rSoundId = 1;
    private int rStreamId;


    private SoundPool chpookSoundPool;
    private int chpookSoundId = 1;
    private int chpookStreamId;

    //Native by Admob
    NativeExpressAdView adView;
    private boolean isNativeAdShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Восстанавливаем данные из сохраненного файла
        Data.userData = readFromFileData();
        //ProgressBar
        pbHorizontal = (ProgressBar) findViewById(R.id.lesson2progressBar);
        pbHorizontal.setMax(maxProgress);

        //Подключаем звуки
        setSystemSound();

        TextView level = (TextView) findViewById(R.id.lesson2);
        setFont(level, getResources().getColor(R.color.new_color_blue_SOFT));

        Intent intent = getIntent();
        unit = intent.getCharExtra("Unit", '+');// + - * /
        keyNumber = intent.getIntExtra("keyNumber", 0);//0 1 2 3 4 5 6 7 8 9 10
        setX();
        setText();
        questionAnimationIn();
        buttonAnination();
    }

    private void setSystemSound() {
        try {
            rSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            rSoundPool.load(this, R.raw.app_tone_success, 1);
            chpookSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            chpookSoundPool.load(this, R.raw.app_tone_facebook_chpook, 1);

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

    //Подключаем NativeAd by Admob
    private void requestNativeAd() {
        if (!Data.userData.isPremium()) {
            try {
                adView = (NativeExpressAdView) findViewById(R.id.lesson2_nativeAdmobAds);

                AdRequest requestNative = new AdRequest.Builder().build();//можно добавить фильтр для детей AdRequest request = new AdRequest.Builder().setLocation(location).setGender(AdRequest.GENDER_FEMALE).setBirthday(new GregorianCalendar(1985, 1, 1).getTime()).tagForChildDirectedTreatment(true).build();
                adView.loadAd(requestNative);
                isNativeAdShow = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setX() {
        boolean sum;
        do {
            sum = false;
            for (int i = 0; i < x.length; i++) {
                x[i] = (int) (Math.random() * x.length);
            }
            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < x.length; j++) {
                    if (j != i && x[j] == x[i]) {
                        sum = true;
                    }
                }
            }
        } while (sum);
    }

    private void setText() {
        int b = x[k];
        TextView questionText = (TextView) findViewById(R.id.lesson_2_question);
        String text = doMathAction(b, keyNumber, unit);
        questionText.setText(text);
        setFont(questionText, getColors());
        k = k + 1;

        postProgress(progress * 100, k * 100);

        progress = k;
    }

    private void setFont(TextView view, int color) {
        Typeface type = Typeface.createFromAsset(getAssets(), Data.userData.font);
        view.setTypeface(type);

        view.setTextColor(color);
    }
    private void setFont(Button view, int color) {
        Typeface type = Typeface.createFromAsset(getAssets(), Data.userData.font);
        view.setTypeface(type);

        view.setTextColor(color);
    }

    private int getColors() {
        //Colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(getResources().getColor(R.color.new_color_yellow_SOFT));
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);

        int i = (int) (Math.random() * colors.size());
        return colors.get(i);
    }

    //ProgressBar
    private void postProgress(int preProgress, int progress) {
        pbHorizontal.setProgress(progress);

        ObjectAnimator animation = ObjectAnimator.ofInt(pbHorizontal, "progress", preProgress, progress);
        animation.setDuration(800);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private String doMathAction(int i, int j, char action) {
        Button answer = (Button) findViewById(R.id.lesson_2_answer);
        int result;
        String text = "null";
        switch (action) {
            case '+':
                result = i + j;
                text = ("" + i + " + " + j + " = ");
                answer.setText("" + result);
                break;
            case '-':
                result = i + j;
                text = ("" + result + " - " + j + " = ");
                answer.setText("" + i);
                break;
            case 'x':
                result = i * j;
                text = ("" + i + " x " + j + " = ");
                answer.setText("" + result);
                break;
            case '/':
                result = i * j;
                text = ("" + result + " / " + j + " = ");
                answer.setText("" + i);
                break;
        }
        return text;
    }

    public void onClickNextAnswer(View view) {
        if (k >= (x.length - 6) & !isNativeAdShow) {
            requestNativeAd();
        }
        if (k < x.length) {
            playSystemSound(rSoundPool,rStreamId,rSoundId);
            setButtonColor();
            setText();
            questionAnimationIn();
            buttonAnination();
        } else {
            Button nextButton = (Button) findViewById(R.id.next_lesson2);
            if (isNativeAdShow) {
                adView.setVisibility(View.GONE);
            }
            setAnim(nextButton, 500, 100);
            setVibro();
            playSystemSound(chpookSoundPool,chpookStreamId,chpookSoundId);
        }
    }

    private void setAnim(View view, long mDuration, long mOffset) {
        float scale = (float) (0.3f * Math.random());
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.new_boolb_over_anim);
        anim.scaleCurrentDuration(scale);
        anim.setStartOffset(mOffset);
        anim.setDuration(mDuration);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void setVibro() {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATOR_TIME_IN_MILES);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setButtonColor() {
        Button answerButton = (Button) findViewById(R.id.lesson_2_answer);
        setFont(answerButton, Color.WHITE);
        int preColor = color;//предыдущий цвет кнопки
        do {
            int z = (int) (Math.random() * 100);
            if (z < 20) {
                color = R.drawable.buttonroundedblue;
                //answerButton.setBackground(getResources().getDrawable(color));
            } else if (z < 40) {
                color = R.drawable.buttonroundedgreen;
                //answerButton.setBackground(getResources().getDrawable(color));
            } else if (z < 60) {
                color = R.drawable.buttonroundedmagenta;
                //answerButton.setBackground(getResources().getDrawable(color));
            } else if (z < 80) {
                color = R.drawable.buttonroundedred;
                //answerButton.setBackground(getResources().getDrawable(color));
            } else if (z < 100) {
                color = R.drawable.buttonroundedyellow;
                //answerButton.setBackground(getResources().getDrawable(color));
            }
        } while (color == preColor);
        answerButton.setBackground(getResources().getDrawable(color));
    }

    //Animation
    private void questionAnimationIn() {
        TextView questionImageIn = (TextView) findViewById(R.id.lesson_2_question);
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft1);
        questionImageIn.startAnimation(anim);
    }

    private void buttonAnination() {
        Button button1 = (Button) findViewById(R.id.lesson_2_answer);
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft2);
        button1.startAnimation(anim);
    }

    public void onClickNext(View view) {
        Intent intent = new Intent(this, Lesson3Activity.class);
        intent.putExtra("Unit", unit);
        intent.putExtra("keyNumber", keyNumber);
        startActivity(intent);
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

    @Override
    protected void onStop() {
        super.onStop();
        saveToFileData();
    }
}
