package com.arturagapov.easymathforgirls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

public class MoreAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.more_apps));
        ab.setDisplayHomeAsUpEnabled(true);

        setAds();
    }


    public void onClickOxford(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.arturagapov.english3000"));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.arturagapov.english3000")));
        }
    }

    public void onClickGeoQuiz(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.arturagapov.geoquiz"));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.arturagapov.geoquiz")));
        }
    }

    public void onClickEasyMath(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.arturagapov.easymath"));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.arturagapov.easymath")));
        }
    }

    private void setAds() {
        String ads = "ca-app-pub-1399393260153583/3222810126";

        if (!Data.userData.isPremium() && isOnline(this)) {
            final NativeExpressAdView adView = new NativeExpressAdView(MoreAppsActivity.this);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    final LinearLayout adLinLay = (LinearLayout) findViewById(R.id.adView_layout);
                    ViewGroup.LayoutParams layoutParams = adLinLay.getLayoutParams();
                    ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = 8;
                    ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = 8;
                    adLinLay.addView(adView);
                }
            });

            adView.setAdUnitId(ads);
            adView.setAdSize(new AdSize(344, 320));

            AdRequest requestNative = new AdRequest.Builder().build();
            adView.loadAd(requestNative);

        } else {
            LinearLayout adLinLay = (LinearLayout) findViewById(R.id.adView_layout);
            ViewGroup.LayoutParams layoutParams = adLinLay.getLayoutParams();

            layoutParams.height = 0;
        }
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
