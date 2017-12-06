package com.arturagapov.easymathforgirls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.arturagapov.easymathforgirls.MathActiveChoice.QuizDivision;
import com.arturagapov.easymathforgirls.MathActiveChoice.QuizMinus;
import com.arturagapov.easymathforgirls.MathActiveChoice.QuizMultiply;
import com.arturagapov.easymathforgirls.MathActiveChoice.QuizPlus;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import java.io.*;


public class QuizActivity extends Activity implements InterstitialAdListener {
    //Serializable
    private static String fileNameData = "EasyMathData.ser";
    //Подключаем звуки
    private SoundPool mSoundPool;
    private SoundPool rSoundPool;
    private SoundPool wSoundPool;
    private int mSoundId = 1;
    private int rSoundId = 1;
    private int wSoundId = 1;
    private int mStreamId;
    private int rStreamId;
    private int wStreamId;
    //Set timer
    private int seconds = 0;
    private boolean running;
    //Задержка времени
    private Handler mHandler = new Handler();

    private int k = 0;
    //ProgressBar
    private int progress = 0;
    private int maxProgress = 12;
    private ProgressBar pbHorizontal;

    //Считаем кнопки
    private boolean count1 = false;
    private boolean count2 = false;
    private boolean count3 = false;
    private boolean count4 = false;

    //Переданный интент
    private String unit;
    public static final String EXTRA_MESSAGE_DIFFICULTY = null;

    //Переменные сложности и мат дейстия
    String diffucultyString;
    private int difficulty = 1;

    //Переменные для отображения в активности
    private String questionText;
    private int rightButton = 0;
    private int rightAnswer;
    private int first;
    private int second;
    private int wrongtAnswer1;
    private int wrongtAnswer2;
    private int wrongtAnswer3;
    //Переменные сохранения Bundle
    private int rightQuantity = 0;
    private int wrongQuantity = 0;
    private int currentScore = 0;

    //Реклама
    //Interstitial by Facebook
    private InterstitialAd interstitialAd;
    //Interstitial by Admob
    private com.google.android.gms.ads.InterstitialAd mInterstitial;
    AdRequest mInterstitialAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Восстановка переменных из Bundle
        if (savedInstanceState != null) {
            rightButton = savedInstanceState.getInt("rightButton");
            seconds = savedInstanceState.getInt("time");
            currentScore = savedInstanceState.getInt("score");
            wrongQuantity = savedInstanceState.getInt("wrongs");
            rightQuantity = savedInstanceState.getInt("rights");
        }

        //ProgressBar
        pbHorizontal = (ProgressBar) findViewById(R.id.practiceprogressBar);
        pbHorizontal.setMax(maxProgress);

        setInterstitial();

        //Получаем интент
        Intent intent = getIntent();
        unit = intent.getStringExtra("mathAction");
        diffucultyString = intent.getStringExtra(EXTRA_MESSAGE_DIFFICULTY);
        setDifficultyInt(diffucultyString);
        setTopText();

        //Подключаем звуки
        try {
            mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            mSoundPool.load(this, R.raw.complet3, 1);
            rSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            rSoundPool.load(this, R.raw.rightanswer, 1);
            wSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            wSoundPool.load(this, R.raw.oops, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        running = true;
        runTimer();
        checkForEnd();
    }

    private void setInterstitial() {
        if (!Data.userData.isPremium()) {
            //Подключаем InterstitialAd by Admob
            mInterstitial = new com.google.android.gms.ads.InterstitialAd(this);
            mInterstitial.setAdUnitId("ca-app-pub-1399393260153583/1363642355");
            mInterstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    continueToAfterQuiz();
                    //super.onAdClosed();
                }
            });
            mInterstitialAdRequest = new AdRequest.Builder().build();
            //Подключаем InterstitialAd by Facebook
            try {
                mInterstitial.loadAd(mInterstitialAdRequest);
                //Подключаем Interstitial by Facebook
                loadInterstitialAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDifficultyInt(String howLevelHard) {
        if (howLevelHard.equals("Easy")) {
            difficulty = 1;
        }
        if (howLevelHard.equals("Intermediate")) {
            difficulty = 2;
        }
        if (howLevelHard.equals("Advanced")) {
            difficulty = 3;
            TextView question = (TextView) findViewById(R.id.quiz_question);
            TextView answer = (TextView) findViewById(R.id.quiz_answer);
            question.setTextSize(getResources().getDimension(R.dimen.titleText));
            answer.setTextSize(getResources().getDimension(R.dimen.titleText));
        }
    }

    private void runTimer() {
        final TextView timer = (TextView) findViewById(R.id.timer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format("%02d:%02d", minutes, secs);
                timer.setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setTopText() {
        TextView mathAction = (TextView) findViewById(R.id.quiz_math_action);
        TextView mathDifficulty = (TextView) findViewById(R.id.quiz_difficulty);

        if (unit.equals("Plus")) {
            mathAction.setText(R.string.button_plus);
        }
        if (unit.equals("Minus")) {
            mathAction.setText(R.string.button_minus);
        }
        if (unit.equals("Multiply")) {
            mathAction.setText(R.string.button_multiply);
        }
        if (unit.equals("Division")) {
            mathAction.setText(R.string.button_division);
        }
        if (diffucultyString.equals("Easy")) {
            mathDifficulty.setText(R.string.level_easy);
        }
        if (diffucultyString.equals("Intermediate")) {
            mathDifficulty.setText(R.string.level_mediume);
        }
        if (diffucultyString.equals("Advanced")) {
            mathDifficulty.setText(R.string.level_hard);
        }
    }

    //ProgressBar
    private void postProgress(int progress) {
        pbHorizontal.setProgress(progress);
        pbHorizontal.setSecondaryProgress(progress + 1);
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

    public void checkForEnd() {
        if (k < maxProgress) {
            k = k + 1;
            progress = k;
            postProgress(progress);
            startQuizClass();
            questionAnimationIn();
            buttonAnimation();
        } else {
            running = false;
            showFinalDialog();
        }
    }

    private void showFinalDialog() {
        playLessonComplete();
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_lesson_complete, null);
        //AlertDialog
        String positiveButtonText = getResources().getString(R.string.tap_to_continue);
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setCancelable(false)
                .setPositiveButton(positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Подключаем InterstitialAd by Facebook
                                if ((!Data.userData.isPremium())&& (Data.userData.getSessionCount() > 2)) {
                                    if (interstitialAd.isAdLoaded()) {
                                        interstitialAd.show();
                                    } else {
                                        requestAdmobInterstitial();
                                    }
                                } else {
                                    continueToAfterQuiz();
                                }
                            }
                        });
        builder.setView(dialoglayout);

        RelativeLayout topText = (RelativeLayout) dialoglayout.findViewById(R.id.dialog_layout);
        ImageView finalSmile = (ImageView) dialoglayout.findViewById(R.id.image_smile);
        int sm = (int) (Math.random() * 10);
        if (sm < 3) {
            finalSmile.setImageResource(R.drawable.smile_happy_2);
        } else if (sm < 6) {
            finalSmile.setImageResource(R.drawable.smile_happy_3);
        } else {
            finalSmile.setImageResource(R.drawable.smile_happy_1);
        }
        TextView dialogText = (TextView) dialoglayout.findViewById(R.id.dialog_text);

        dialogText.setText(R.string.unit_complete);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startQuizClass() {
        if (unit.equals("Plus")) {
            plusQuiz(difficulty);
        }
        if (unit.equals("Minus")) {
            minusQuiz(difficulty);
        }
        if (unit.equals("Multiply")) {
            multiplyQuiz(difficulty);
        }
        if (unit.equals("Division")) {
            divisionQuiz(difficulty);
        }
    }

    private void plusQuiz(int level) {

        QuizPlus quizPlus = new QuizPlus();
        quizPlus.setLevel(level);
        quizPlus.generation();
        first = quizPlus.getFirstNumber();
        second = quizPlus.getSecondNumber();
        questionText = (" " + first + " + " + second + " = ");
        rightAnswer = quizPlus.getRightAnswer();
        wrongtAnswer1 = quizPlus.getWrongtAnswer1();
        wrongtAnswer2 = quizPlus.getWrongtAnswer2();
        wrongtAnswer3 = quizPlus.getWrongtAnswer3();
        setQuestionText();
        setButtonText();
    }

    private void minusQuiz(int level) {
        QuizMinus quizMinus = new QuizMinus();
        quizMinus.setLevel(level);
        quizMinus.generation();
        first = quizMinus.getFirstNumber();
        second = quizMinus.getSecondNumber();
        questionText = (" " + first + " - " + second + " = ");
        rightAnswer = quizMinus.getRightAnswer();
        wrongtAnswer1 = quizMinus.getWrongtAnswer1();
        wrongtAnswer2 = quizMinus.getWrongtAnswer2();
        wrongtAnswer3 = quizMinus.getWrongtAnswer3();
        setQuestionText();
        setButtonText();
    }

    private void multiplyQuiz(int level) {
        QuizMultiply quizMultiply = new QuizMultiply();
        quizMultiply.setLevel(level);
        quizMultiply.generation();
        first = quizMultiply.getFirstNumber();
        second = quizMultiply.getSecondNumber();
        questionText = (" " + first + " * " + second + " = ");
        rightAnswer = quizMultiply.getRightAnswer();
        wrongtAnswer1 = quizMultiply.getWrongtAnswer1();
        wrongtAnswer2 = quizMultiply.getWrongtAnswer2();
        wrongtAnswer3 = quizMultiply.getWrongtAnswer3();
        setQuestionText();
        setButtonText();
    }

    private void divisionQuiz(int level) {
        QuizDivision quizDivision = new QuizDivision();
        quizDivision.setLevel(level);
        quizDivision.generation();
        first = quizDivision.getFirstNumber();
        second = quizDivision.getSecondNumber();
        questionText = (" " + first + " / " + second + " = ");
        rightAnswer = quizDivision.getRightAnswer();
        wrongtAnswer1 = quizDivision.getWrongtAnswer1();
        wrongtAnswer2 = quizDivision.getWrongtAnswer2();
        wrongtAnswer3 = quizDivision.getWrongtAnswer3();
        setQuestionText();
        setButtonText();
    }

    private void setQuestionText() {
        //Устанавливаем текст вопроса
        TextView question = (TextView) findViewById(R.id.quiz_question);
        question.setText("" + questionText);

        TextView answer = (TextView) findViewById(R.id.quiz_answer);
        answer.setVisibility(View.INVISIBLE);
        answer.setText("" + rightAnswer);
    }

    private void setButtonText() {
        //Устанавливаем текст кнопок
        Button button1 = (Button) findViewById(R.id.quizbutton1);
        Button button2 = (Button) findViewById(R.id.quizbutton2);
        Button button3 = (Button) findViewById(R.id.quizbutton3);
        Button button4 = (Button) findViewById(R.id.quizbutton4);

        //Обнуляем rightButton
        rightButton = 0;

        do {
            int x = (int) (Math.random() * 100);
            if (x < 25 & !count1) {
                button1.setText("" + rightAnswer);
                button2.setText("" + wrongtAnswer1);
                button3.setText("" + wrongtAnswer2);
                button4.setText("" + wrongtAnswer3);
                rightButton = 1;
                count1 = true;
                count2 = false;
                count3 = false;
                count4 = false;
            } else if (x < 50 & !count2) {
                button2.setText("" + rightAnswer);
                button1.setText("" + wrongtAnswer1);
                button3.setText("" + wrongtAnswer2);
                button4.setText("" + wrongtAnswer3);
                rightButton = 2;
                count1 = false;
                count2 = true;
                count3 = false;
                count4 = false;
            } else if (x < 75 & !count3) {
                button3.setText("" + rightAnswer);
                button1.setText("" + wrongtAnswer1);
                button2.setText("" + wrongtAnswer2);
                button4.setText("" + wrongtAnswer3);
                rightButton = 3;
                count1 = false;
                count2 = false;
                count3 = true;
                count4 = false;
            } else if (!count4) {
                button4.setText("" + rightAnswer);
                button1.setText("" + wrongtAnswer1);
                button2.setText("" + wrongtAnswer2);
                button3.setText("" + wrongtAnswer3);
                rightButton = 4;
                count1 = false;
                count2 = false;
                count3 = false;
                count4 = true;
            }
        } while (rightButton == 0);
    }

    private void setCurrentScore() {
        currentScore = currentScore + first + second;
        TextView score = (TextView) findViewById(R.id.score_is);
        score.setText("" + currentScore);
    }

    public void onClickQButton1(View view) {
        if (rightButton == 1) {
            playRight();
            fonIfRight();
            rightQuantity++;
            setCurrentScore();
        } else {
            playWrong();
            fonIfWrong();
            wrongQuantity++;
        }
        showRightAnswer();
        waiting2();
    }

    public void onClickQButton2(View view) {
        if (rightButton == 2) {
            playRight();
            fonIfRight();
            rightQuantity++;
            setCurrentScore();
        } else {
            playWrong();
            fonIfWrong();
            wrongQuantity++;
        }
        showRightAnswer();
        waiting2();
    }

    public void onClickQButton3(View view) {
        if (rightButton == 3) {
            playRight();
            rightQuantity++;
            fonIfRight();
            setCurrentScore();
        } else {
            playWrong();
            fonIfWrong();
            wrongQuantity++;
        }
        showRightAnswer();
        waiting2();
    }

    public void onClickQButton4(View view) {
        if (rightButton == 4) {
            playRight();
            rightQuantity++;
            fonIfRight();
            setCurrentScore();
        } else {
            playWrong();
            fonIfWrong();
            wrongQuantity++;
        }
        showRightAnswer();
        waiting2();
    }

    private void showRightAnswer() {
        TextView answer = (TextView) findViewById(R.id.quiz_answer);
        answer.setVisibility(View.VISIBLE);
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.anim_bigger);
        answer.startAnimation(anim);
    }

    private void fonIfRight() {
        RelativeLayout fonAnim = (RelativeLayout) findViewById(R.id.quiz_correct_or_incorrect_answer);
        fonAnim.setVisibility(View.VISIBLE);
        fonAnim.setBackgroundColor(getResources().getColor(R.color.new_color_green_MAIN));
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        fonAnim.startAnimation(anim);
        fonAnim.setVisibility(View.INVISIBLE);
    }

    private void fonIfWrong() {
        RelativeLayout fonAnim = (RelativeLayout) findViewById(R.id.quiz_correct_or_incorrect_answer);
        fonAnim.setVisibility(View.VISIBLE);
        fonAnim.setBackgroundColor(getResources().getColor(R.color.colorRed));
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        fonAnim.startAnimation(anim);
        fonAnim.setVisibility(View.INVISIBLE);
    }

    private void questionAnimationIn() {
        TextView questionImageIn = (TextView) findViewById(R.id.quiz_question);
        //Animation
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft1);
        questionImageIn.startAnimation(anim);
    }

    private void buttonAnimation() {
        Button button1 = (Button) findViewById(R.id.quizbutton1);
        Button button2 = (Button) findViewById(R.id.quizbutton2);
        Button button3 = (Button) findViewById(R.id.quizbutton3);
        Button button4 = (Button) findViewById(R.id.quizbutton4);
        //Animation
        Animation anim1 = null;
        anim1 = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft1);
        Animation anim2 = null;
        anim2 = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft2);
        Animation anim3 = null;
        anim3 = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft3);
        Animation anim4 = null;
        anim4 = AnimationUtils.loadAnimation(this, R.anim.level_activity_righttoleft4);

        button1.startAnimation(anim1);
        button2.startAnimation(anim2);
        button3.startAnimation(anim3);
        button4.startAnimation(anim4);
    }

    private void waiting2() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                checkForEnd();
            }
        }, 1000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("seconds", seconds);
        outState.putInt("rights", rightQuantity);
        outState.putInt("wrongs", wrongQuantity);
        outState.putBoolean("running", running);
        outState.putInt("Score", currentScore);
        outState.putInt("rightButton", rightButton);
    }

    private void continueToAfterQuiz() {
        Intent intent = new Intent(QuizActivity.this, AfterQuizActivity.class);
        intent.putExtra("Unit", unit);
        intent.putExtra(AfterQuizActivity.EXTRA_MESSAGE_DIFFICULTY, diffucultyString);
        intent.putExtra("time", seconds);
        intent.putExtra("score", currentScore);
        intent.putExtra("wrongs", wrongQuantity);
        intent.putExtra("rights", rightQuantity);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        running = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        running = true;
        super.onResume();
    }

    //Реклама
    //Подключаем Interstitial by Facebook
    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this, "343660272660579_343663905993549");
        interstitialAd.setAdListener(QuizActivity.this);
        interstitialAd.loadAd();
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        continueToAfterQuiz();
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        //Если рекламы для отображения нет, вызывается метод onError, в котором error.code имеет значение 1001. Если вы используете собственную индивидуальную службу отчетов или промежуточную платформу, возможно, вам понадобится проверить значение кода, чтобы обнаружить подобные случаи. В этой ситуации вы можете перейти на другую рекламную сеть, но не отправляйте сразу же после этого повторный запрос на получение рекламы.
        //requestAdmobInterstitial();
    }

    @Override
    public void onAdLoaded(Ad ad) {
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
    //конец реализации Interstitial by Facebook

    private void requestAdmobInterstitial() {
        if (mInterstitial.isLoaded()) {
            mInterstitial.show();
        } else {
            continueToAfterQuiz();
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

    @Override
    protected void onStop() {
        super.onStop();
        saveToFileData();
    }
}