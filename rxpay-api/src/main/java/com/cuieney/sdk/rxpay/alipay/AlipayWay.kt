package com.cuieney.sdk.rxpay.alipay

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log

import com.alipay.sdk.app.PayTask
import com.cuieney.sdk.rxpay.PaymentStatus
import java.util.Observable

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by cuieney on 18/08/2017.
 */
object AlipayWay {

    fun payMoney(activity: Activity, orderInfo: String): Flowable<PaymentStatus> {
        return Flowable.create(FlowableOnSubscribe<PayTask> { e ->
            val alipay = PayTask(activity)
            e.onNext(alipay)
        }, BackpressureStrategy.ERROR)
                .map { payTask ->
                    createPaymentStatus(payTask, orderInfo)
                }
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())

    }


    private fun createPaymentStatus(payTask: PayTask, orderInfo: String): PaymentStatus {
        val result = payTask.payV2(orderInfo, true)
        val payResult = PayResult(result)
        val resultStatus = payResult.resultStatus
        return if (resultStatus.equals("9000")) {
            PaymentStatus(true)
        } else {
            Log.e("Rxpay","${payResult.resultStatus},${payResult.result}")
            PaymentStatus(false)
        }
    }

}
