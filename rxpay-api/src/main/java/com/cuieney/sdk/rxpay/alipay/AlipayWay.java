package com.cuieney.sdk.rxpay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.cuieney.sdk.rxpay.PaymentStatus;

import java.util.Map;
import java.util.Observable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by cuieney on 18/08/2017.
 */
public class AlipayWay {

    public static  Flowable<PaymentStatus> payMoney(final Activity activity, final String orderInfo) {
        return Flowable.create(new FlowableOnSubscribe<PayTask>() {
            @Override
            public void subscribe(FlowableEmitter<PayTask> e) throws Exception {
                PayTask alipay = new PayTask(activity);
                e.onNext(alipay);
            }
        }, BackpressureStrategy.ERROR)
                .map(new Function<PayTask, PaymentStatus>() {
                    @Override
                    public PaymentStatus apply(PayTask payTask) throws Exception {
                        return createPaymentStatus(payTask,orderInfo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());

    }


    private static PaymentStatus createPaymentStatus(PayTask payTask,String orderInfo){
        Map<String, String> result = payTask.payV2(orderInfo, true);
        PayResult payResult = new PayResult(result);
        String resultStatus = payResult.getResultStatus();
        if (TextUtils.equals(resultStatus, "9000")) {
            return new PaymentStatus(true);
        } else {
            return new PaymentStatus(false);
        }
    }

}
