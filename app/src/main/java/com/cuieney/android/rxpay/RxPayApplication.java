package com.cuieney.android.rxpay;

import android.app.Application;

import com.cuieney.rxpay_annotation.WX;

@WX(packageName = "com.cuieney.android.rxpay")//微信支付注册keystore时候的包名
public class RxPayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
