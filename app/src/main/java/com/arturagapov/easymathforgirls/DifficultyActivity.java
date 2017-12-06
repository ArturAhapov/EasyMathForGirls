package com.arturagapov.easymathforgirls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.*;

public class DifficultyActivity extends Activity {
    //Serializable
    private static String fileNameData = "EasyMathData.ser";
    public static String unit;
    //Firebase EventLog
    private FirebaseAnalytics mFirebaseAnalytics;
    //Подключаем звуки
    private SoundPool wwSoundPool;
    private int wwSoundId = 1;
    private int wwStreamId;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //Восстанавливаем данные из сохраненного файла
        readFromFileData();

        //Подключаем звуки
        try {
            wwSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            wwSoundPool.load(this, R.raw.notif2, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Получаем интент
        Intent intent = getIntent();
        unit = intent.getStringExtra("Unit");
        updateUi();
    }
    private void updateUi(){
        if(Data.userData.isPremium()){
            final Intent intent = new Intent(this, QuizActivity.class);
            Button buttonMedium = (Button)findViewById(R.id.button_medium);
            Button buttonAdvanced = (Button)findViewById(R.id.button_advanced);
            buttonMedium.setBackground(getResources().getDrawable(R.drawable.buttonroundedblue));
            buttonAdvanced.setBackground(getResources().getDrawable(R.drawable.buttonroundedred));

            buttonMedium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventLogs("MathDifficulty", "Intermediate");
                    intent.putExtra("mathAction", unit);
                    intent.putExtra(QuizActivity.EXTRA_MESSAGE_DIFFICULTY,"Intermediate");
                    startActivity(intent);
                }
            });
            buttonAdvanced.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventLogs("MathDifficulty", "Advanced");
                    intent.putExtra("mathAction", unit);
                    intent.putExtra(QuizActivity.EXTRA_MESSAGE_DIFFICULTY,"Advanced");
                    startActivity(intent);
                }
            });
        }
    }

    private void eventLogs(String id, String name){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void onClickEasy(View view) {
        eventLogs("MathDifficulty", "Easy");
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("mathAction", unit);
        intent.putExtra(QuizActivity.EXTRA_MESSAGE_DIFFICULTY,"Easy");
        startActivity(intent);
    }

    public void onClickMedium(View view) {
        continueToMarket();
    }

    public void onClickAdvanced(View view) {
        continueToMarket();
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

    private void eventLogsGetRpo(){
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE_REFUND, bundle);
    }

    private void continueToMarket(){
        playCongrats();
        eventLogsGetRpo();
        Intent intent = new Intent(this, BuyPremiumActivity.class);
        startActivity(intent);
    }

    // Serializes an object and saves it to a file
    private void saveToFileData(){//Context context) {
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
    private void readFromFileData(){//Context context) {
        try {
            FileInputStream fileInputStream = openFileInput(fileNameData);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Data.userData = (Data) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readFromFileData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveToFileData();
    }
}