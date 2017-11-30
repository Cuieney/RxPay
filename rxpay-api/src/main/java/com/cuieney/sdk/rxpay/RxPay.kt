package com.cuieney.sdk.rxpay

import android.app.Activity


import com.cuieney.sdk.rxpay.alipay.AlipayWay
import com.cuieney.sdk.rxpay.wechatpay.WXPayWay

import org.json.JSONObject

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.annotations.NonNull

/**
 * Created by cuieney on 18/08/2017.
 */

class RxPay(@param:NonNull private val activity: Activity) {

    fun requestAlipay(@NonNull orderInfo: String): Flowable<Boolean> {
        return aliPayment(orderInfo)
    }


    fun requestWXpay(@NonNull json: JSONObject): Flowable<Boolean> {
        return wxPayment(json)
    }


    private fun ensure(payWay: PayWay, orderInfo: String?, json: JSONObject?): FlowableTransformer<Any, Boolean> {
        return FlowableTransformer {
            if (payWay === PayWay.WECHATPAY) {
                requestImplementation(json).map { paymentStatus -> paymentStatus.isStatus }
            } else requestImplementation(orderInfo).map { paymentStatus -> paymentStatus.isStatus }
        }
    }

    private fun requestImplementation(json: JSONObject?): Flowable<PaymentStatus> {
        return WXPayWay.payMoney(activity, json!!)
    }

    private fun requestImplementation(orderInfo: String?): Flowable<PaymentStatus> {
        return AlipayWay.payMoney(activity, orderInfo!!)
    }

    private fun aliPayment(orderInfo: String): Flowable<Boolean> {
        return Flowable.just(orderInfo).compose(ensure(PayWay.ALIPAY, orderInfo, null))
    }

    private fun wxPayment(json: JSONObject): Flowable<Boolean> {
        return Flowable.just(json).compose(ensure(PayWay.WECHATPAY, null, json))
    }

    companion object {
        internal val TAG = "RxPay"
    }

}
