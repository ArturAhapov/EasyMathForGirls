package com.arturagapov.easymathforgirls.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Artur Agapov on 02.10.2016.
 */
public class IabBroadcastReceiver extends BroadcastReceiver {
    public interface IabBroadcastListener {
        void receivedBroadcast();
    }

    /**
     * The Intent action that this Receiver should filter for.
     */
    public static final String ACTION = "com.android.vending.billing.PURCHASES_UPDATED";

    private final IabBroadcastListener mListener;

    public IabBroadcastReceiver(IabBroadcastListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {
            mListener.receivedBroadcast();
        }
    }
}
