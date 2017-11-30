package com.cuieney.sdk.rxpay.wechatpay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.cuieney.sdk.rxpay.PaymentStatus;
import com.cuieney.sdk.rxpay.RxBus;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * wechat Method of payment
 */
public class WXPayWay {
    private static final String PARTNER_ID = "partnerId";
    private static final String NONCE_STR = "nonceStr";
    private static final String TIME_STAMP = "timeStamp";
    private  static final String SIGN = "sign";
    private static final String META_WX_APPID = "WX_APPID";
    private static final String META_PARTNER_ID = "PARTNER_ID";
    private static final String META_API_KEY = "API_KEY";


    public static Flowable<PaymentStatus> payMoney(final Activity context, final JSONObject json) {

        return Flowable.create(new FlowableOnSubscribe<PaymentStatus>() {
            @Override
            public void subscribe(final FlowableEmitter<PaymentStatus> e) throws Exception {
                final String appId = getMetaData(context, META_WX_APPID);
                final IWXAPI api = WXAPIFactory.createWXAPI(context, appId);
                api.registerApp(appId);
                PayReq req = new PayReq();

                req.appId = appId;
                setValue(req,PARTNER_ID,json.optString("partnerId"),context);
                req.prepayId = json.optString("prepayId");
                setValue(req,NONCE_STR,json.optString("nonceStr"),context);
                setValue(req,TIME_STAMP,json.optString("timeStamp"),context);
                req.packageValue = json.optString("packageValue", "Sign=WXPay");
                setValue(req,SIGN,json.optString("sign"),context);
                req.extData = "app data";

                boolean sendReq = api.sendReq(req);
                if (!sendReq) {
                    e.onNext(new PaymentStatus(false));
                    e.onComplete();
                } else {
                    RxBus.getDefault().toFlowable(PaymentStatus.class)
                            .subscribe(new Consumer<PaymentStatus>() {
                                @Override
                                public void accept(PaymentStatus paymentStatus) throws Exception {
                                    e.onNext(paymentStatus);
                                    e.onComplete();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    e.onNext(new PaymentStatus(false));
                                    e.onComplete();
                                }
                            });
                }


            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());


    }

    public static String getMetaData(Activity context, String metaData) {
        ApplicationInfo info = null;
        try {
            info = context.getApplication().getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Object data = info.metaData.get(metaData);
            if (data == null) {
                throw new NullPointerException(metaData + " field cannot be empty");
            }
            return String.valueOf(data);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void setValue(PayReq req, String value, String rawValue, Activity context) {
        String configValue = rawValue;
        switch (value) {
            case PARTNER_ID:
                if (configValue.isEmpty()) {
                    configValue = getMetaData(context, META_PARTNER_ID);
                }
                req.partnerId = configValue;
                break;
            case NONCE_STR:
                if (configValue.isEmpty()) {
                    configValue = genNonceStr();
                }

                req.nonceStr = configValue;
                break;
            case TIME_STAMP:
                if (configValue.isEmpty()) {
                    configValue =  genTimeStamp();
                }
                req.timeStamp = configValue;
                break;
            case SIGN:
                if (configValue.isEmpty()) {
                    configValue =  genAppSign(req,getMetaData(context, META_API_KEY));
                }
                req.sign = configValue;
                break;
            default:break;
        }
    }

    private static String genNonceStr() {
        Random random = new Random();
        return getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }


    private static String genAppSign(PayReq payReq, String apiKey) {
        LinkedList<NameValuePair> params = new LinkedList<>();
        params.add(new NameValuePair("appid", payReq.appId));
        params.add(new NameValuePair("noncestr", payReq.nonceStr));
        params.add(new NameValuePair("package", payReq.packageValue));
        params.add(new NameValuePair("partnerid", payReq.partnerId));
        params.add(new NameValuePair("prepayid", payReq.prepayId));
        params.add(new NameValuePair("timestamp", payReq.timeStamp));


        StringBuilder tempSb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            tempSb.append(params.get(i).getName());
            tempSb.append('=');
            tempSb.append(params.get(i).getValue());
            tempSb.append('&');
        }
        tempSb.append("key=");
        tempSb.append(apiKey);
        return getMessageDigest(tempSb.toString().getBytes()).toUpperCase();
    }

    private final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    private static String genTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
}
