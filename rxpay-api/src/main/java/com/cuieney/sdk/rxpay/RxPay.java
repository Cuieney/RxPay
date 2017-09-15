package com.cuieney.sdk.rxpay;

import android.app.Activity;


import com.cuieney.sdk.rxpay.alipay.AlipayWay;
import com.cuieney.sdk.rxpay.wechatpay.WXPayWay;

import org.json.JSONObject;

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

    /**
     *
     * @param orderInfo from server order information
     * @return
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Boolean> requestAlipay(@NonNull final String orderInfo){
        return aliPayment(orderInfo);
    }

    /**
     *
     * @param json from server order information
     * @return
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Boolean> requestWXpay(@NonNull final JSONObject json){
        return wxPayment(json);
    }


    @SuppressWarnings("WeakerAccess")
    private ObservableTransformer<Object,Boolean> ensure(final PayWay payWay, final String orderInfo,final JSONObject json){
        return new ObservableTransformer<Object, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(@NonNull Observable<Object> upstream) {
                if (payWay == PayWay.WECHATPAY) {
                 return  requestImplementation(json)
                         .map(new Function<PaymentStatus, Boolean>() {
                             @Override
                             public Boolean apply(@NonNull PaymentStatus paymentStatus) throws Exception {
                                 return paymentStatus.isStatus();
                             }
                         });
                }

                return requestImplementation(orderInfo)
                        .map(new Function<PaymentStatus, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull PaymentStatus paymentStatus) throws Exception {
                                return paymentStatus.isStatus();
                            }
                        });
            }

        };
    }

    private Observable<PaymentStatus> requestImplementation(final JSONObject json){
        WXPayWay.payMoney(activity,json);
        return  RxBus.getDefault().toObservable(PaymentStatus.class);
    }

    private Observable<PaymentStatus> requestImplementation(final String orderInfo){
        AlipayWay.payMoney(activity,orderInfo);
        return AlipayWay.getSubjects();
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    private Observable<Boolean> aliPayment(final String orderInfo){
        return Observable.just(orderInfo).compose(ensure(PayWay.ALIPAY,orderInfo,null));
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    private Observable<Boolean> wxPayment(final JSONObject json){
        return Observable.just(json).compose(ensure(PayWay.WECHATPAY,null,json));
    }



}
