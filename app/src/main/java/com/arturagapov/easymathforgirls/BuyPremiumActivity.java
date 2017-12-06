package com.arturagapov.easymathforgirls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.arturagapov.easymathforgirls.Util.*;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.*;

public class BuyPremiumActivity extends Activity implements IabBroadcastReceiver.IabBroadcastListener{
    //Serializable
    private static String fileNameData = "EasyMathData.ser";
    //Firebase EventLog
    private FirebaseAnalytics mFirebaseAnalytics;

    //Billing
    static final String TAG = "Easy Math";
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "premium";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_premium);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        readFromFileData();

        //Billing
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxqcUpChN4fyUkha1wd0g0ySAX0bhvy1sQMvwImW+oVVP7KdPuWnqB8nDn90suot7L4gkeUvyUKvDcO01i/O0SxFnycJpurNyogFRT94JTqWN3xjhIIkv2BnzJteDrJYeuZ7WMmdPpHHX7BdoQSvxmTF8lBmH5ehQ1+fuSedMZ47dhx0gyqcs7A4ChGTmf0dTC+BFchFrOQh/N+vVaGhrFmc/IlXhSdzlC9C7IwNLeDa0VCsVQoXxWL4QK2IZxkRaooUnG0AwFnxiNhhQBOQWlv1+ljA+prjtC1iQGJeeta0thLL/IVINbvuYIItTiUth0A+cZfOYReoDxk/UUQylXwIDAQAB";
        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) return;
                mBroadcastReceiver = new IabBroadcastReceiver(BuyPremiumActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }
    private void updateUi() {
        if (mIsPremium) {
            Data.userData.setPremium(true);
            eventLogsPurchaseEnd();
            saveToFileData();
        }
    }

    private void reUpdateUi() {
        if (mIsPremium) {
            Data.userData.setPremium(true);
            saveToFileData();
        }
    }

    private void eventLogsPurchaseStart(){
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, bundle);
    }
    private void eventLogsPurchaseEnd(){
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
    }

    public void onClickGoBack(View view) {
        Intent intent = new Intent(this, Activity_1st.class);
        startActivity(intent);
    }

    //Billing
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            reUpdateUi();
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }
    public void onClickPremium(View view) {
        Log.d(TAG, "Premium button clicked.");
        eventLogsPurchaseStart();
        String payload = "Amsterdam87";

        try {
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        if (payload == "Amsterdam87" || payload.equals("Amsterdam87")) {
            return true;
        } else {
            return false;
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }
            Log.d(TAG, "Purchase successful.");
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
                updateUi();
            }
        }
    };






    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
    // Serializes an object and saves it to a file
    private void saveToFileData() {
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
    private Data readFromFileData() {
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
}
