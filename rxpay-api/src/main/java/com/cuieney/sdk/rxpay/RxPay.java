package com.cuieney.sdk.rxpay;

import android.app.Activity;


import com.cuieney.sdk.rxpay.alipay.AlipayWay;
import com.cuieney.sdk.rxpay.wechatpay.WXPayWay;

import org.json.JSONObject;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by cuieney on 18/08/2017.
 */

public class RxPay {
    static final String TAG = "RxPay";
    private Activity activity;

    public RxPay(@NonNull Activity activity) {
        this.activity = activity;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public Flowable<Boolean> requestAlipay(@NonNull final String orderInfo){
        return aliPayment(orderInfo);
    }


    @SuppressWarnings({"WeakerAccess", "unused"})
    public Flowable<Boolean> requestWXpay(@NonNull final JSONObject json){
        return wxPayment(json);
    }


    @SuppressWarnings("WeakerAccess")
    private FlowableTransformer<Object,Boolean> ensure(final PayWay payWay, final String orderInfo, final JSONObject json){
        return new FlowableTransformer<Object, Boolean>() {
            @Override
            public Publisher<Boolean> apply(Flowable<Object> upstream) {
                if (payWay == PayWay.WECHATPAY) {
                    return requestImplementation(json).map(new Function<PaymentStatus, Boolean>() {
                        @Override
                        public Boolean apply(PaymentStatus paymentStatus) throws Exception {
                            return paymentStatus.isStatus();
                        }
                    });
                }
                return requestImplementation(orderInfo).map(new Function<PaymentStatus, Boolean>() {
                    @Override
                    public Boolean apply(PaymentStatus paymentStatus) throws Exception {
                        return paymentStatus.isStatus();
                    }
                });
            }
        };
    }

    private Flowable<PaymentStatus> requestImplementation(final JSONObject json){
        return WXPayWay.payMoney(activity,json);
    }

    private Flowable<PaymentStatus> requestImplementation(final String orderInfo){
        return AlipayWay.payMoney(activity, orderInfo);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    private Flowable<Boolean> aliPayment(final String orderInfo){
        return Flowable.just(orderInfo).compose(ensure(PayWay.ALIPAY,orderInfo,null));
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    private Flowable<Boolean> wxPayment(final JSONObject json){
        return Flowable.just(json).compose(ensure(PayWay.WECHATPAY,null,json));
    }

}
