package com.tsign.bluetooth;

import android.app.Application;
import android.content.Context;

import com.tsign.bluetooth.utlis.BluetoothUtil;

/**
 * @author 13001
 */
public class App extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        BluetoothUtil.getInstance().setContext(mContext);
    }

    public static Context getContext() {
        return mContext;
    }
}
