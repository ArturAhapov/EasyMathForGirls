package com.arturagapov.easymathforgirls.Lessons;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.arturagapov.easymathforgirls.AfterLessonActivity;
import com.arturagapov.easymathforgirls.Data;
import com.arturagapov.easymathforgirls.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import java.io.*;
import java.util.ArrayList;


public class Lesson3Activity extends Activity {
    //Serializable
    private static String fileNameData = "EasyMathData.ser";
    //Задержка времени
    private Handler mHandler = new Handler();
    private Handler bHandler;// = new Handler();//шарики
    private Runnable myRunnable;
    private long runnableTime = 2000;

    private int[] x = new int[10];
    private int k = 0;
    private char unit;
    private int keyNumber;
    private int rightQuantity = 0;
    private int wrongQuantity = 0;
    //Buttons
    int rightButton;
    int rightAnswer;
    int wrongtAnswer;
    //Подключаем звуки
    private SoundPool mSoundPool;
    private SoundPool rSoundPool;
    private SoundPool wSoundPool;
    private SoundPool fSoundPool;
    private int mSoundId = 1;
    private int rSoundId = 1;
    private int wSoundId = 1;
    private int fSoundId = 1;
    private int mStreamId;
    private int rStreamId;
    private int wStreamId;
    private int fStreamId;

    //ProgressBar
    private int progress = 0;
    private int maxProgress = x.length * 100;
    private ProgressBar pbHorizontal;

    //Реклама
    //Native by Admob
    private boolean isNativeAdShow = false;
    //Interstitial by Admob
    private com.google.android.gms.ads.InterstitialAd mInterstitial;
    AdRequest mInterstitialAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson3);
        //Восстановка переменных из Bundle
        if (savedInstanceState != null) {
            rightButton = savedInstanceState.getInt("rightButton");
            rightQuantity = savedInstanceState.getInt("rightQuantity");
        }

        //ProgressBar
        pbHorizontal = (ProgressBar) findViewById(R.id.lesson3progressBar);
        pbHorizontal.setMax(maxProgress);

        setInterstitial();

        //Подключаем звуки
        try {
            mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            mSoundPool.load(this, R.raw.complet3, 1);
            rSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            rSoundPool.load(this, R.raw.rightanswer5, 1);
            wSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            wSoundPool.load(this, R.raw.oops, 1);
            fSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            fSoundPool.load(this, R.raw.wronganswer, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        TextView level = (TextView) findViewById(R.id.lesson3);
        setFont(level, getResources().getColor(R.color.new_color_blue_SOFT));

        Intent intent = getIntent();
        unit = intent.getCharExtra("Unit", '+');// + - * /
        keyNumber = intent.getIntExtra("keyNumber", 0);//0 1 2 3 4 5 6 7 8 9 10
        setX();
        setText();
        setButtonText();
        questionAnimationIn();
        buttonAnination();

        setBallonsAnimHandler();
    }

    //Ballons
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

        runnableTime = 10000;
    }

    private int getBallons() {
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

    private void setInterstitial() {
        if (!Data.userData.isPremium()) {
            //Подключаем InterstitialAd by Admob
            mInterstitial = new com.google.android.gms.ads.InterstitialAd(this);
            mInterstitial.setAdUnitId("ca-app-pub-1399393260153583/6073043554");
            mInterstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Continue();
                }
            });
            mInterstitialAdRequest = new AdRequest.Builder().build();
            mInterstitial.loadAd(mInterstitialAdRequest);
        }
    }

    /*Подключаем NativeAd by Admob
    private void requestNativeAd() {
        if (!Data.userData.isPremium()) {
            NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.lesson3_nativeAdmobAds);
            AdRequest requestNative = new AdRequest.Builder().build();//можно добавить фильтр для детей AdRequest request = new AdRequest.Builder().setLocation(location).setGender(AdRequest.GENDER_FEMALE).setBirthday(new GregorianCalendar(1985, 1, 1).getTime()).tagForChildDirectedTreatment(true).build();
            adView.loadAd(requestNative);
            isNativeAdShow = true;
        }
    }*/

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
        if (k >= (x.length - 5) & !isNativeAdShow) {
            //requestNativeAd();
        }
        int b = x[k];
        TextView questionText = (TextView) findViewById(R.id.lesson_3_question);
        TextView answer = (TextView) findViewById(R.id.lesson3_answer);
        answer.setVisibility(View.INVISIBLE);
        String text = doMathAction(b, keyNumber, unit);
        questionText.setText(text);

        setFont(questionText, getColors());
        setFont(answer, getColors());

        k = k + 1;
        postProgress(progress * 100, k * 100);
        progress = k;
        setButtonText();
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
        TextView answer = (TextView) findViewById(R.id.lesson3_answer);
        int result;
        String text = "null";
        switch (action) {
            case '+':
                result = i + j;
                text = ("" + i + " + " + j + " = ");
                rightAnswer = result;
                answer.setText("" + result);
                break;
            case '-':
                result = i + j;
                text = ("" + result + " - " + j + " = ");
                rightAnswer = i;
                answer.setText("" + i);
                break;
            case 'x':
                result = i * j;
                text = ("" + i + " x " + j + " = ");
                rightAnswer = result;
                answer.setText("" + result);
                break;
            case '/':
                result = i * j;
                text = ("" + result + " / " + j + " = ");
                rightAnswer = i;
                answer.setText("" + i);
                break;
        }
        setWrongAnswer(rightAnswer);
        return text;
    }

    private void setWrongAnswer(int r) {
        do {
            if (r > 2 && r < keyNumber * 10) {
                if ((int) (Math.random() * 100) < 50) {
                    wrongtAnswer = r + 2;
                } else {
                    wrongtAnswer = r - 2;
                }
            } else if (r <= 2) {
                wrongtAnswer = r + 1;
            } else {
                wrongtAnswer = r - 1;
            }
        } while (wrongtAnswer == r);//x[k]);
    }

    private void setButtonText() {
        //Typeface face = Typeface.createFromAsset(getAssets(), "disneypark.ttf");
        Button button1 = (Button) findViewById(R.id.lesson3Answer1);
        Button button2 = (Button) findViewById(R.id.lesson3Answer2);

        setFont(button1, Color.MAGENTA);
        setFont(button2, Color.MAGENTA);

        rightButton = 0;
        int x = (int) (Math.random() * 100);
        if (x < 50) {
            button1.setText("" + rightAnswer);
            button2.setText("" + wrongtAnswer);
            rightButton = 1;
        } else {
            button2.setText("" + rightAnswer);
            button1.setText("" + wrongtAnswer);
            rightButton = 2;
        }
    }

    private void showRightAnswer() {
        TextView answer = (TextView) findViewById(R.id.lesson3_answer);
        answer.setVisibility(View.VISIBLE);
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.anim_bigger);
        answer.startAnimation(anim);
    }

    private void fonIfRight() {
        RelativeLayout fonAnim = (RelativeLayout) findViewById(R.id.correct_or_incorrect_answer);
        fonAnim.setVisibility(View.VISIBLE);
        fonAnim.setBackgroundColor(getResources().getColor(R.color.new_color_green_MAIN));
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        fonAnim.startAnimation(anim);
        fonAnim.setVisibility(View.INVISIBLE);
    }

    private void fonIfWrong() {
        RelativeLayout fonAnim = (RelativeLayout) findViewById(R.id.correct_or_incorrect_answer);
        fonAnim.setVisibility(View.VISIBLE);
        fonAnim.setBackgroundColor(getResources().getColor(R.color.colorRed));
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        fonAnim.startAnimation(anim);
        fonAnim.setVisibility(View.INVISIBLE);
    }

    public void onClickLesson3Answer1(View view) {
        if (rightButton == 1) {
            playRight();
            fonIfRight();
            rightQuantity++;
        } else {
            playWrong();
            fonIfWrong();
            wrongQuantity++;
        }
        showRightAnswer();
        waiting2();
    }

    public void onClickLesson3Answer2(View view) {
        if (rightButton == 2) {
            playRight();
            fonIfRight();
            rightQuantity++;
        } else {
            playWrong();
            fonIfWrong();
            wrongQuantity++;
        }
        showRightAnswer();
        waiting2();
    }

    private void checkTheEnd() {
        if (k < x.length) {
            setText();
            setButtonText();
            questionAnimationIn();
            buttonAnination();
        } else {
            showFinalDialog();
        }
    }

    private void showFinalDialog() {
        playLessonComplete();
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_lesson_complete, null);
        //AlertDialog
        String positiveButtonText = getResources().getString(R.string.tap_to_continue);
        AlertDialog.Builder builder = new AlertDialog.Builder(Lesson3Activity.this);
        builder.setCancelable(false)
                .setPositiveButton(positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Подключаем InterstitialAd
                                if (!Data.userData.isPremium()) {
                                    requestAdmobInterstitial();
                                } else {
                                    Continue();
                                }
                            }
                        });
        builder.setView(dialoglayout);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void waiting2() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                checkTheEnd();
            }
        }, 1000);
    }

    private void questionAnimationIn() {
        TextView questionImageIn = (TextView) findViewById(R.id.lesson_3_question);
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft1);
        questionImageIn.startAnimation(anim);
    }

    private void buttonAnination() {
        Button button1 = (Button) findViewById(R.id.lesson3Answer1);
        Button button2 = (Button) findViewById(R.id.lesson3Answer2);
        //Animation
        Animation anim1 = null;
        anim1 = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft2);
        Animation anim2 = null;
        anim2 = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft3);

        button1.startAnimation(anim1);
        button2.startAnimation(anim2);
    }

    private void playLessonComplete() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        mStreamId = mSoundPool.play(mSoundId, leftVolume, rightVolume, priority, no_loop,
                normal_playback_rate);
    }

    private void playRight() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        rStreamId = rSoundPool.play(rSoundId, leftVolume, rightVolume, priority, no_loop,
                normal_playback_rate);
    }

    private void playWrong() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        wStreamId = wSoundPool.play(wSoundId, leftVolume, rightVolume, priority, no_loop,
                normal_playback_rate);
    }

    private void Continue() {
        Intent intent = new Intent(this, AfterLessonActivity.class);
        intent.putExtra("Unit", unit);
        intent.putExtra("keyNumber", keyNumber);
        intent.putExtra("rightQuantity", rightQuantity);
        intent.putExtra("wrongQuantity", wrongQuantity);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("rightButton", rightButton);
        outState.putInt("rightQuantity", rightQuantity);
    }

    private void requestAdmobInterstitial() {
        if (mInterstitial.isLoaded()) {
            mInterstitial.show();
        } else {
            Continue();
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

    protected void onStart() {
        super.onStart();
        bHandler = new Handler();
        bHandler.post(myRunnable);
    }

    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(myRunnable);
        saveToFileData();
    }
}
